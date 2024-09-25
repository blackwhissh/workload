package com.blackwhissh.workload.service;

import com.blackwhissh.workload.dto.GiftDTO;
import com.blackwhissh.workload.entity.*;
import com.blackwhissh.workload.entity.enums.RequestStatusEnum;
import com.blackwhissh.workload.entity.enums.StatusEnum;
import com.blackwhissh.workload.exceptions.list.*;
import com.blackwhissh.workload.repository.*;
import com.blackwhissh.workload.utils.MapToDTOUtils;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

import static com.blackwhissh.workload.service.SwapService.checkHourStartAndEnd;

@Service
public class GiftService {
    private final static Logger LOGGER = LoggerFactory.getLogger(GiftService.class);
    private final GiftRepository giftRepository;
    private final EmployeeRepository employeeRepository;
    private final HourRepository hourRepository;
    private final ScheduleRepository scheduleRepository;
    private final SwapRepository swapRepository;
    private final HourService hourService;

    public GiftService(GiftRepository giftRepository, EmployeeRepository employeeRepository, HourRepository hourRepository, ScheduleRepository scheduleRepository, SwapRepository swapRepository, HourService hourService) {
        this.giftRepository = giftRepository;
        this.employeeRepository = employeeRepository;
        this.hourRepository = hourRepository;
        this.scheduleRepository = scheduleRepository;
        this.swapRepository = swapRepository;
        this.hourService = hourService;
    }
    @Transactional
    public GiftDTO publishGift(String publisherWorkId, LocalDate giftDate,
                               LocalTime start, LocalTime end,
                               Optional<String> receiverWorkId) {
        LOGGER.info("Started to publish gift");
        Schedule publisherSchedule = scheduleRepository
                .findByDateAndEmployee_WorkId(giftDate, publisherWorkId)
                .orElseThrow(ScheduleNotFoundException::new);
        if (publisherSchedule.getHours().isEmpty()) {
            throw new WrongHourAmountException();
        }
        hourService.checkIsBeforeRotation(publisherSchedule);

        List<Hour> hours = new ArrayList<>();

        for (Hour hour : publisherSchedule.getHours()) {
            checkHourStartAndEnd(start, end, hour, hours);
        }
        if (!hourService.checkHoursAmount(hours)) {
            throw new WrongHourAmountException();
        }
        Employee publisher = employeeRepository.findByWorkId(publisherWorkId)
                .orElseThrow(EmployeeNotFoundException::new);

        if (!hourService.checkFirstOrLastHour(hours)){
            throw new FirstOrLastHourTransferException();
        }

        if (!hourService.checkHoursChain(hours)) {
            throw new HoursAreNotChainedException();
        }

        List<Hour> hourList = new ArrayList<>();
        if (!hourService.isValidMinimumLimit(hours.get(0), hours.size())) {
            LOGGER.error("Can not be gifted, monthly hours will be less than 40");
            throw new MonthlyMinimumLimitException();
        }
        checkAndSetGift(hours, hourList);

        Gift gift = new Gift(
                publisher,
                hourList,
                giftDate,
                LocalDate.now(),
                RequestStatusEnum.ACTIVE
        );

        receiverWorkId.ifPresent(id -> {
            Employee receiver = employeeRepository.findByWorkId(id)
                    .orElseThrow(EmployeeNotFoundException::new);
            gift.setReceiver(receiver);
        });
        giftRepository.save(gift);
        LOGGER.info("Gift saved successfully");
        return MapToDTOUtils.mapGiftToDTO(gift);
    }

    private void checkAndSetGift(List<Hour> hours, List<Hour> hourList) {
        for (Hour hour : hours) {
            if (hour.getGiftExists() || hour.getSwapExists()) {
                LOGGER.error("One or more hour is already being gifted/swapped");
                throw new HourGiftException();
            }
            if (hour.getRotationItem() != null) {
                LOGGER.error("Hour is already in rotation");
                throw new HourIsInRotationException();
            }
            hour.setGiftExists(true);
            hourList.add(hour);
        }
    }

    public List<GiftDTO> getAllActiveGifts() {
        LOGGER.info("Started search for all active gifts");
        List<Gift> allActive = giftRepository.findAll()
                .stream().filter(gift -> gift.getStatus().equals(RequestStatusEnum.ACTIVE)).toList();
        allActive = allActive.stream().filter(gift -> gift.getPublisher().getUser().getActive()).toList();
        List<GiftDTO> allActiveDTO = new ArrayList<>();
        allActive.forEach(gift -> allActiveDTO.add(MapToDTOUtils.mapGiftToDTO(gift)));
        return allActiveDTO;
    }

