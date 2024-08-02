package com.blackwhissh.workload.repository;

import com.blackwhissh.workload.entity.WorkDay;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkDayRepository extends JpaRepository<WorkDay, Integer> {
}
