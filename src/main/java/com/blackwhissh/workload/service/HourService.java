package com.blackwhissh.workload.service;

import com.blackwhissh.workload.dto.HourDTO;
import com.blackwhissh.workload.dto.request.AddNewHourRequest;
import com.blackwhissh.workload.entity.Gift;
import com.blackwhissh.workload.entity.Hour;
import com.blackwhissh.workload.entity.Schedule;
import com.blackwhissh.workload.entity.enums.StatusEnum;
import com.blackwhissh.workload.exceptions.list.*;
import com.blackwhissh.workload.repository.HourRepository;
import com.blackwhissh.workload.repository.ScheduleRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.*;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static com.blackwhissh.workload.utils.MapToDTOUtils.mapHourToDTO;

@Service
public class HourService {
    private final static Logger LOGGER = LoggerFactory.getLogger(HourService.class);
    private final ScheduleRepository scheduleRepository;
    private final HourRepository hourRepository;

    public HourService(ScheduleRepository scheduleRepository, HourRepository hourRepository) {
        this.scheduleRepository = scheduleRepository;
        this.hourRepository = hourRepository;
    }

    public static void changeHours(Schedule receiverSchedule, Schedule publisherSchedule, HourRepository hourRepository, List<Hour> hours, ScheduleRepository scheduleRepository, Gift gift) {
        hourRepository.saveAll(hours);

        publisherSchedule.setTotalHours(publisherSchedule.getTotalHours() - hours.size());
        if (publisherSchedule.getTotalHours() == 0) {
            publisherSchedule.setWorkStatus(StatusEnum.REST);
        }
        scheduleRepository.save(publisherSchedule);

        receiverSchedule.setTotalHours(receiverSchedule.getTotalHours() + hours.size());
        receiverSchedule.setWorkStatus(StatusEnum.WORK);
        scheduleRepository.save(receiverSchedule);
    }

    public List<HourDTO> getHoursByScheduleId(Integer scheduleId) {
        LOGGER.info("Started get hours by schedule with ID: " + scheduleId);
        Schedule schedule = scheduleRepository.findById(scheduleId).orElseThrow(ScheduleNotFoundException::new);
        List<Hour> bySchedule = hourRepository.findBySchedule(schedule);
        List<HourDTO> hourDTOList = new ArrayList<>();
        bySchedule.forEach(hour -> hourDTOList.add(new HourDTO(hour.getId(), hour.getStart(), hour.getEnd(), hour.getSwapExists(), hour.getGiftExists())));
        return hourDTOList;
    }

    public void generateMorningHours(Schedule schedule) {
        Hour first = new Hour(LocalTime.of(8, 0), LocalTime.of(9, 0));
        Hour second = new Hour(LocalTime.of(9, 0), LocalTime.of(10, 0));
        Hour third = new Hour(LocalTime.of(10, 0), LocalTime.of(11, 0));
        Hour fourth = new Hour(LocalTime.of(11, 0), LocalTime.of(12, 0));
        Hour fifth = new Hour(LocalTime.of(12, 0), LocalTime.of(13, 0));
        Hour sixth = new Hour(LocalTime.of(13, 0), LocalTime.of(14, 0));
        Hour seventh = new Hour(LocalTime.of(14, 0), LocalTime.of(15, 0));
        Hour eighth = new Hour(LocalTime.of(15, 0), LocalTime.of(16, 0));

        List<Hour> hours = List.of(first, second, third, fourth, fifth, sixth, seventh, eighth);

        hours.forEach(hour -> hour.setSchedule(schedule));
        if (schedule.getWorkStatus().toString().equalsIgnoreCase("work")) {
            hourRepository.saveAll(hours);
            schedule.setHours(hours);
        }
    }

    public void generateDayHours(Schedule schedule) {
        Hour first = new Hour(LocalTime.of(16, 0), LocalTime.of(17, 0));
        Hour second = new Hour(LocalTime.of(17, 0), LocalTime.of(18, 0));
        Hour third = new Hour(LocalTime.of(18, 0), LocalTime.of(19, 0));
        Hour fourth = new Hour(LocalTime.of(19, 0), LocalTime.of(20, 0));
        Hour fifth = new Hour(LocalTime.of(20, 0), LocalTime.of(21, 0));
        Hour sixth = new Hour(LocalTime.of(21, 0), LocalTime.of(22, 0));
        Hour seventh = new Hour(LocalTime.of(22, 0), LocalTime.of(23, 0));
        Hour eighth = new Hour(LocalTime.of(23, 0), LocalTime.of(0, 0));

        List<Hour> hours = List.of(first, second, third, fourth, fifth, sixth, seventh, eighth);

        hours.forEach(hour -> hour.setSchedule(schedule));

        if (schedule.getWorkStatus().toString().equalsIgnoreCase("work")) {
            hourRepository.saveAll(hours);
            schedule.setHours(hours);
        }
    }

