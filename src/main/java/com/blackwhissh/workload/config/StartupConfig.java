package com.blackwhissh.workload.config;

import com.blackwhissh.workload.entity.User;
import com.blackwhissh.workload.entity.enums.RoleEnum;
import com.blackwhissh.workload.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class StartupConfig {
    private final UserRepository userRepository;

    public StartupConfig(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostConstruct
    private void addAdmin() {
        User user = new User();
        user.setActive(true);
        user.setFirstName("admin");
        user.setLastName("admin");
        user.setMultiplier(100d);
        user.setPassword("admin");
        user.setRole(RoleEnum.ADMIN);
        user.setRegistrationDate(LocalDate.now());
        user.setTempPass("admin");
        user.setUsername("admin");
        userRepository.save(user);
    }
}
