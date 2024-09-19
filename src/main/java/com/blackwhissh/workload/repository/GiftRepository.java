package com.blackwhissh.workload.repository;

import com.blackwhissh.workload.entity.Employee;
import com.blackwhissh.workload.entity.Gift;
import com.blackwhissh.workload.entity.enums.RequestStatusEnum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GiftRepository extends JpaRepository<Gift, Integer> {
    List<Gift> findAllByPublisher(Employee publisher);
    Optional<Gift> findByPublisher_WorkIdAndGiftId(String publisherWorkId, Integer giftId);
    Optional<Gift> findByGiftIdAndStatus(Integer giftId, RequestStatusEnum status);
    List<Gift> findByReceiver_WorkIdAndStatusOrStatus(String workId, RequestStatusEnum statusA, RequestStatusEnum statusB);
}