    public void generateNightHours(Schedule schedule) {
        Hour first = new Hour(LocalTime.of(0, 0), LocalTime.of(1, 0));
        Hour second = new Hour(LocalTime.of(1, 0), LocalTime.of(2, 0));
        Hour third = new Hour(LocalTime.of(2, 0), LocalTime.of(3, 0));
        Hour fourth = new Hour(LocalTime.of(3, 0), LocalTime.of(4, 0));
        Hour fifth = new Hour(LocalTime.of(4, 0), LocalTime.of(5, 0));
        Hour sixth = new Hour(LocalTime.of(5, 0), LocalTime.of(6, 0));
        Hour seventh = new Hour(LocalTime.of(6, 0), LocalTime.of(7, 0));
        Hour eighth = new Hour(LocalTime.of(7, 0), LocalTime.of(8, 0));

        List<Hour> hours = List.of(first, second, third, fourth, fifth, sixth, seventh, eighth);

        hours.forEach(hour -> hour.setSchedule(schedule));

        if (schedule.getWorkStatus().toString().equalsIgnoreCase("work")) {
            for (Hour hour : hours) {
                hour.setSchedule(schedule);
            }
            hourRepository.saveAll(hours);
            schedule.setHours(hours);
        }
    }

    @Transactional
    public void removeHourById(Integer hourId) {
        Hour hour = hourRepository.findById(hourId).orElseThrow();
        Schedule schedule = hour.getSchedule();
        hour.setSchedule(null);
        hour.setEnd(null);
        hour.setStart(null);
        hour.setGiftExists(false);
        hour.setSwapExists(false);
        hour.setRotationItem(null);
        List<Hour> hours = schedule.getHours();

        hours.remove(hour);
        schedule.setTotalHours(schedule.getTotalHours() - 1);
        schedule.setHours(hours);
        scheduleRepository.save(schedule);
        hourRepository.save(hour);
        LOGGER.info("Hour with ID: " + hourId + " removed successfully!");

    }

    @Transactional
    public List<HourDTO> addNewHour(AddNewHourRequest request) {
        Schedule schedule = scheduleRepository.findById(request.scheduleId())
                .orElseThrow(ScheduleNotFoundException::new);

        if (hourRepository.existsByStartAndEndAndSchedule_ScheduleId(request.start(), request.end(), request.scheduleId())) {
            throw new HourAdditionValidationException();
        }
        Hour hour = new Hour(request.start(), request.end());
        hour.setSchedule(schedule);
        hour.setGiftExists(false);
        hour.setSwapExists(false);
        hour.setRotationItem(null);

        schedule.getHours().add(hour);
        schedule.setTotalHours(schedule.getTotalHours() + 1);
        schedule.setWorkStatus(StatusEnum.WORK);
        hourRepository.save(hour);
        Schedule save = scheduleRepository.save(schedule);
        List<HourDTO> hourDTOList = new ArrayList<>();
        for (Hour h : save.getHours()) {
            hourDTOList.add(mapHourToDTO(h));
        }
        return hourDTOList;
    }

    public double getWeekHours(Schedule schedule) {
        LOGGER.info("Calculating week hours");
        List<Schedule> weeklyScheduleList = getWeeklySchedule(schedule);
        double weekHours = 0;
        for (Schedule weekSchedule : weeklyScheduleList) {
            weekHours += weekSchedule.getTotalHours();
        }
        return weekHours;
    }

    public double getMonthHours(Schedule schedule) {
        LOGGER.info("Calculating month hours");
        List<Schedule> monthlyScheduleList = getMonthlySchedule(schedule);
        double monthHours = 0;
        for (Schedule monthSchedule : monthlyScheduleList) {
            monthHours += monthSchedule.getTotalHours();
        }
        return monthHours;
    }

    public boolean isValidMinimumLimit(Hour hour, int hourAmount) {
        LOGGER.info("Validating monthly minimum limit");
        double monthHours = getMonthHours(hour.getSchedule());
        return monthHours - hourAmount >= 40;
    }

