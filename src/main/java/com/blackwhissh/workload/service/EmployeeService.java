package com.blackwhissh.workload.service;

import com.blackwhissh.workload.dto.EmployeeDTO;
import com.blackwhissh.workload.dto.request.RegisterEmployeeRequest;
import com.blackwhissh.workload.dto.response.RegisterEmployeeResponse;
import com.blackwhissh.workload.entity.Employee;
import com.blackwhissh.workload.entity.User;
import com.blackwhissh.workload.entity.enums.RoleEnum;
import com.blackwhissh.workload.entity.enums.ShiftEnum;
import com.blackwhissh.workload.exceptions.list.EmployeeAlreadyExistsException;
import com.blackwhissh.workload.exceptions.list.EmployeeNotFoundException;
import com.blackwhissh.workload.repository.EmployeeRepository;
import com.blackwhissh.workload.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class EmployeeService {
    public static final Logger LOGGER = LoggerFactory.getLogger(EmployeeService.class);
    private final EmployeeRepository employeeRepository;
    private final ScheduleService scheduleService;
    private final UserRepository userRepository;

    public EmployeeService(EmployeeRepository employeeRepository, ScheduleService scheduleService, UserRepository userRepository) {
        this.employeeRepository = employeeRepository;
        this.scheduleService = scheduleService;
        this.userRepository = userRepository;
    }

    @Transactional
    public RegisterEmployeeResponse registerEmployee(RegisterEmployeeRequest request) {
        if (request.set() > 2 || request.set() <= 0) throw new IllegalArgumentException("Wrong set number provided");
        if (employeeRepository.existsByWorkId(request.workId())) {
            LOGGER.error("Employee with this work ID already exists");
            throw new EmployeeAlreadyExistsException();
        }
        ShiftEnum shift = ShiftEnum.valueOf(request.shift().toUpperCase());

        User user = new User(true, RoleEnum.EMPLOYEE);
        user.setEmail(request.email());

        userRepository.save(user);

        Employee employee = new Employee(
                request.workId(),
                shift,
                request.set()
        );

        employee.setUser(user);
        employeeRepository.save(employee);
        LOGGER.info("employee registered successfully");
        scheduleService.generateSchedule(employee);
        return new RegisterEmployeeResponse(
                new EmployeeDTO(
                        employee.getId(),
                        employee.getWorkId(),
                        employee.getShift(),
                        employee.getSet()
                )
        );
    }

    public Employee findByWorkId(String workId) {
        return employeeRepository.findByWorkId(workId)
                .orElseThrow(EmployeeNotFoundException::new);
    }
}
