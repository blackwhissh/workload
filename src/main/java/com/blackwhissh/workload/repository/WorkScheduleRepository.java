package com.blackwhissh.workload.repository;

import com.blackwhissh.workload.entity.WorkSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkScheduleRepository extends JpaRepository<WorkSchedule, Integer> {
}
