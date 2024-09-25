package com.blackwhissh.workload.service;

import com.blackwhissh.workload.dto.SwapDTO;
import com.blackwhissh.workload.entity.*;
import com.blackwhissh.workload.entity.enums.RequestStatusEnum;
import com.blackwhissh.workload.entity.enums.StatusEnum;
import com.blackwhissh.workload.exceptions.list.*;
import com.blackwhissh.workload.repository.EmployeeRepository;
import com.blackwhissh.workload.repository.HourRepository;
import com.blackwhissh.workload.repository.ScheduleRepository;
import com.blackwhissh.workload.repository.SwapRepository;
import com.blackwhissh.workload.utils.MapToDTOUtils;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.blackwhissh.workload.utils.MapToDTOUtils.mapSwapToDTO;

@Service
public class SwapService {
    private final static Logger LOGGER = LoggerFactory.getLogger(SwapService.class);
    private final HourService hourService;
    private final GiftService giftService;
    private final SwapRepository swapRepository;
    private final EmployeeRepository employeeRepository;
    private final HourRepository hourRepository;
    private final ScheduleRepository scheduleRepository;

    public SwapService(HourService hourService,
                       GiftService giftService, SwapRepository swapRepository,
                       EmployeeRepository employeeRepository,
                       HourRepository hourRepository, ScheduleRepository scheduleRepository) {
        this.hourService = hourService;
        this.giftService = giftService;
        this.swapRepository = swapRepository;
        this.employeeRepository = employeeRepository;
        this.hourRepository = hourRepository;
        this.scheduleRepository = scheduleRepository;
    }

    @Transactional
    public SwapDTO publishSwap (String publisherWorkId, LocalDate swapDate,
                                LocalTime start, LocalTime end,
                                LocalDate targetDate, LocalTime targetStart,
                                LocalTime targetEnd, Optional<String> receiverWorkId) {
        LOGGER.info("Started to publish swap");
        Schedule publisherSchedule = scheduleRepository
                .findByDateAndEmployee_WorkId(swapDate, publisherWorkId)
                .orElseThrow(ScheduleNotFoundException::new);
        if (publisherSchedule.getHours().isEmpty()) {
            throw new WrongHourAmountException();
        }
        hourService.checkIsBeforeRotation(publisherSchedule);

        List<Hour> hours = new ArrayList<>();

        for (Hour hour : publisherSchedule.getHours()) {
            checkHourStartAndEnd(start, end, hour, hours);
        }
        if (hours.size() != Duration.between(start, end).toHours()) {
            LOGGER.error("Could not find all hours");
            throw new NoNeededHoursPresentException();
        }
        if (targetEnd.isBefore(targetStart)) {
            LOGGER.error("Wrong target time is provided");
            throw new HourSwapException();
        }
        if (!hourService.checkHoursAmount(hours)) {
            LOGGER.error("Wrong amount of proposed hours");
            throw new WrongHourAmountException();
        }

        if (Duration.between(targetStart, targetEnd).toHours() == 1L) {
            LOGGER.error("Wrong amount of target hours");
            throw new WrongHourAmountException();
        }

        if (!hourService.checkFirstOrLastHour(hours)){
            throw new FirstOrLastHourTransferException();
        }

        if (!hourService.checkHoursChain(hours)) {
            throw new HoursAreNotChainedException();
        }

        if (targetDate.getMonthValue() != LocalDate.now().getMonthValue()) {
            if (!hourService.checkNextMonthRequest(targetDate)) {
                throw new NextMonthSwapException();
            }
        }

        Schedule targetSchedule = scheduleRepository.findByDateAndEmployee_WorkId(targetDate, publisherWorkId)
                .orElseThrow(ScheduleNotFoundException::new);

        Employee publisher = employeeRepository.findByWorkId(publisherWorkId)
                .orElseThrow(EmployeeNotFoundException::new);

        List<Hour> hourList = new ArrayList<>();
        LOGGER.info("Started searching hours");

        for (Hour hour : hours) {
            if (hour.getGiftExists() || hour.getSwapExists()) {
                LOGGER.error("One or more hour is already being gifted/swapped");
                throw new HourSwapException();
            }
            if (hour.getRotationItem() != null) {
                LOGGER.error("Hour is already in rotation");
                throw new HourIsInRotationException();
            }
            hour.setSwapExists(true);
            hourList.add(hour);
        }

        Hour firstHour = hourList.get(0);
        Hour lastHour = hourList.get(hourList.size() - 1);


        if (!hourService.validateSwapTargetHours(targetSchedule, targetStart, targetEnd, swapDate, hourList)
                && !hourService.validateGap(firstHour, lastHour, targetSchedule)) {
            LOGGER.error("Error during validating target schedule hours");
            throw new HourSwapException();
        }
        Swap swap = new Swap(
                publisher,
                hourList,
                swapDate,
                LocalDate.now(),
                RequestStatusEnum.ACTIVE,
                targetDate,
                targetStart,
                targetEnd
        );

        receiverWorkId.ifPresent(id -> {
            Employee receiver = employeeRepository.findByWorkId(id)
                    .orElseThrow(EmployeeNotFoundException::new);
            swap.setReceiver(receiver);
        });
        swapRepository.save(swap);
        LOGGER.info("Swap saved successfully");
        return MapToDTOUtils.mapSwapToDTO(swap);
    }

