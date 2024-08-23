package com.blackwhissh.workload.service;

import com.blackwhissh.workload.dto.HourDTO;
import com.blackwhissh.workload.dto.request.ScheduleByYearMonthAndWorkIdRequest;
import com.blackwhissh.workload.dto.request.ScheduleByYearMonthRequest;
import com.blackwhissh.workload.dto.response.ScheduleByYearMonthResponse;
import com.blackwhissh.workload.entity.Employee;
import com.blackwhissh.workload.entity.Schedule;
import com.blackwhissh.workload.entity.enums.ShiftEnum;
import com.blackwhissh.workload.entity.enums.StatusEnum;
import com.blackwhissh.workload.exceptions.list.EmployeeNotFoundException;
import com.blackwhissh.workload.exceptions.list.WrongMonthException;
import com.blackwhissh.workload.repository.EmployeeRepository;
import com.blackwhissh.workload.repository.ScheduleRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Service
public class ScheduleService {
    private final static Logger LOGGER = LoggerFactory.getLogger(ScheduleService.class);
    private final ScheduleRepository scheduleRepository;
    private final EmployeeRepository employeeRepository;
    private final HourService hourService;

    public ScheduleService(ScheduleRepository scheduleRepository, EmployeeRepository employeeRepository, HourService hourService) {
        this.scheduleRepository = scheduleRepository;
        this.employeeRepository = employeeRepository;
        this.hourService = hourService;
    }

    @Transactional
    public void generateHours(Employee employee, Schedule schedule) {
        LOGGER.info("Started generating hours");
        if (employee.getShift().equals(ShiftEnum.MORNING)) {
            hourService.generateMorningHours(schedule);
        } else if (employee.getShift().equals(ShiftEnum.DAY)) {
            hourService.generateDayHours(schedule);
        } else if (employee.getShift().equals(ShiftEnum.NIGHT)) {
            hourService.generateNightHours(schedule);
        }
        LOGGER.info("Hours generated successfully");
    }

    @Transactional
    public void generateSchedule(Employee employee) {
        LOGGER.info("Started generating schedule for employee with WorkID: " + employee.getWorkId());
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusYears(1);
        List<Schedule> scheduleList = new ArrayList<>();

        while (startDate.isBefore(endDate)) {
            Schedule schedule = new Schedule();
            schedule.setEmployee(employee);
            schedule.setDate(startDate);

            long daysFromStart = startDate.getDayOfYear() % 6;
            if (employee.getSet() == 1) {
                schedule.setWorkStatus((daysFromStart < 3) ? StatusEnum.WORK : StatusEnum.REST);
            } else {
                schedule.setWorkStatus((daysFromStart >= 3) ? StatusEnum.WORK : StatusEnum.REST);
            }

            generateHours(employee, schedule);
            if (schedule.getHours() != null && !schedule.getHours().isEmpty()) {
                schedule.setTotalHours(8d);
            } else {
                schedule.setTotalHours(0d);
            }
            scheduleList.add(schedule);
            startDate = startDate.plusDays(1);
        }
        LOGGER.info("Schedule generated successfully for employee with WorkID: " + employee.getWorkId());
        scheduleRepository.saveAll(scheduleList);
    }

    public List<Schedule> getScheduleByYearMonthAndWorkId(ScheduleByYearMonthAndWorkIdRequest request) {
        if (request.month() > 12 || request.month() <= 0) throw new WrongMonthException();
        Employee employee = employeeRepository.findByWorkId(request.workId()).orElseThrow(EmployeeNotFoundException::new);
        LocalDate start = LocalDate.of(request.year(), request.month(), 1);
        int lastDayOfMonth = YearMonth.of(request.year(), request.month()).atEndOfMonth().getDayOfMonth();
        LocalDate end = LocalDate.of(request.year(), request.month(), lastDayOfMonth);
        return scheduleRepository.findAllByEmployeeAndDateBetween(employee, start, end);
    }

    public List<ScheduleByYearMonthResponse> getScheduleByYearMonth(ScheduleByYearMonthRequest request) {
        if (request.month() > 12 || request.month() <= 0) throw new WrongMonthException();
        LocalDate start = LocalDate.of(request.year(), request.month(), 1);
        int lastDayOfMonth = YearMonth.of(request.year(), request.month()).atEndOfMonth().getDayOfMonth();
        LocalDate end = LocalDate.of(request.year(), request.month(), lastDayOfMonth);

        List<Schedule> allByDateBetween = scheduleRepository.findAllByDateBetween(start, end);
        List<ScheduleByYearMonthResponse> scheduleByYearMonthList = new ArrayList<>();

        for (Schedule schedule : allByDateBetween) {
            List<HourDTO> hourDTOList = new ArrayList<>();
            schedule.getHours().forEach(hour ->
                    hourDTOList.add(new HourDTO(hour.getId(), hour.getStart(), hour.getEnd()))
            );

            scheduleByYearMonthList.add(new ScheduleByYearMonthResponse(
                    schedule.getScheduleId(),
                    schedule.getEmployee(),
                    schedule.getWorkStatus(),
                    schedule.getDate(),
                    hourDTOList,
                    schedule.getTotalHours()));
        }
        return scheduleByYearMonthList;
    }
}
