package com.blackwhissh.workload.service;

import com.blackwhissh.workload.entity.WorkDay;
import com.blackwhissh.workload.entity.WorkHour;
import com.blackwhissh.workload.entity.WorkSchedule;
import com.blackwhissh.workload.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.util.List;

@Service
public class WorkloadService {
    private final UserRepository userRepository;

    public WorkloadService(UserRepository userRepository) {
        this.userRepository = userRepository;
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
