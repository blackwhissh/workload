package com.blackwhissh.workload.config;

import com.blackwhissh.workload.dto.request.RegisterEmployeeRequest;
import com.blackwhissh.workload.entity.Employee;
import com.blackwhissh.workload.entity.Studio;
import com.blackwhissh.workload.entity.User;
import com.blackwhissh.workload.entity.enums.RoleEnum;
import com.blackwhissh.workload.entity.enums.RotationAction;
import com.blackwhissh.workload.repository.EmployeeRepository;
import com.blackwhissh.workload.repository.StudioRepository;
import com.blackwhissh.workload.repository.UserRepository;
import com.blackwhissh.workload.service.EmployeeService;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class StartupConfig {
    private final UserRepository userRepository;
    private final EmployeeRepository employeeRepository;
    private final EmployeeService employeeService;
    private final StudioRepository studioRepository;

    public StartupConfig(UserRepository userRepository, EmployeeRepository employeeRepository, EmployeeService employeeService, StudioRepository studioRepository) {
        this.userRepository = userRepository;
        this.employeeRepository = employeeRepository;
        this.employeeService = employeeService;
        this.studioRepository = studioRepository;
    }

    @PostConstruct
    @Transactional
    public void addManager() {
        User user = new User();
        user.setEmail("manager");
        user.setActive(true);
        user.setPassword(PasswordConfig.passwordEncoder().encode("manager"));
        user.setRole(RoleEnum.MANAGER);
        user.setRegistrationDate(LocalDate.now());

        Employee employee = new Employee();
        user.setEmployee(employee);
        employee.setUser(user);
        employee.setWorkId("manager");
        userRepository.save(user);
        employeeRepository.save(employee);


    }

    @PostConstruct
    private void addEmployees() {

        RegisterEmployeeRequest request1= new RegisterEmployeeRequest(
                "morning", "emp1", "001", 1, "Mikheil", "Saakashvili", "59001124232",
                LocalDate.of(2003, 5, 8), "598928434", "Shindisi Highway 11","574777721",
                LocalDate.of(2019,5,10), "Dealer", "12345678");
        RegisterEmployeeRequest request2= new RegisterEmployeeRequest(
                "morning", "emp2", "002", 1, "Mikheil", "Saakashvili", "59001124423",
                LocalDate.of(2003, 5, 8), "598928434", "Shindisi Highway 11","574777721",
                LocalDate.of(2019,5,10), "Dealer", "12345678");
        RegisterEmployeeRequest request3= new RegisterEmployeeRequest(
                "morning", "emp3", "003", 2, "Mikheil", "Saakashvili", "59001124532",
                LocalDate.of(2003, 5, 8), "598928434", "Shindisi Highway 11","574777721",
                LocalDate.of(2019,5,10), "Dealer", "12345678");
        RegisterEmployeeRequest request4= new RegisterEmployeeRequest(
                "night", "emp4", "004", 1, "Mikheil", "Saakashvili", "590032124232",
                LocalDate.of(2003, 5, 8), "598928434", "Shindisi Highway 11","574777721",
                LocalDate.of(2019,5,10), "Dealer", "12345678");
        RegisterEmployeeRequest request5= new RegisterEmployeeRequest(
                "day", "emp5", "005", 1, "Mikheil", "Saakashvili", "59431124232",
                LocalDate.of(2003, 5, 8), "598928434", "Shindisi Highway 11","574777721",
                LocalDate.of(2019,5,10), "Dealer", "12345678");

        employeeService.registerEmployee(request1);
        employeeService.registerEmployee(request2);
        employeeService.registerEmployee(request3);
        employeeService.registerEmployee(request4);
        employeeService.registerEmployee(request5);
    }

    @PostConstruct
    private void addStudios() {


        Studio first = new Studio();
        List<RotationAction> firstActions = List.of(
                RotationAction.SICK_LEAVE,
                RotationAction.VACATION,
                RotationAction.HALF_BREAK,
                RotationAction.FULL_BREAK,
                RotationAction.ROULETTE,
                RotationAction.BACCARAT,
                RotationAction.DOMINO);
        first.setAvailableActions(firstActions);
        first.setBonus(false);
        studioRepository.save(first);

        Studio second = new Studio();
        List<RotationAction> secondActions = List.of(
                RotationAction.SICK_LEAVE,
                RotationAction.VACATION,
                RotationAction.HALF_BREAK,
                RotationAction.FULL_BREAK,
                RotationAction.D12,
                RotationAction.DNT,
                RotationAction.DICE
        );
        second.setAvailableActions(secondActions);
        second.setBonus(false);
        studioRepository.save(second);

        Studio third = new Studio();
        List<RotationAction> thirdActions = List.of(
                RotationAction.SICK_LEAVE,
                RotationAction.VACATION,
                RotationAction.HALF_BREAK,
                RotationAction.FULL_BREAK,
                RotationAction.SOCCER
        );
        third.setAvailableActions(thirdActions);
        third.setBonus(true);
        studioRepository.save(third);

        Studio fourth = new Studio();
        List<RotationAction> fourthActions = List.of(
                RotationAction.SICK_LEAVE,
                RotationAction.VACATION,
                RotationAction.HALF_BREAK,
                RotationAction.FULL_BREAK,
                RotationAction.D24
        );
        fourth.setAvailableActions(fourthActions);
        fourth.setBonus(true);
        studioRepository.save(fourth);
    }
}
