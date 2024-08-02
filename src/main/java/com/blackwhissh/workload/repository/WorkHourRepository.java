package com.blackwhissh.workload.repository;

import com.blackwhissh.workload.entity.WorkHour;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkHourRepository extends JpaRepository<WorkHour, Integer> {
}
