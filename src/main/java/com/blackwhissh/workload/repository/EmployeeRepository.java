package com.blackwhissh.workload.repository;

import com.blackwhissh.workload.entity.Employee;
import com.blackwhissh.workload.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Integer> {
    Optional<Employee> findByWorkId (String workId);
    Optional<Employee> findByUser(User user);
    boolean existsByWorkId (String workId);
}