    public boolean checkNextMonthRequest(LocalDate targetDate) {
        LOGGER.info("Validating if request is made after 20:15 of month's last day");
        LocalDateTime publishDateTime = LocalDateTime.now();
        if (publishDateTime.toLocalDate().plusMonths(1).getMonthValue() != targetDate.getMonthValue()) {
            LOGGER.error("Target date is not next month");
            return false;
        }
        if (publishDateTime.toLocalTime().isBefore(LocalTime.of(20, 14, 59))) {
            LOGGER.error("Publish time is before 20:15");
            return false;
        }

        return true;
    }

    public boolean validateHours(Schedule schedule, LocalTime start, LocalTime end) {
        LOGGER.info("Started hours validation");
        return !checkHourOccupied(schedule, start, end)
                && checkDailyHoursLimit(schedule, start, end)
                && checkWeekHoursLimit(schedule, start, end)
                && checkMonthlyHoursLimit(schedule, start, end);
    }

    private boolean checkHourOccupied(Schedule schedule, LocalTime start, LocalTime end) {
        LOGGER.info("Started checking if provided hour is occupied or not");
        List<Hour> hours = schedule.getHours();
        for (Hour hour : hours) {
            if (start.isBefore(hour.getEnd()) && end.isAfter(hour.getStart())) {
                LOGGER.error("Current hour is occupied");
                throw new HourIsOccupiedException();
            }
        }
        return false;
    }

    private boolean checkDailyHoursLimit(Schedule schedule, LocalTime start, LocalTime end) {
        LOGGER.info("Started checking daily hours limit");
        double currentDayHours = schedule.getTotalHours();
        double hours = Duration.between(start, end).toHours();
        if (currentDayHours + hours > 12) {
            LOGGER.error("Hour addition exceeds daily limit");
            throw new DailyHoursLimitExceedsException();
        }
        return true;
    }

    private boolean checkWeekHoursLimit(Schedule schedule, LocalTime start, LocalTime end) {
        LOGGER.info("Started checking week hours limit");
        double currentWeekHours = getWeekHours(schedule);
        double hours = Duration.between(start, end).toHours();
        if (currentWeekHours + hours > 40) {
            LOGGER.error("Hour addition exceeds week limit");
            throw new WeeklyHoursLimitExceedsException();
        }
        return true;
    }

    private boolean checkMonthlyHoursLimit(Schedule schedule, LocalTime start, LocalTime end) {
        LOGGER.info("Started checking month hours limit");
        double currentMonthHours = getMonthHours(schedule);
        double hours = Duration.between(start, end).toHours();
        if (currentMonthHours + hours > 160) {
            LOGGER.error("Hour addition exceeds month limit");
            throw new MonthlyHoursLimitExceedsException();
        }
        return true;
    }

    private List<Schedule> getMonthlySchedule(Schedule schedule) {
        LocalDate startOfMonth = schedule.getDate().with(TemporalAdjusters.firstDayOfMonth());
        LocalDate endOfMonth = schedule.getDate().with(TemporalAdjusters.lastDayOfMonth());
        return scheduleRepository.findAllByDateBetweenAndEmployee_WorkId(startOfMonth, endOfMonth, schedule.getEmployee().getWorkId());
    }

    private List<Schedule> getWeeklySchedule(Schedule schedule) {
        LocalDate startOfWeek = schedule.getDate().with(DayOfWeek.MONDAY);
        LocalDate endOfWeek = startOfWeek.plusDays(6);
        return scheduleRepository.findAllByDateBetweenAndEmployee_WorkId(startOfWeek, endOfWeek, schedule.getEmployee().getWorkId());
    }

    public boolean checkNextHourGap(Hour hourToAddEnd, Hour nextHour) {
        LOGGER.info("Checking gap between current and next hour");
        LocalDate hourToAddDate = hourToAddEnd.getSchedule().getDate();
        LocalTime hourToAddTime = hourToAddEnd.getEnd();
        LocalDateTime hourToAddDateTime = LocalDateTime.of(hourToAddDate, hourToAddTime);

        LocalDate nextHourDate = nextHour.getSchedule().getDate();
        LocalTime nextHourTime = nextHour.getStart();
        LocalDateTime nextHourDateTime = LocalDateTime.of(nextHourDate, nextHourTime);
        long hours = Duration.between(hourToAddDateTime, nextHourDateTime.minusHours(1)).toHours();
        return getDifference(hours);
    }

