package com.blackwhissh.workload.repository;

import com.blackwhissh.workload.entity.Rotation;
import com.blackwhissh.workload.entity.RotationItem;
import com.blackwhissh.workload.entity.enums.ShiftEnum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface RotationItemRepository extends JpaRepository<RotationItem, Integer> {
}