    public static void checkHourStartAndEnd(LocalTime start, LocalTime end, Hour hour, List<Hour> hours) {
        if (hour.getStart().equals(start)) {
            hours.add(hour);
            return;
        }
        if (hour.getEnd().equals(end)){
            hours.add(hour);
            return;
        }
        if (hour.getStart().isAfter(start) && hour.getEnd().isBefore(end)){
            hours.add(hour);
        }
    }

    @Transactional
    public boolean receiveSwap(String receiverWorkId, int swapId) {
        LOGGER.info("Started receiving swap");
        Swap swap = swapRepository.findBySwapIdAndStatus(swapId, RequestStatusEnum.ACTIVE)
                .orElseThrow(SwapNotFoundException::new);
        if (swap.getPublisher().getWorkId().equals(receiverWorkId)) {
            LOGGER.error("Swap cannot be received by same user");
            throw new SwapReceivedBySameUserException();
        }
        LocalDate swapDate = swap.getSwapDate();
        LocalDate targetDate = swap.getTargetDate();

        LOGGER.info("Checking if receiver has appropriate hours");
        Schedule receiverSchedule = scheduleRepository
                .findByDateAndEmployee_WorkId(targetDate, receiverWorkId)
                .orElseThrow(ScheduleNotFoundException::new);
        Schedule receiverNewSchedule = scheduleRepository
                .findByDateAndEmployee_WorkId(swapDate, receiverWorkId)
                .orElseThrow(ScheduleNotFoundException::new);
        if (receiverSchedule.getHours().isEmpty()) {
            throw new WrongHourAmountException();
        }
        hourService.checkIsBeforeRotation(receiverSchedule);

        List<Hour> receiverHours = new ArrayList<>();

        for (Hour hour : receiverSchedule.getHours()) {
            checkHourStartAndEnd(swap.getTargetStart(), swap.getTargetEnd(), hour, receiverHours);
        }

        if (receiverHours.size() != Duration.between(swap.getTargetStart(), swap.getTargetEnd()).toHours()) {
            LOGGER.error("No needed hours are present in receiver's schedule");
            throw new NoNeededHoursPresentException();
        }

        LocalTime publisherStart = swap.getHours().get(0).getStart();
        LocalTime publisherEnd = swap.getHours().get(swap.getHours().size() - 1).getEnd();

        if (hourService.checkFirstOrLastHour(receiverHours)){
            throw new FirstOrLastHourCannotBeTransferredException();
        }
        if (receiverHours.isEmpty()) {
            LOGGER.error("Receivers schedule is empty");
            throw new ScheduleIsEmptyException();
        }


        Hour firstHour = swap.getHours().get(0);
        Hour lastHour = swap.getHours().get(swap.getHours().size() - 1);

        if (hourService.validateSwapTargetHours(receiverNewSchedule, publisherStart, publisherEnd, swapDate, swap.getHours())
                && hourService.validateGap(firstHour, lastHour, receiverSchedule)
                && giftService.validateAgainstSwapsAndGifts(receiverWorkId, publisherStart, publisherEnd, swapDate)) {
            for (Hour hour : receiverHours) {
                if (hour.getGiftExists() || hour.getSwapExists()) {
                    LOGGER.error("One or more hour is already is being gifted/swapped");
                    throw new HourSwapException();
                }
                hour.setSwapExists(true);

            }
            hourRepository.saveAll(receiverHours);
            Employee receiver = employeeRepository.findByWorkId(receiverWorkId).orElseThrow(EmployeeNotFoundException::new);
            swap.setReceiver(receiver);
            swap.setStatus(RequestStatusEnum.IN_PROGRESS);
            swap.setReceiverHours(receiverHours);
            swapRepository.save(swap);
            return true;
        }
        return false;
    }

