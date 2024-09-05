package com.blackwhissh.workload.repository;

import com.blackwhissh.workload.entity.Employee;
import com.blackwhissh.workload.entity.Schedule;
import com.blackwhissh.workload.entity.enums.StatusEnum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ScheduleRepository extends JpaRepository<Schedule, Integer> {
    List<Schedule> findAllByDateBetweenAndEmployee_WorkId(LocalDate start, LocalDate end, String workId);
    List<Schedule> findAllByDateBetween(LocalDate start, LocalDate end);
    Optional<Schedule> findFirstByDateAfterAndWorkStatus(LocalDate current, StatusEnum statusEnum);
    Optional<Schedule> findByDateAndEmployee_WorkId(LocalDate hourDay, String workId);
}
