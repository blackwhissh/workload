package com.blackwhissh.workload.service;

import com.blackwhissh.workload.dto.HourDTO;
import com.blackwhissh.workload.dto.request.AddNewHourRequest;
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
import java.util.List;
import java.util.Optional;

@Service
public class HourService {
    private final static Logger LOGGER = LoggerFactory.getLogger(HourService.class);
    private final ScheduleRepository scheduleRepository;
    private final HourRepository hourRepository;

    public HourService(ScheduleRepository scheduleRepository, HourRepository hourRepository) {
        this.scheduleRepository = scheduleRepository;
        this.hourRepository = hourRepository;
    }

    public List<HourDTO> getHoursByScheduleId(Integer scheduleId) {
        LOGGER.info("Started get hours by schedule with ID: " + scheduleId);
        Schedule schedule = scheduleRepository.findById(scheduleId).orElseThrow(ScheduleNotFoundException::new);
        List<HourDTO> hourDTOList = new ArrayList<>();
        schedule.getHours().forEach(hour -> hourDTOList.add(new HourDTO(hour.getId(), hour.getStart(), hour.getEnd(), hour.getSwapExists(), hour.getGiftExists())));
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
        List<Hour> hours = schedule.getHours();
        if (hours.get(0) == hour || hours.get(hours.size() - 1) == hour) {
            LOGGER.error("First or last hour of day can not be deleted");
            throw new HourRemoveException();
        } else {
            hours.remove(hour);
            schedule.setTotalHours(schedule.getTotalHours() - 2);
            schedule.setHours(hours);
            scheduleRepository.save(schedule);
            LOGGER.info("Hour with ID: " + hourId + " removed successfully!");
        }
    }

    @Transactional
    public List<HourDTO> addNewHour(AddNewHourRequest request) {
        Schedule schedule = scheduleRepository.findById(request.scheduleId())
                .orElseThrow(ScheduleNotFoundException::new);

        if (validateHours(schedule, request.start(), request.end())) {
            double diff = Duration.between(request.start(), request.end()).toHours();
            schedule.setTotalHours(schedule.getTotalHours() + diff);
            List<Hour> hours = schedule.getHours();
            hours.add(hourRepository.save(new Hour(request.start(), request.end())));
            schedule.setHours(hours);
            scheduleRepository.save(schedule);
            List<HourDTO> hourDTOList = new ArrayList<>();
            for (Hour hour : hours) {
                hourDTOList.add(new HourDTO(hour.getId(), hour.getStart(), hour.getEnd(), hour.getSwapExists(), hour.getGiftExists()));
            }
            LOGGER.info("Add hour validated successfully");
            return hourDTOList;
        }
        LOGGER.error("Error during validation");
        throw new HourAdditionValidationException();
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

    public boolean validateHours(Schedule schedule, LocalTime start, LocalTime end) {
        LOGGER.info("Started hours validation");
        return !checkHourOccupied(schedule, start, end)
                && checkDailyHoursLimit(schedule, start, end)
                && checkWeekHoursLimit(schedule, start, end)
                && checkMonthlyHoursLimit(schedule, start, end)
                && checkNextScheduleHour(schedule, end);
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

    private boolean checkNextScheduleHour(Schedule schedule, LocalTime end) {
        boolean result = true;
        LOGGER.info("Checking gap between schedules");
        Optional<Schedule> nextScheduleOptional = scheduleRepository.findFirstByDateAfterAndWorkStatus(schedule.getDate(), StatusEnum.WORK);
        if (nextScheduleOptional.isPresent()) {
            Schedule nextSchedule = nextScheduleOptional.get();
            LocalDateTime currentDateTime = LocalDateTime.of(schedule.getDate().getYear(), schedule.getDate().getMonthValue(), schedule.getDate().getDayOfMonth(), end.getHour(), end.getMinute());
            LocalDateTime nextDateTime = LocalDateTime.of(nextSchedule.getDate().getYear(), nextSchedule.getDate().getMonthValue(), nextSchedule.getDate().getDayOfMonth(), nextSchedule.getHours().get(0).getStart().getHour(), nextSchedule.getHours().get(0).getStart().getMinute());
            double diff = Duration.between(currentDateTime, nextDateTime).toHours();
            if (diff < 12) {
                LOGGER.error("Gap is less than 12");
                throw new ScheduleGapException();
            }
        }

        return result;
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
}