    public boolean deleteCurrentUserSwap(String workId, int swapId) {
        LOGGER.info("Started deleting current user swap with ID: " + swapId);
        Swap swap = swapRepository.findByPublisher_WorkIdAndSwapId(workId, swapId)
                .orElseThrow(SwapNotFoundException::new);
        if (swap.getStatus().equals(RequestStatusEnum.ACTIVE)) {
            swap.getHours().forEach(hour -> hour.setSwapExists(false));
            swapRepository.delete(swap);
            return true;
        }
        return false;
    }

    @Transactional
    public void rejectSwap(int swapId) {
        LOGGER.info("Started rejecting swap with ID: " + swapId);
        Swap swap = swapRepository.findBySwapIdAndStatus(swapId, RequestStatusEnum.IN_PROGRESS)
                .orElseThrow(GiftNotFoundException::new);
        swap.setStatus(RequestStatusEnum.REJECTED);
        List<Hour> publisherHours = swap.getHours();
        publisherHours = publisherHours.stream().peek(hour -> hour.setSwapExists(false)).toList();
        swap.getHours().clear();

        List<Hour> receiverHours = swap.getReceiverHours();
        receiverHours = receiverHours.stream().peek(hour -> hour.setSwapExists(false)).toList();
        swap.getReceiverHours().clear();

        hourRepository.saveAll(publisherHours);
        hourRepository.saveAll(receiverHours);
        swapRepository.save(swap);
    }

    @Transactional
    public boolean acceptSwap(int swapId) {
        LOGGER.info("Started accepting swap with ID: " + swapId);
        Swap swap = swapRepository.findBySwapIdAndStatus(swapId, RequestStatusEnum.IN_PROGRESS)
                .orElseThrow(SwapNotFoundException::new);
        LocalDate swapDate = swap.getSwapDate();
        Schedule receiverNewSchedule = scheduleRepository.findByDateAndEmployee_WorkId(swapDate, swap.getReceiver().getWorkId())
                .orElseThrow(ScheduleNotFoundException::new);
        Schedule publisherOldSchedule = scheduleRepository.findByDateAndEmployee_WorkId(swapDate, swap.getPublisher().getWorkId())
                .orElseThrow(ScheduleNotFoundException::new);
        LocalTime start = swap.getHours().get(0).getStart();
        LocalTime end = swap.getHours().get(swap.getHours().size() - 1).getEnd();

        LocalDate targetDate = swap.getTargetDate();
        Schedule receiverOldSchedule = scheduleRepository.findByDateAndEmployee_WorkId(targetDate, swap.getReceiver().getWorkId())
                .orElseThrow(ScheduleNotFoundException::new);
        Schedule publisherNewSchedule = scheduleRepository.findByDateAndEmployee_WorkId(targetDate, swap.getPublisher().getWorkId())
                .orElseThrow(ScheduleNotFoundException::new);
        LocalTime targetStart = swap.getTargetStart();
        LocalTime targetEnd = swap.getTargetEnd();

        List<Hour> receiverOldHours = hourRepository.findBetweenStartEndAndSchedule_Id(targetStart, targetEnd, receiverOldSchedule.getScheduleId());
        int receiverOldHoursSize = receiverOldHours.size();
        if (hourService.validateSwapTargetHours(receiverNewSchedule,start,end,swapDate,swap.getHours())
                && hourService.validateSwapTargetHours(publisherNewSchedule, targetStart, targetEnd, targetDate, receiverOldHours)){

            for (Hour hour : swap.getHours()) {
                hour.setSwapExists(false);
                publisherOldSchedule.getHours().remove(hour);
                hour.setSchedule(receiverNewSchedule);
                receiverNewSchedule.getHours().add(hour);
            }
            hourRepository.saveAll(swap.getHours());

            for (Hour hour : receiverOldHours) {
                if (hour.getRotationItem() != null) {
                    LOGGER.error("Hour is already in rotation");
                    throw new HourIsInRotationException();
                }
                hour.setSwapExists(false);
                receiverOldSchedule.getHours().remove(hour);
                hour.setSchedule(publisherNewSchedule);
                publisherNewSchedule.getHours().add(hour);
            }
            hourRepository.saveAll(receiverOldHours);

            receiverOldSchedule.setTotalHours(receiverOldSchedule.getTotalHours() - receiverOldHoursSize);
            if (receiverOldSchedule.getTotalHours() == 0) {
                receiverOldSchedule.setWorkStatus(StatusEnum.REST);
            }
            scheduleRepository.save(receiverOldSchedule);
            publisherOldSchedule.setTotalHours(publisherOldSchedule.getTotalHours() - swap.getHours().size());
            if (publisherOldSchedule.getTotalHours() == 0) {
                publisherOldSchedule.setWorkStatus(StatusEnum.REST);
            }
            scheduleRepository.save(publisherOldSchedule);

            System.out.println(publisherNewSchedule.getTotalHours());
            publisherNewSchedule.setTotalHours(publisherNewSchedule.getTotalHours() + receiverOldHoursSize);
            publisherNewSchedule.setWorkStatus(StatusEnum.WORK);
            System.out.println(publisherNewSchedule.getTotalHours());
            scheduleRepository.save(publisherNewSchedule);
            receiverNewSchedule.setTotalHours(receiverNewSchedule.getTotalHours() + swap.getHours().size());
            receiverNewSchedule.setWorkStatus(StatusEnum.WORK);
            scheduleRepository.save(receiverNewSchedule);
            swap.setStatus(RequestStatusEnum.OK);
            swapRepository.save(swap);
            LOGGER.info("Swap has been saved and hours transferred successfully");
            return true;
        }
        LOGGER.error("Error during validation");
        return false;
    }

