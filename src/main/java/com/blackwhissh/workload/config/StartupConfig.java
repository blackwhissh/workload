package com.blackwhissh.workload.config;

import com.blackwhissh.workload.dto.request.RegisterEmployeeRequest;
import com.blackwhissh.workload.entity.Employee;
import com.blackwhissh.workload.entity.User;
import com.blackwhissh.workload.entity.enums.RoleEnum;
import com.blackwhissh.workload.repository.EmployeeRepository;
import com.blackwhissh.workload.repository.UserRepository;
import com.blackwhissh.workload.service.EmployeeService;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class StartupConfig {
    private final UserRepository userRepository;
    private final EmployeeRepository employeeRepository;
    private final EmployeeService employeeService;

    public StartupConfig(UserRepository userRepository, EmployeeRepository employeeRepository, EmployeeService employeeService) {
        this.userRepository = userRepository;
        this.employeeRepository = employeeRepository;
        this.employeeService = employeeService;
    }

    @PostConstruct
    private void addManager() {
        User user = new User();
        user.setEmail("manager");
        user.setActive(true);
        user.setPassword(PasswordConfig.passwordEncoder().encode("manager"));
        user.setRole(RoleEnum.MANAGER);
        user.setRegistrationDate(LocalDate.now());
        user.setTempPass(PasswordConfig.passwordEncoder().encode("manager"));
        userRepository.save(user);

        Employee employee = new Employee();
        employee.setUser(user);
        employee.setWorkId("manager");
        employeeRepository.save(employee);
    }

    @PostConstruct
    private void addEmployees() {
        RegisterEmployeeRequest request1= new RegisterEmployeeRequest("morning", "emp1", "001", 1);
        RegisterEmployeeRequest request2= new RegisterEmployeeRequest("morning", "emp2", "002", 1);
        RegisterEmployeeRequest request3= new RegisterEmployeeRequest("morning", "emp3", "003", 2);
        RegisterEmployeeRequest request4= new RegisterEmployeeRequest("night", "emp4", "004", 1);

        employeeService.registerEmployee(request1);
        employeeService.registerEmployee(request2);
        employeeService.registerEmployee(request3);
        employeeService.registerEmployee(request4);
    }
}
