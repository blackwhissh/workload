package com.blackwhissh.workload.repository;

import com.blackwhissh.workload.entity.Hour;
import com.blackwhissh.workload.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface HourRepository extends JpaRepository<Hour, Integer> {
    List<Hour> findBySchedule(Schedule schedule);
    Optional<Hour> findByStartAndSchedule(LocalTime start, Schedule schedule);
    List<Hour> findAllByStartAndEndAndSchedule(LocalTime start, LocalTime end, Schedule schedule);
}
