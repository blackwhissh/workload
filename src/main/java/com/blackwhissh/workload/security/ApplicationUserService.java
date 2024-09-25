package com.blackwhissh.workload.security;

import com.blackwhissh.workload.entity.User;
import com.blackwhissh.workload.exceptions.list.EntityNotFoundException;
import com.blackwhissh.workload.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class ApplicationUserService implements UserDetailsService {
    private final UserRepository userRepository;

    public ApplicationUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public ApplicationUser loadUserByUsername(String workId) throws UsernameNotFoundException {
        User user;
        if (userRepository.existsByEmployee_WorkIdAndIsActive(workId, true)) {
            user = userRepository.findByEmployee_WorkId(workId).orElseThrow(EntityNotFoundException::new);
            if (user != null) {
                return new ApplicationUser(
                        user.getUserId(), user.getEmployee().getWorkId(), user.getPassword(),
                        Collections.singleton(new SimpleGrantedAuthority("ROLE_" + user.getRole()))
                );
            }
        }
        throw new UsernameNotFoundException(String.format("Work ID %s not found", workId));
    }
}
