package com.blackwhissh.workload.repository;

import com.blackwhissh.workload.entity.Employee;
import com.blackwhissh.workload.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Integer> {
    List<Schedule> findAllByEmployeeAndDateBetween(Employee employee, LocalDate start, LocalDate end);
    List<Schedule> findAllByDateBetween(LocalDate start, LocalDate end);
}
