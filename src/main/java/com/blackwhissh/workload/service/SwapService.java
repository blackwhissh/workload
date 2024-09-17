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
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static com.blackwhissh.workload.utils.MapToDTOUtils.mapSwapToDTO;

@Service
public class SwapService {
    private final static Logger LOGGER = LoggerFactory.getLogger(SwapService.class);
    private final HourService hourService;
    private final SwapRepository swapRepository;
    private final EmployeeRepository employeeRepository;
    private final HourRepository hourRepository;
    private final ScheduleRepository scheduleRepository;

    public SwapService(HourService hourService,
                       SwapRepository swapRepository,
                       EmployeeRepository employeeRepository,
                       HourRepository hourRepository, ScheduleRepository scheduleRepository) {
        this.hourService = hourService;
        this.swapRepository = swapRepository;
        this.employeeRepository = employeeRepository;
        this.hourRepository = hourRepository;
        this.scheduleRepository = scheduleRepository;
    }

    @Transactional
    public SwapDTO publishSwap (String workId, List<Integer> hourIdList,
                                LocalDate targetDate, LocalTime targetStart) {
        LOGGER.info("Started to publish swap");

        //TODO Check if first or last hour left alone
//        if (!hourService.checkFirstOrLastHour(hourIdList.get(0))) {
//            throw new FirstOrLastHourCannotBeTransferredException();
//        }

        //Checking if target date is other month
        if (targetDate.getMonthValue() != LocalDate.now().getMonthValue()) {
            if (!hourService.checkNextMonthRequest(targetDate)) {
                throw new NextMonthSwapException();
            }
        }

        LOGGER.info("Checking if current user can accept new hours");
        Schedule publisherSchedule = scheduleRepository.findByDateAndEmployee_WorkId(targetDate, workId)
                .orElseThrow(ScheduleNotFoundException::new);
        if (!hourService.validateHours(publisherSchedule, targetStart, targetStart.plusHours(hourIdList.size()))) {
            LOGGER.error("Error during validating hours");
            throw new HourSwapException();
        }

        Employee publisher = employeeRepository.findByWorkId(workId)
                .orElseThrow(EmployeeNotFoundException::new);

        List<Hour> hourList = new ArrayList<>();
        if (!hourService.isValidMinimumLimit(hourIdList.get(0), hourIdList.size())) {
            LOGGER.error("Can not be swapped, monthly hours will be less than 40");
            throw new MonthlyMinimumLimitException();
        }
        LOGGER.info("Started searching hours");

        for (int hourId : hourIdList) {
            Hour hour = hourRepository.findById(hourId).orElseThrow();
            if (hour.getGiftExists() || hour.getSwapExists()) {
                LOGGER.error("One or more hour is already being gifted/swapped");
                throw new HourSwapException();
            }
            hour.setSwapExists(true);
            hourList.add(hour);
        }

        LocalDate swapDate = hourList.get(0).getSchedule().getDate();
        Swap saved = swapRepository.save(new Swap(
                publisher,
                hourList,
                swapDate,
                LocalDate.now(),
                RequestStatusEnum.ACTIVE,
                targetDate,
                targetStart
        ));

        LOGGER.info("Swap saved successfully");
        return MapToDTOUtils.mapSwapToDTO(saved);
    }

    public List<SwapDTO> listAllSwaps() {
        LOGGER.info("Started listing all swaps");
        List<Swap> allSwaps = swapRepository.findAll();
        LOGGER.info("Finished listing swaps");
        List<SwapDTO> allDTO = new ArrayList<>();
        for (Swap swap : allSwaps) {
            allDTO.add(mapSwapToDTO(swap));
        }
        return allDTO;
    }

    public List<SwapDTO> listSwapsByStatus(RequestStatusEnum requestStatusEnum) {
        LOGGER.info("Started listing swaps by status: " + requestStatusEnum);
        List<Swap> allByStatus = swapRepository.findAllByStatusIsLike(requestStatusEnum);
        LOGGER.info("Finished listing swaps by status: " + requestStatusEnum);
        List<SwapDTO> allByStatusDTO = new ArrayList<>();
        for (Swap swap : allByStatus) {
            allByStatusDTO.add(mapSwapToDTO(swap));
        }
        return allByStatusDTO;
    }

