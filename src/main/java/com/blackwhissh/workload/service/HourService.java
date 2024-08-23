package com.blackwhissh.workload.service;

import com.blackwhissh.workload.entity.Hour;
import com.blackwhissh.workload.entity.Schedule;
import com.blackwhissh.workload.exceptions.list.WorkScheduleNotFoundException;
import com.blackwhissh.workload.repository.HourRepository;
import com.blackwhissh.workload.repository.ScheduleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.List;

@Service
public class HourService {
    private final static Logger LOGGER = LoggerFactory.getLogger(HourService.class);
    private final ScheduleRepository scheduleRepository;
    private final HourRepository hourRepository;

    public HourService(ScheduleRepository scheduleRepository, HourRepository hourRepository) {
        this.scheduleRepository = scheduleRepository;
        this.hourRepository = hourRepository;
    }

    public List<Hour> getHoursByScheduleId(Integer scheduleId) {
        LOGGER.info("Started get hours by schedule with ID: " + scheduleId);
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(WorkScheduleNotFoundException::new);
        return schedule.getHours();
    }

    public void generateMorningHours(Schedule schedule) {
        Hour first = new Hour(LocalTime.of(8, 0), LocalTime.of(10, 0));
        Hour second = new Hour(LocalTime.of(10, 0), LocalTime.of(12, 0));
        Hour third = new Hour(LocalTime.of(12, 0), LocalTime.of(14, 0));
        Hour fourth = new Hour(LocalTime.of(14, 0), LocalTime.of(16, 0));
        List<Hour> hours = List.of(first, second, third, fourth);
        if (schedule.getWorkStatus().toString().equalsIgnoreCase("work")) {
            hourRepository.saveAll(hours);
            schedule.setHours(hours);
        }
    }

    public void generateDayHours(Schedule schedule) {
        Hour first = new Hour(LocalTime.of(16, 0), LocalTime.of(18, 0));
        Hour second = new Hour(LocalTime.of(18, 0), LocalTime.of(20, 0));
        Hour third = new Hour(LocalTime.of(20, 0), LocalTime.of(22, 0));
        Hour fourth = new Hour(LocalTime.of(22, 0), LocalTime.of(0, 0));
        List<Hour> hours = List.of(first, second, third, fourth);
        if (schedule.getWorkStatus().toString().equalsIgnoreCase("work")) {
            hourRepository.saveAll(hours);
            schedule.setHours(hours);
        }
    }

    public void generateNightHours(Schedule schedule) {
        Hour first = new Hour(LocalTime.of(0, 0), LocalTime.of(2, 0));
        Hour second = new Hour(LocalTime.of(2, 0), LocalTime.of(4, 0));
        Hour third = new Hour(LocalTime.of(4, 0), LocalTime.of(6, 0));
        Hour fourth = new Hour(LocalTime.of(6, 0), LocalTime.of(8, 0));
        List<Hour> hours = List.of(first, second, third, fourth);

        if (schedule.getWorkStatus().toString().equalsIgnoreCase("work")) {
            hourRepository.saveAll(hours);
            schedule.setHours(hours);
        }
    }
}
