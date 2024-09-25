package com.blackwhissh.workload.repository;

import com.blackwhissh.workload.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    boolean existsByEmailAndIsActive(String email, boolean active);
    boolean existsByEmployee_WorkIdAndIsActive(String workId, boolean active);

    Optional<User> findByEmail(String email);
    Optional<User> findByEmployee_WorkId(String workId);
}