    private boolean getDifference(long hours) {
        LOGGER.info("Duration between hours is - " + hours);

        if (hours < 0) {
            LOGGER.error("Hours overlap");
            return false;
        } else if (hours == 0) {
            return true;
        } else if (hours < 12) {
            LOGGER.error("Gap is less than 12");
            return false;
        }
        return true;
    }

    public boolean checkPreviousHourGap(Hour hourToAddStart, Hour previousHour) {
        LOGGER.info("Checking gap between current and previous hour");
        LocalDate hourToAddDate = hourToAddStart.getSchedule().getDate();
        LocalTime hourToAddTime = hourToAddStart.getStart();
        LocalDateTime hourToAddDateTime = LocalDateTime.of(hourToAddDate, hourToAddTime);

        LocalDate previousHourDate = previousHour.getSchedule().getDate();
        LocalTime previousHourTime = previousHour.getEnd();
        LocalDateTime previousHourDateTime = LocalDateTime.of(previousHourDate, previousHourTime);
        long hours = Duration.between(previousHourDateTime, hourToAddDateTime).toHours();
        return getDifference(hours);
    }

    public Hour findLastHourBefore(Schedule schedule, LocalTime time) {
        List<Hour> hours = schedule.getHours();
        hours.sort(Comparator.comparing(Hour::getStart));
        Hour lastHourBefore = null;

        for (Hour hour : hours) {
            if (hour.getEnd().isBefore(time) || hour.getEnd().equals(time)) {
                lastHourBefore = hour;
            } else {
                break;
            }
        }

        return lastHourBefore;
    }

    public Hour findFirstHourAfter(Schedule schedule, LocalTime time) {
        List<Hour> hours = schedule.getHours();
        hours.sort(Comparator.comparing(Hour::getStart));
        for (Hour hour : hours) {
            if (hour.getStart().isAfter(time)) {
                return hour;
            }
        }
        return null;
    }

    public boolean checkHoursAmount(List<Hour> hoursToAddIds) {
        if (hoursToAddIds.size() == 1 || hoursToAddIds.isEmpty()) {
            LOGGER.error("Only one hour can not be swapped/Hours are not provided");
            return false;
        }
        return true;
    }

    public boolean checkFirstOrLastHour(List<Hour> hoursToAdd) {
        LOGGER.info("Checking if first hour of schedule is left alone");
        List<Hour> scheduleHours = hoursToAdd.get(0).getSchedule().getHours();
        scheduleHours.sort(new Comparator<Hour>() {
            @Override
            public int compare(Hour o1, Hour o2) {
                return o1.getStart().getHour() - o2.getStart().getHour();
            }
        });
        if (hoursToAdd.get(0) == scheduleHours.get(1)) {
            LOGGER.error("First hour of a schedule can not be left alone");
            return false;
        }

        LOGGER.info("Checking if last hour of schedule is left alone");
        if (hoursToAdd.get(hoursToAdd.size() - 1) == scheduleHours.get(scheduleHours.size() - 2)) {
            LOGGER.error("Last hour of a schedule can not be left alone");
            return false;
        }

        return true;
    }

    public boolean checkHoursChain(List<Hour> hoursToAdd) {
        LOGGER.info("Checking if hours are chained");
        List<Hour> scheduleHours = hoursToAdd.get(0).getSchedule().getHours();
        scheduleHours.sort(new Comparator<Hour>() {
            @Override
            public int compare(Hour o1, Hour o2) {
                return o1.getStart().getHour() - o2.getStart().getHour();
            }
        });
        int firstHourToAddIndexInSchedule = scheduleHours.indexOf(hoursToAdd.get(0));
        int i = 0;
        while (i < hoursToAdd.size()) {
            if (hoursToAdd.get(i) != scheduleHours.get(firstHourToAddIndexInSchedule)) {
                LOGGER.error("Hours are not chained");
                return false;
            }
            i++;
            firstHourToAddIndexInSchedule++;
        }
        return true;
    }

    public boolean checkIsBeforeRotation(Schedule schedule) {
        LOGGER.info("Checking if duration between now and schedule is more than 4 hours");
        if (Duration.between(LocalDateTime.now(), LocalDateTime.of(schedule.getDate(), schedule.getHours().get(0).getStart())).toMinutes() >= 240) {
            return true;
        }
        LOGGER.error("Gap is less than 4, you are not eligible to do this action anymore");
        throw new SwapOrGiftIsRestrictedException();
    }


