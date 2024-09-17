package com.blackwhissh.workload.service;

import com.blackwhissh.workload.dto.GiftDTO;
import com.blackwhissh.workload.entity.Employee;
import com.blackwhissh.workload.entity.Gift;
import com.blackwhissh.workload.entity.Hour;
import com.blackwhissh.workload.entity.Schedule;
import com.blackwhissh.workload.entity.enums.RequestStatusEnum;
import com.blackwhissh.workload.entity.enums.StatusEnum;
import com.blackwhissh.workload.exceptions.list.*;
import com.blackwhissh.workload.repository.EmployeeRepository;
import com.blackwhissh.workload.repository.GiftRepository;
import com.blackwhissh.workload.repository.HourRepository;
import com.blackwhissh.workload.repository.ScheduleRepository;
import com.blackwhissh.workload.utils.MapToDTOUtils;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class GiftService {
    private final static Logger LOGGER = LoggerFactory.getLogger(GiftService.class);
    private final GiftRepository giftRepository;
    private final EmployeeRepository employeeRepository;
    private final HourRepository hourRepository;
    private final ScheduleRepository scheduleRepository;
    private final HourService hourService;

    public GiftService(GiftRepository giftRepository, EmployeeRepository employeeRepository, HourRepository hourRepository, ScheduleRepository scheduleRepository, HourService hourService) {
        this.giftRepository = giftRepository;
        this.employeeRepository = employeeRepository;
        this.hourRepository = hourRepository;
        this.scheduleRepository = scheduleRepository;
        this.hourService = hourService;
    }
    @Transactional
    public GiftDTO publishGift(String workId, List<Integer> hourIdList) {
        LOGGER.info("Started to publish gift");
        if (!hourService.checkHoursAmount(hourIdList)) {
            throw new WrongHourAmountException();
        }
        Employee publisher = employeeRepository.findByWorkId(workId)
                .orElseThrow(EmployeeNotFoundException::new);

        if (!hourService.checkFirstOrLastHour(hourIdList)){
            throw new FirstOrLastHourTransferException();
        }

        if (!hourService.checkHoursChain(hourIdList)) {
            throw new HoursAreNotChainedException();
        }

        List<Hour> hourList = new ArrayList<>();
        if (!hourService.isValidMinimumLimit(hourIdList.get(0), hourIdList.size())) {
            LOGGER.error("Can not be gifted, monthly hours will be less than 40");
            throw new MonthlyMinimumLimitException();
        }
        LOGGER.info("Started searching hours");
        checkAndSetGift(hourIdList, hourList);

        LocalDate giftDate = hourList.get(0).getSchedule().getDate();
        Gift saved = giftRepository.save(new Gift(
                publisher,
                hourList,
                giftDate,
                LocalDate.now(),
                RequestStatusEnum.ACTIVE
        ));
        LOGGER.info("Gift saved successfully");
        return MapToDTOUtils.mapGiftToDTO(saved);
    }

    private void checkAndSetGift(List<Integer> hourIdList, List<Hour> hourList) {
        for (int hourId : hourIdList) {
            Hour hour = hourRepository.findById(hourId).orElseThrow();
            if (hour.getGiftExists() || hour.getSwapExists()) {
                LOGGER.error("One or more hour is already is being gifted/swapped");
                throw new HourGiftException();
            }
            hour.setGiftExists(true);
            hourList.add(hour);
        }
    }

    public List<GiftDTO> getAllActiveGifts() {
        LOGGER.info("Started search for all active gifts");
        List<Gift> allActive = giftRepository.findAll()
                .stream().filter(gift -> gift.getStatus().equals(RequestStatusEnum.ACTIVE)).toList();
        List<GiftDTO> allActiveDTO = new ArrayList<>();
        allActive.forEach(gift -> allActiveDTO.add(MapToDTOUtils.mapGiftToDTO(gift)));
        return allActiveDTO;
    }

    public List<GiftDTO> getAllGiftsByStatus(RequestStatusEnum statusEnum) {
        LOGGER.info("Started search for all gifts with type: " + statusEnum.name());
        List<Gift> allByStatus = giftRepository.findAll()
                .stream().filter(gift -> gift.getStatus().equals(statusEnum)).toList();
        List<GiftDTO> allDTO = new ArrayList<>();
        allByStatus.forEach(gift -> allDTO.add(MapToDTOUtils.mapGiftToDTO(gift)));
        return allDTO;
    }

    public List<GiftDTO> getAllGifts() {
        LOGGER.info("Started search for all gifts");
        List<Gift> allGifts = giftRepository.findAll();
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
        LocalTime start = firstHour.getStart();
        LocalTime end = lastHour.getEnd();

        // Fetch previous and next hours
        Hour previousHour = hourService.findLastHourBefore(schedule, start);
        Hour nextHour = hourService.findFirstHourAfter(schedule, end);

        // Validate gaps
        boolean validPreviousGap = (previousHour == null) || hourService.checkPreviousHourGap(firstHour, previousHour);
        boolean validNextGap = (nextHour == null) || hourService.checkNextHourGap(lastHour, nextHour);

        if (validPreviousGap && validNextGap && hourService.validateHours(schedule, start, end) && validateExistingGifts(receiverWorkId, start, end, giftDate)) {
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
        LocalTime start = gift.getHours().get(0).getStart();
        LocalTime end = gift.getHours().get(gift.getHours().size() - 1).getEnd();
        if (hourService.validateHours(receiverSchedule,start,end)){
            Schedule publisherSchedule = scheduleRepository.findByDateAndEmployee_WorkId(giftDate, gift.getPublisher().getWorkId())
                    .orElseThrow(ScheduleNotFoundException::new);
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

    public void rejectGift(int giftId) {
        LOGGER.info("Started rejecting gift with ID: " + giftId);
        Gift gift = giftRepository.findByGiftIdAndStatus(giftId, RequestStatusEnum.IN_PROGRESS)
                .orElseThrow(GiftNotFoundException::new);
        gift.setStatus(RequestStatusEnum.REJECTED);
        giftRepository.save(gift);
    }

    public List<GiftDTO> getGiftsByWorkId(String workId) {
        LOGGER.info("Started search for gifts by workId");
        Employee publisher = employeeRepository.findByWorkId(workId).orElseThrow(EmployeeNotFoundException::new);
        List<Gift> allByPublisher = giftRepository.findAllByPublisher(publisher);
        List<GiftDTO> allGiftDTO = new ArrayList<>();
        allByPublisher.forEach(gift -> allGiftDTO.add(MapToDTOUtils.mapGiftToDTO(gift)));
        return allGiftDTO;
    }


    public boolean validateExistingGifts(String receiverWorkId, LocalTime start, LocalTime end, LocalDate giftDate) {
        LOGGER.info("Validating against existing gifts");
        List<Gift> existingGifts = giftRepository.findByReceiver_WorkIdAndStatus(receiverWorkId, RequestStatusEnum.IN_PROGRESS);
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
        return true;
    }

}
