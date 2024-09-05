package com.blackwhissh.workload.repository;

import com.blackwhissh.workload.entity.Hour;
import com.blackwhissh.workload.entity.Swap;
import com.blackwhissh.workload.entity.enums.RequestStatusEnum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SwapRequestRepository extends JpaRepository<Swap, Integer> {
    List<Swap> findAllByStatusIsLike(RequestStatusEnum requestStatusEnum);
    Boolean existsByHour_Id(Integer id);
}
