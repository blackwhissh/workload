package com.blackwhissh.workload.repository;

import com.blackwhissh.workload.entity.Employee;
import com.blackwhissh.workload.entity.Gift;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GiftRepository extends JpaRepository<Gift, Integer> {
    List<Gift> findAllByPublisher(Employee publisher);
}