    public void filterNonReceivedSwaps() {
        LOGGER.info("Started filtering non-received swaps");
        List<Swap> swaps = swapRepository.findAllByStatusIsLike(RequestStatusEnum.ACTIVE);

        for (Swap swap : swaps) {
            for (Hour hour : swap.getHours()) {
                long duration = Duration.between(
                        LocalDateTime.now(),
                        LocalDateTime.of(hour.getSchedule().getDate(), hour.getStart()))
                        .toMinutes();
                if (duration < 240) {
                    LOGGER.warn("Found non-received swap");
                    swap.getHours().replaceAll(hour1 -> {
                        hour1.setSwapExists(false);
                        return hour1;
                    });
                    swapRepository.delete(swap);
                    LOGGER.warn("Swap has been removed");
                    break;
                }
            }
        }
    }

    public List<SwapDTO> getSwapsByWorkId(String workId) {
        LOGGER.info("Started search for swaps by workId");
        Employee publisher = employeeRepository.findByWorkId(workId).orElseThrow(EmployeeNotFoundException::new);
        if (!publisher.getUser().getActive()) {
            throw new UserIsInactiveException();
        }
        List<Swap> allByPublisher = swapRepository.findAllByPublisher(publisher);
        List<SwapDTO> allSwapDTO = new ArrayList<>();
        allByPublisher.forEach(swap -> allSwapDTO.add(MapToDTOUtils.mapSwapToDTO(swap)));
        return allSwapDTO;
    }

    public List<SwapDTO> getAllSwaps() {
        LOGGER.info("Started search for all swaps");
        List<Swap> allSwaps = swapRepository.findAll();
        allSwaps = allSwaps.stream().filter(gift -> gift.getPublisher().getUser().getActive()).toList();
        List<SwapDTO> allSwapsDTO = new ArrayList<>();
        allSwaps.forEach(swap -> allSwapsDTO.add(MapToDTOUtils.mapSwapToDTO(swap)));
        return allSwapsDTO;
    }

    public List<SwapDTO> getAllSwapsByStatus(RequestStatusEnum statusEnum) {
        LOGGER.info("Started search for all swaps with type: " + statusEnum.name());
        List<Swap> allByStatus = swapRepository.findAll()
                .stream().filter(swap -> swap.getStatus().equals(statusEnum)).toList();
        allByStatus = allByStatus.stream().filter(swap -> swap.getPublisher().getUser().getActive()).toList();
        List<SwapDTO> allDTO = new ArrayList<>();
        allByStatus.forEach(swap -> allDTO.add(MapToDTOUtils.mapSwapToDTO(swap)));
        return allDTO;
    }

    public List<SwapDTO> getAllActiveSwaps() {
        LOGGER.info("Started search for all active swaps");
        List<Swap> allActive = swapRepository.findAll()
                .stream().filter(swap -> swap.getStatus().equals(RequestStatusEnum.ACTIVE)).toList();
        allActive = allActive.stream().filter(swap -> swap.getPublisher().getUser().getActive()).toList();
        List<SwapDTO> allActiveDTO = new ArrayList<>();
        allActive.forEach(swap -> allActiveDTO.add(MapToDTOUtils.mapSwapToDTO(swap)));
        return allActiveDTO;
    }
}
