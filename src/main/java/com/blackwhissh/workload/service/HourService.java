package com.blackwhissh.workload.service;

import com.blackwhissh.workload.dto.HourDTO;
import com.blackwhissh.workload.dto.request.AddHourRequest;
import com.blackwhissh.workload.entity.Hour;
import com.blackwhissh.workload.entity.Schedule;
import com.blackwhissh.workload.exceptions.list.HourRemoveException;
import com.blackwhissh.workload.exceptions.list.ScheduleNotFoundException;
import com.blackwhissh.workload.repository.HourRepository;
import com.blackwhissh.workload.repository.ScheduleRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.ArrayList;
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

    public List<HourDTO> getHoursByScheduleId(Integer scheduleId) {
        LOGGER.info("Started get hours by schedule with ID: " + scheduleId);
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(ScheduleNotFoundException::new);
        List<HourDTO> hourDTOList = new ArrayList<>();
        schedule.getHours().forEach(hour ->
                hourDTOList.add(new HourDTO(hour.getId(), hour.getStart(), hour.getEnd()))
        );
        return hourDTOList;
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
        if (hours.get(0) == hour || hours.get(3) == hour) {
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

//    public List<HourDTO> addHour(AddHourRequest request) {
//        Schedule schedule = scheduleRepository.findById(request.scheduleId())
//                .orElseThrow(ScheduleNotFoundException::new);
//
//    }
//
//    private boolean checkWeekHours(Schedule schedule) {
//        schedule.getEmployee()
//    }
}