    public List<GiftDTO> getAllGiftsByStatus(RequestStatusEnum statusEnum) {
        LOGGER.info("Started search for all gifts with type: " + statusEnum.name());
        List<Gift> allByStatus = giftRepository.findAll()
                .stream().filter(gift -> gift.getStatus().equals(statusEnum)).toList();
        allByStatus = allByStatus.stream().filter(gift -> gift.getPublisher().getUser().getActive()).toList();
        List<GiftDTO> allDTO = new ArrayList<>();
        allByStatus.forEach(gift -> allDTO.add(MapToDTOUtils.mapGiftToDTO(gift)));
        return allDTO;
    }

    public List<GiftDTO> getAllGifts() {
        LOGGER.info("Started search for all gifts");
        List<Gift> allGifts = giftRepository.findAll();
        allGifts = allGifts.stream().filter(gift -> gift.getPublisher().getUser().getActive()).toList();
        List<GiftDTO> allGiftsDTO = new ArrayList<>();
        allGifts.forEach(gift -> allGiftsDTO.add(MapToDTOUtils.mapGiftToDTO(gift)));
        return allGiftsDTO;
    }

    public boolean deleteCurrentUserGift(String workId, int giftId) {
        LOGGER.info("Started deleting current user gift with ID: " + giftId);
        Gift gift = giftRepository.findByPublisher_WorkIdAndGiftId(workId, giftId).orElseThrow();
        if (gift.getStatus().equals(RequestStatusEnum.ACTIVE)) {
            gift.getHours().forEach(hour -> hour.setGiftExists(false));
            giftRepository.delete(gift);
            return true;
        }
        return false;
    }

    @Transactional
    public boolean receiveGift(String receiverWorkId, int giftId) {
        LOGGER.info("Started receiving gift");
        Gift gift = giftRepository.findByGiftIdAndStatus(giftId, RequestStatusEnum.ACTIVE).orElseThrow(GiftNotFoundException::new);
        if (gift.getPublisher().getWorkId().equals(receiverWorkId)) {
            LOGGER.error("Gift cannot be received by same user");
            throw new GiftReceivedBySameUserException();
        }
        LocalDate giftDate = gift.getGiftDate();
        Schedule schedule = scheduleRepository.findByDateAndEmployee_WorkId(giftDate, receiverWorkId).orElseThrow(ScheduleNotFoundException::new);
        Hour firstHour = gift.getHours().get(0);
        Hour lastHour = gift.getHours().get(gift.getHours().size() - 1);

        if (hourService.validateGap(firstHour,lastHour,schedule)
                && hourService.validateHours(schedule, firstHour.getStart(), lastHour.getEnd())
                && validateAgainstSwapsAndGifts(receiverWorkId, firstHour.getStart(), lastHour.getEnd(), giftDate)) {
            Employee employee = employeeRepository.findByWorkId(receiverWorkId).orElseThrow(EmployeeNotFoundException::new);
            gift.setReceiver(employee);
            gift.setStatus(RequestStatusEnum.IN_PROGRESS);
            giftRepository.save(gift);
            return true;
        }
        return false;
    }

    @Transactional
    public boolean acceptGift(int giftId) {
        LOGGER.info("Started accepting gift with ID: " + giftId);
        Gift gift = giftRepository.findByGiftIdAndStatus(giftId, RequestStatusEnum.IN_PROGRESS)
                .orElseThrow(GiftNotFoundException::new);
        LocalDate giftDate = gift.getGiftDate();
        Schedule receiverSchedule = scheduleRepository.findByDateAndEmployee_WorkId(giftDate, gift.getReceiver().getWorkId())
                .orElseThrow(ScheduleNotFoundException::new);
        if (!receiverSchedule.getEmployee().getUser().getActive()) {
            throw new UserIsInactiveException();
        }
        LocalTime start = gift.getHours().get(0).getStart();
        LocalTime end = gift.getHours().get(gift.getHours().size() - 1).getEnd();
        if (hourService.validateHours(receiverSchedule,start,end)){
            Schedule publisherSchedule = scheduleRepository.findByDateAndEmployee_WorkId(giftDate, gift.getPublisher().getWorkId())
                    .orElseThrow(ScheduleNotFoundException::new);

            if (!publisherSchedule.getEmployee().getUser().getActive()) {
                throw new UserIsInactiveException();
            }
            gift.setStatus(RequestStatusEnum.OK);
            giftRepository.save(gift);

            for (Hour hour : gift.getHours()) {
                hour.setGiftExists(false);
                publisherSchedule.getHours().remove(hour);
                hour.setSchedule(receiverSchedule);
            }
            hourRepository.saveAll(gift.getHours());

            publisherSchedule.setTotalHours(publisherSchedule.getTotalHours() - gift.getHours().size());
            if (publisherSchedule.getTotalHours() == 0) {
                publisherSchedule.setWorkStatus(StatusEnum.REST);
            }
            scheduleRepository.save(publisherSchedule);

            receiverSchedule.setTotalHours(receiverSchedule.getTotalHours() + gift.getHours().size());
            receiverSchedule.setWorkStatus(StatusEnum.WORK);

            scheduleRepository.save(receiverSchedule);

            LOGGER.info("Gift has been saved and hours transferred successfully");
            return true;
        }
        LOGGER.error("Error during validation");
        return false;
    }