    public boolean validateSwapTargetHours(Schedule targetSchedule, LocalTime targetStart,
                                           LocalTime targetEnd, LocalDate swapDate,
                                           List<Hour> hourList) {
        LOGGER.info("Started hours validation");
        return validateTargetHourOccupied(targetSchedule, targetStart, targetEnd, swapDate, hourList)
                && validateTargetDayLimit(targetSchedule, targetStart, targetEnd, swapDate, hourList.size())
                && validateTargetWeekLimit(targetSchedule, targetStart, targetEnd, swapDate, hourList.size())
                && validateTargetMonthlyHoursLimit(targetSchedule, targetStart, targetEnd, swapDate, hourList.size());
    }

    private boolean validateTargetHourOccupied(Schedule targetSchedule, LocalTime targetStart,
                                               LocalTime targetEnd, LocalDate swapDate,
                                               List<Hour> hourList) {
        LOGGER.info("Started checking if provided target hour is occupied or not");
        List<Hour> targetHours = targetSchedule.getHours();
        if (targetSchedule.getDate() == swapDate) {
            hourList.forEach(hour -> {
                targetHours.forEach(targetHour -> {
                    if (targetHour.getStart().equals(hour.getStart())) {
                        targetHours.remove(targetHour);
                    }
                });
            });
        }

        for (Hour targetHour : targetHours) {
            if (targetStart.isBefore(targetHour.getEnd()) && targetEnd.isAfter(targetHour.getStart())) {
                LOGGER.error("Current hour is occupied");
                throw new HourIsOccupiedException();
            }
        }

        return true;
    }

    private boolean validateTargetDayLimit(Schedule targetSchedule, LocalTime targetStart,
                                           LocalTime targetEnd, LocalDate swapDate,
                                           Integer hoursToBeSwappedAmount) {
        LOGGER.info("Started checking target daily hours limit");
        double currentDayHours;
        if (targetSchedule.getDate() != swapDate) {
            currentDayHours = targetSchedule.getTotalHours();
        } else {
            currentDayHours = targetSchedule.getTotalHours() - hoursToBeSwappedAmount;
        }

        double targetHours = Duration.between(targetStart, targetEnd).toHours();
        if (currentDayHours + targetHours > 12) {
            LOGGER.error("Hour addition exceeds daily limit");
            throw new DailyHoursLimitExceedsException();
        }

        return true;
    }

    private boolean validateTargetWeekLimit(Schedule targetSchedule, LocalTime start,
                                            LocalTime end, LocalDate swapDate,
                                            Integer hoursToBeSwappedAmount) {
        LOGGER.info("Started checking target week hours limit");
        double currentWeekHours;
        if (targetSchedule.getDate() != swapDate) {
            currentWeekHours = targetSchedule.getTotalHours();
        } else {
            currentWeekHours = targetSchedule.getTotalHours() - hoursToBeSwappedAmount;
        }
        double hours = Duration.between(start, end).toHours();
        if (currentWeekHours + hours > 40) {
            LOGGER.error("Hour addition exceeds target week limit");
            throw new WeeklyHoursLimitExceedsException();
        }
        return true;
    }

    private boolean validateTargetMonthlyHoursLimit(Schedule targetSchedule, LocalTime targetStart,
                                                    LocalTime targetEnd, LocalDate swapDate,
                                                    Integer hoursToBeSwappedAmount) {
        LOGGER.info("Started checking month hours limit");
        double currentMonthHours;
        if (targetSchedule.getDate() != swapDate) {
            currentMonthHours = targetSchedule.getTotalHours();
        } else {
            currentMonthHours = targetSchedule.getTotalHours() - hoursToBeSwappedAmount;
        }
        double hours = Duration.between(targetStart, targetEnd).toHours();
        if (currentMonthHours + hours > 160) {
            LOGGER.error("Hour addition exceeds month limit");
            throw new MonthlyHoursLimitExceedsException();
        }
        return true;
    }

    public boolean validateGap(Hour firstHour, Hour lastHour, Schedule targetSchedule) {
        LocalTime start = firstHour.getStart();
        LocalTime end = lastHour.getEnd();

        // Fetch previous and next hours
        Hour previousHour = findLastHourBefore(targetSchedule, start);
        Hour nextHour = findFirstHourAfter(targetSchedule, end);

        // Validate gaps
        boolean validPreviousGap = (previousHour == null) || checkPreviousHourGap(firstHour, previousHour);
        boolean validNextGap = (nextHour == null) || checkNextHourGap(lastHour, nextHour);

        return validPreviousGap && validNextGap;
    }

}