    @Transactional
    public boolean receiveSwap(String receiverWorkId, int swapId) {
        LOGGER.info("Started receiving swap");
        Swap swap = swapRepository.findBySwapIdAndStatus(swapId, RequestStatusEnum.ACTIVE).orElseThrow(SwapNotFoundException::new);
        if (swap.getPublisher().getWorkId().equals(receiverWorkId)) {
            LOGGER.error("Swap cannot be received by same user");
            throw new SwapReceivedBySameUserException();
        }
        LocalDate swapDate = swap.getSwapDate();
        Schedule receiverSchedule = scheduleRepository.findByDateAndEmployee_WorkId(swapDate, receiverWorkId)
                .orElseThrow(ScheduleNotFoundException::new);

        LOGGER.info("Checking if receiver has appropriate hours");
        LocalTime start = swap.getHours().get(0).getStart();
        LocalTime end = swap.getHours().get(swap.getHours().size() - 1).getEnd();
        List<Hour> receiverHours = hourRepository.findAllByStartAndEndAndSchedule(start, end, receiverSchedule);
        if (receiverHours.isEmpty()) {
            LOGGER.error("Receivers schedule is empty");
            throw new ScheduleIsEmptyException();
        }
        if (receiverHours.size() != Duration.between(start, end).toHours()) {
            LOGGER.error("No needed hours are present in receiver's schedule");
            throw new NoNeededHoursPresentException();
        }

        if (hourService.validateHours(receiverSchedule, start, end)
                && validateExistingSwaps(receiverWorkId, start, end, swapDate)
                && validateFirstAndLastHourOfSwap(swap.getHours().get(0).getId())) {
            for (Hour hour : receiverHours) {
                if (hour.getGiftExists() || hour.getSwapExists()) {
                    LOGGER.error("One or more hour is already is being gifted/swapped");
                    throw new HourSwapException();
                }
                hour.setGiftExists(true);
                receiverHours.add(hour);
            }
            hourRepository.saveAll(receiverHours);
            Employee receiver = employeeRepository.findByWorkId(receiverWorkId).orElseThrow(EmployeeNotFoundException::new);
            swap.setReceiver(receiver);
            swap.setStatus(RequestStatusEnum.IN_PROGRESS);
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
        if (hourService.validateHours(receiverNewSchedule,start,end)
                && hourService.validateHours(publisherNewSchedule, targetStart, targetEnd)){

            for (Hour hour : swap.getHours()) {
                hour.setSwapExists(false);
                publisherOldSchedule.getHours().remove(hour);
                hour.setSchedule(receiverNewSchedule);
            }
            hourRepository.saveAll(swap.getHours());

            List<Hour> receiverOldHours = hourRepository.findAllByStartAndEndAndSchedule(targetStart, targetEnd, receiverOldSchedule);
            for (Hour hour : receiverOldHours) {
                hour.setSwapExists(false);
                receiverOldSchedule.getHours().remove(hour);
                hour.setSchedule(publisherNewSchedule);
            }
            hourRepository.saveAll(receiverOldHours);

            //TODO gavagrdzelo aqedan
            publisherNewSchedule.setTotalHours(publisherNewSchedule.getTotalHours() - swap.getHours().size());
            if (publisherNewSchedule.getTotalHours() == 0) {
                publisherNewSchedule.setWorkStatus(StatusEnum.REST);
            }
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

    public boolean validateExistingSwaps(String workId, LocalTime start, LocalTime end, LocalDate swapDate) {
        LOGGER.info("Validating against existing swaps");
        List<Swap> existingSwaps = swapRepository.findByReceiver_WorkIdAndStatus(workId, RequestStatusEnum.IN_PROGRESS);
        for (Swap existingSwap : existingSwaps) {
            if (existingSwap.getSwapDate().isEqual(swapDate)) {
                LocalTime existingStart = existingSwap.getHours().get(0).getStart();
                LocalTime existingEnd = existingSwap.getHours().get(existingSwap.getHours().size() - 1).getEnd();
                if (start.isBefore(existingEnd) && end.isAfter(existingStart)) {
                    LOGGER.error("Swap time conflicts with an existing swap");
                    throw new SwapTimeConflictException();
                }
            }
        }
        return true;
    }

    public boolean validateFirstAndLastHourOfSwap(int publisherHourId) {
        Hour publisherHour = hourRepository.findById(publisherHourId).orElseThrow();
        Hour receiverHour = hourRepository.findByStartAndSchedule(publisherHour.getStart(), publisherHour.getSchedule())
                        .orElseThrow();
        LOGGER.info("Checking if first or last hour is not left for receiver");
        //TODO Check if first or last hour left alone

//        if (!hourService.checkFirstOrLastHour(receiverHour.getId())) {
//            throw new FirstOrLastHourCannotBeTransferredException();
//        }
        return true;
    }
}