    @Transactional
    public void rejectGift(int giftId) {
        LOGGER.info("Started rejecting gift with ID: " + giftId);
        Gift gift = giftRepository.findByGiftIdAndStatus(giftId, RequestStatusEnum.IN_PROGRESS)
                .orElseThrow(GiftNotFoundException::new);
        gift.setStatus(RequestStatusEnum.REJECTED);
        List<Hour> hours = gift.getHours();
        hours = hours.stream().peek(hour -> hour.setGiftExists(false)).toList();
        gift.getHours().clear();
        giftRepository.save(gift);
        hourRepository.saveAll(hours);
    }

    public List<GiftDTO> getGiftsByWorkId(String workId) {
        LOGGER.info("Started search for gifts by workId");
        Employee publisher = employeeRepository.findByWorkId(workId).orElseThrow(EmployeeNotFoundException::new);
        if (!publisher.getUser().getActive()) {
            throw new UserIsInactiveException();
        }
        List<Gift> allByPublisher = giftRepository.findAllByPublisher(publisher);
        List<GiftDTO> allGiftDTO = new ArrayList<>();
        allByPublisher.forEach(gift -> allGiftDTO.add(MapToDTOUtils.mapGiftToDTO(gift)));
        return allGiftDTO;
    }


    private boolean validateExistingGifts(String receiverWorkId, LocalTime start, LocalTime end, LocalDate giftDate) {
        LOGGER.info("Validating against existing gifts");
        List<Gift> existingGifts = giftRepository
                .findByReceiver_WorkIdAndStatusOrStatus(receiverWorkId, RequestStatusEnum.IN_PROGRESS, RequestStatusEnum.ACTIVE);
        if (!existingGifts.isEmpty()) {
            for (Gift existingGift : existingGifts) {
                if (existingGift.getGiftDate().isEqual(giftDate)) {
                    LocalTime existingStart = existingGift.getHours().get(0).getStart();
                    LocalTime existingEnd = existingGift.getHours().get(existingGift.getHours().size() - 1).getEnd();
                    if (start.isBefore(existingEnd) && end.isAfter(existingStart)) {
                        LOGGER.error("Gift time conflicts with an existing gift");
                        throw new GiftTimeConflictException();
                    }
                }
            }
        }
        LOGGER.info("Validated against existing gifts");
        return true;
    }

    private boolean validateExistingSwaps(String receiverWorkId, LocalTime start, LocalTime end, LocalDate giftDate) {
        LOGGER.info("Validating against existing swaps");
        List<Swap> existingSwaps = swapRepository
                .findByReceiver_WorkIdAndStatusOrStatus(receiverWorkId, RequestStatusEnum.IN_PROGRESS, RequestStatusEnum.ACTIVE);

        if (!existingSwaps.isEmpty()) {
            for (Swap existingSwap : existingSwaps) {
                if (existingSwap.getTargetDate().isEqual(giftDate)) {
                    LocalTime existingStart = existingSwap.getTargetStart();
                    LocalTime existingEnd = existingSwap.getTargetEnd();
                    if (start.isBefore(existingEnd) && end.isAfter(existingStart)) {
                        LOGGER.error("Gift time conflicts with an existing swap target hours");
                        throw new SwapTimeConflictException();
                    }
                }
            }
        }
        LOGGER.info("Validated against existing swaps");
        return true;
    }

    public boolean validateAgainstSwapsAndGifts(String receiverWorkId, LocalTime start, LocalTime end, LocalDate giftDate) {
        return validateExistingGifts(receiverWorkId,start,end,giftDate)
                && validateExistingSwaps(receiverWorkId,start,end,giftDate);
    }

    public void filterNonReceivedGifts() {
        LOGGER.info("Started filtering non-received gifts");
        List<Gift> gifts = giftRepository.findAllByStatusIsLike(RequestStatusEnum.ACTIVE);

        for (Gift gift : gifts) {
            for (Hour hour : gift.getHours()) {
                long duration = Duration.between(
                                LocalDateTime.now(),
                                LocalDateTime.of(hour.getSchedule().getDate(), hour.getStart()))
                        .toMinutes();
                if (duration < 240) {
                    LOGGER.warn("Found non-received gift");
                    gift.getHours().replaceAll(hour1 -> {
                        hour1.setSwapExists(false);
                        return hour1;
                    });
                    giftRepository.delete(gift);
                    LOGGER.warn("Gift has been removed");
                    break;
                }
            }
        }
    }


}
