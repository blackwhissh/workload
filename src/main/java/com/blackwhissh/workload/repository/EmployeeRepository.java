package com.blackwhissh.workload.repository;

import com.blackwhissh.workload.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Integer> {
    Optional<Employee> findByWorkId (String workId);
    boolean existsByWorkId (String workId);
}
