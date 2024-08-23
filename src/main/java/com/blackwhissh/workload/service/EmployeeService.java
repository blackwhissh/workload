package com.blackwhissh.workload.service;

import com.blackwhissh.workload.dto.request.RegisterEmployeeRequest;
import com.blackwhissh.workload.dto.response.RegisterEmployeeResponse;
import com.blackwhissh.workload.entity.Employee;
import com.blackwhissh.workload.entity.Schedule;
import com.blackwhissh.workload.entity.enums.ShiftEnum;
import com.blackwhissh.workload.exceptions.list.EmployeeNotFoundException;
import com.blackwhissh.workload.repository.EmployeeRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeService {
    public static final Logger LOGGER = LoggerFactory.getLogger(EmployeeService.class);
    private final EmployeeRepository employeeRepository;
    private final ScheduleService scheduleService;

    public EmployeeService(EmployeeRepository employeeRepository, ScheduleService scheduleService) {
        this.employeeRepository = employeeRepository;
        this.scheduleService = scheduleService;
    }
    @Transactional
    public RegisterEmployeeResponse registerEmployee(RegisterEmployeeRequest request){
        if (request.set() > 2 || request.set() <= 0) throw new IllegalArgumentException("Wrong set number provided");
        if (employeeRepository.existsByWorkId(request.workId())){
            LOGGER.warn("Employee with this work ID already exists");
            throw new IllegalArgumentException();
        }
        ShiftEnum shift = ShiftEnum.valueOf(request.shift().toUpperCase());

        Employee employee = employeeRepository.save(new Employee(
                request.workId(),
                shift,
                request.set()
        ));
        LOGGER.info("employee registered successfully");
        scheduleService.generateSchedule(employee);
        return new RegisterEmployeeResponse(employee);
    }
    public Employee findByWorkId(String workId){
        return employeeRepository.findByWorkId(workId)
                .orElseThrow(EmployeeNotFoundException::new);
    }
}
