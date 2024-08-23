package com.blackwhissh.workload.repository;

import com.blackwhissh.workload.entity.Hour;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HourRepository extends JpaRepository<Hour, Integer> {
}
