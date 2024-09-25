package com.blackwhissh.workload.service;

import com.blackwhissh.workload.config.PasswordConfig;
import com.blackwhissh.workload.dto.EmployeeDTO;
import com.blackwhissh.workload.dto.request.RegisterEmployeeRequest;
import com.blackwhissh.workload.dto.response.ChangeEmployeePasswordResponse;
import com.blackwhissh.workload.dto.response.EmployeeProfileResponse;
import com.blackwhissh.workload.dto.response.RegisterEmployeeResponse;
import com.blackwhissh.workload.entity.Employee;
import com.blackwhissh.workload.entity.Hour;
import com.blackwhissh.workload.entity.Schedule;
import com.blackwhissh.workload.entity.User;
import com.blackwhissh.workload.entity.enums.RoleEnum;
import com.blackwhissh.workload.entity.enums.ShiftEnum;
import com.blackwhissh.workload.entity.enums.StatusEnum;
import com.blackwhissh.workload.exceptions.list.EmployeeAlreadyExistsException;
import com.blackwhissh.workload.exceptions.list.EmployeeNotFoundException;
import com.blackwhissh.workload.repository.EmployeeRepository;
import com.blackwhissh.workload.repository.HourRepository;
import com.blackwhissh.workload.repository.ScheduleRepository;
import com.blackwhissh.workload.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class EmployeeService {
    public static final Logger LOGGER = LoggerFactory.getLogger(EmployeeService.class);
    private final EmployeeRepository employeeRepository;
    private final ScheduleService scheduleService;
    private final UserRepository userRepository;
    private final ScheduleRepository scheduleRepository;
    private final HourRepository hourRepository;
    private final EntityManager entityManager;

    public EmployeeService(EmployeeRepository employeeRepository, ScheduleService scheduleService, UserRepository userRepository, ScheduleRepository scheduleRepository, HourRepository hourRepository, EntityManager entityManager) {
        this.employeeRepository = employeeRepository;
        this.scheduleService = scheduleService;
        this.userRepository = userRepository;
        this.scheduleRepository = scheduleRepository;
        this.hourRepository = hourRepository;
        this.entityManager = entityManager;
    }

    @Transactional
    public RegisterEmployeeResponse registerEmployee(RegisterEmployeeRequest request) {
        if (request.set() > 2 || request.set() <= 0) throw new IllegalArgumentException("Wrong set number provided");
        if (employeeRepository.existsByWorkId(request.workId())) {
            LOGGER.error("Employee with this work ID already exists");
            throw new EmployeeAlreadyExistsException();
        }
        if (employeeRepository.existsByUser_Email(request.email())) {
            LOGGER.error("Employee with this email already exists");
            throw new EmployeeAlreadyExistsException();
        }
        if (employeeRepository.existsByPid(request.pid())) {
            LOGGER.error("Employee with this pid already exists");
        }
        ShiftEnum shift = ShiftEnum.valueOf(request.shift().toUpperCase());

        User user = new User(request.email(), RoleEnum.EMPLOYEE, request.password(), request.dateOfHire());
        user.setEmail(request.email());


        Employee employee = new Employee(request.firstName(), request.lastName(), request.pid(), request.dob(),request.phoneNumber(),
                request.address(), request.emergencyContact(), request.position(), request.workId(), shift,
                request.set());

        employee.setUser(user);
        employeeRepository.save(employee);
        user.setEmployee(employee);
        userRepository.save(user);
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


    public EmployeeProfileResponse getUserProfile(String workId) {
        LOGGER.info("Started retrieving user profile");
        Employee employee = employeeRepository.findByWorkId(workId)
                .orElseThrow(EmployeeNotFoundException::new);
        return new EmployeeProfileResponse(
                employee.getFirstName(),
                employee.getLastName(),
                employee.getDob(),
                employee.getPhoneNumber(),
                employee.getAddress(),
                employee.getEmergencyContact(),
                employee.getPosition(),
                employee.getPid(),
                employee.getWorkId(),
                employee.getUser().getEmail(),
                employee.getUser().getActive()
        );
    }
    @Transactional
    public boolean deactivateUserProfile(String workId) {
        LOGGER.warn("Started deletion/deactivation of user profile");
        Employee employee = employeeRepository.findByWorkId(workId)
                .orElseThrow(EmployeeNotFoundException::new);

        List<Schedule> list = employee.getScheduleList().stream().peek(schedule -> {
            schedule.setEmployee(null);
            schedule.setHours(null);
            schedule.setTotalHours(0d);
            schedule.setWorkStatus(StatusEnum.REST);
        }).toList();


        User user = employee.getUser();
        user.setActive(false);
        employee.setUser(user);
        employeeRepository.save(employee);
        userRepository.save(user);
        scheduleRepository.saveAll(list);

        return true;
    }

    public EmployeeProfileResponse editCurrentEmployeeProfile(Optional<String> emergencyContact, Optional<LocalDate> dob,
                                                              Optional<String> email, Optional<String> address,
                                                              Optional<String> phone, Optional<String> lastName,
                                                              Optional<String> firstName, String workId) {
        LOGGER.info("Started editing current employee profile");
        Employee employee = employeeRepository.findByWorkId(workId)
                .orElseThrow(EmployeeNotFoundException::new);

        emergencyContact.ifPresent(employee::setEmergencyContact);
        dob.ifPresent(employee::setDob);
        email.ifPresent(e -> {
            if (employeeRepository.existsByUser_Email(e)) {
                throw new EmployeeAlreadyExistsException();
            }
        });
        address.ifPresent(employee::setAddress);
        phone.ifPresent(p -> {
            if (employeeRepository.existsByPhoneNumber(p)) {
                throw new EmployeeAlreadyExistsException();
            }
        });
        lastName.ifPresent(employee::setLastName);
        firstName.ifPresent(employee::setFirstName);
        employeeRepository.save(employee);
        return new EmployeeProfileResponse(
                employee.getFirstName(),
                employee.getLastName(),
                employee.getDob(),
                employee.getPhoneNumber(),
                employee.getAddress(),
                employee.getEmergencyContact(),
                employee.getPosition(),
                employee.getPid(),
                employee.getWorkId(),
                employee.getUser().getEmail(),
                employee.getUser().getActive()
        );
    }

    public ChangeEmployeePasswordResponse changeEmployeePassword(String workId, String password) {
        LOGGER.info("Started changing password for the employee with work ID: " + workId);
        Employee employee = employeeRepository.findByWorkId(workId)
                .orElseThrow(EmployeeNotFoundException::new);

        User user = employee.getUser();
        user.setPassword(PasswordConfig.passwordEncoder().encode(password));
        userRepository.save(user);
        employee.setUser(user);
        employeeRepository.save(employee);
        return new ChangeEmployeePasswordResponse(employee.getWorkId(), password);
    }
}
