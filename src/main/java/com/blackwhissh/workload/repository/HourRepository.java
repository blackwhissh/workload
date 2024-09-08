package com.blackwhissh.workload.repository;

import com.blackwhissh.workload.entity.Hour;
import com.blackwhissh.workload.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HourRepository extends JpaRepository<Hour, Integer> {
    List<Hour> findBySchedule(Schedule schedule);
}
