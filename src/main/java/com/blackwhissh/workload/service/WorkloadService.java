package com.blackwhissh.workload.service;

import com.blackwhissh.workload.entity.User;
import com.blackwhissh.workload.entity.WorkDay;
import com.blackwhissh.workload.entity.WorkHour;
import com.blackwhissh.workload.entity.WorkSchedule;
import com.blackwhissh.workload.entity.enums.WorkDayType;
import com.blackwhissh.workload.exceptions.list.UserNotFoundException;
import com.blackwhissh.workload.exceptions.list.WorkDayNotFoundException;
import com.blackwhissh.workload.exceptions.list.WorkScheduleNotFoundException;
import com.blackwhissh.workload.exceptions.list.WrongWorkDayTypeException;
import com.blackwhissh.workload.repository.UserRepository;
import com.blackwhissh.workload.repository.WorkDayRepository;
import com.blackwhissh.workload.repository.WorkHourRepository;
import com.blackwhissh.workload.repository.WorkScheduleRepository;
import jakarta.transaction.Transactional;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.List;

@Service
public class WorkloadService {
    private final WorkScheduleRepository workScheduleRepository;
    private final WorkDayRepository workDayRepository;
    private final WorkHourRepository workHourRepository;
    private final UserRepository userRepository;

    public WorkloadService(WorkScheduleRepository workScheduleRepository, WorkDayRepository workDayRepository, WorkHourRepository workHourRepository, UserRepository userRepository) {
        this.workScheduleRepository = workScheduleRepository;
        this.workDayRepository = workDayRepository;
        this.workHourRepository = workHourRepository;
        this.userRepository = userRepository;
    }
    @Transactional
    public WorkSchedule createSchedule(String username, int year, int month){
        User user = userRepository.findByUsername(username).orElseThrow(UserNotFoundException::new);
        WorkSchedule workSchedule = new WorkSchedule(month, year, new ArrayList<>(),user);
        return workScheduleRepository.save(workSchedule);
    }
    @Transactional
    public WorkSchedule addWorkDay(Integer scheduleId, Integer day, String type, List<WorkHour> hours){
        WorkDayType workDayType;
        try {
            workDayType = WorkDayType.valueOf(type.toUpperCase());
        } catch (Exception e){
            throw new WrongWorkDayTypeException();
        }
        WorkSchedule workSchedule = workScheduleRepository.findById(scheduleId)
                .orElseThrow(WorkScheduleNotFoundException::new);

        LocalDate targetDate = LocalDate.of(workSchedule.getYear(), workSchedule.getMonth(), day);

        WorkDay workDay = new WorkDay();
        workDay.setWorkDay(targetDate);
        workDay.setType(workDayType);
        workDay.setWorkHours(hours);
        workDayRepository.save(workDay);

        workSchedule.getDays().add(workDay);
        return workScheduleRepository.save(workSchedule);
    }
    @Transactional
    public void addWorkHour(Integer workDayId, Integer hour, String studio, Double coefficient){
        WorkDay workDay = workDayRepository.findById(workDayId)
                .orElseThrow(WorkDayNotFoundException::new);
        WorkHour workHour = new WorkHour(hour, studio, coefficient);
        workHourRepository.save(workHour);
        workDay.getWorkHours().add(workHour);
        workDayRepository.save(workDay);
    }


    private int calculateTotalHoursForWeek(List<WorkDay> workDays, LocalDate targetDate){
        int totalHours = 0;
        int targetWeek = targetDate.get(ChronoField.ALIGNED_WEEK_OF_YEAR);

        for (WorkDay workDay : workDays) {
            if (workDay.getWorkDay().get(ChronoField.ALIGNED_WEEK_OF_YEAR) == targetWeek) {
                for (WorkHour workHour : workDay.getWorkHours()) {
                    totalHours += workHour.getHour();
                }
            }
        }

        return totalHours;
    }

    private int calculateTotalHoursForMonth(List<WorkDay> workDays, LocalDate targetDate){
        int totalHours = 0;
        int targetMonth = targetDate.getMonthValue();
        for (WorkDay workDay : workDays){
            if (workDay.getWorkDay().getMonthValue() == targetMonth){
                for (WorkHour workHour : workDay.getWorkHours()) {
                    totalHours += workHour.getHour();
                }
            }
        }
        return totalHours;
    }

    private boolean canAddWorkDay(WorkSchedule workSchedule, WorkDay newWorkDay) {
        int existingWeekHours = calculateTotalHoursForWeek(workSchedule.getDays(), newWorkDay.getWorkDay());
        int existingMonthHours = calculateTotalHoursForMonth(workSchedule.getDays(), newWorkDay.getWorkDay());
        int newDayHours = newWorkDay.getWorkHours().stream().mapToInt(WorkHour::getHour).sum();

        return existingWeekHours + newDayHours <= 40 && existingMonthHours + newDayHours <= 160;
    }
}
