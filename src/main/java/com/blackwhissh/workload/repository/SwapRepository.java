package com.blackwhissh.workload.repository;

import com.blackwhissh.workload.entity.Swap;
import com.blackwhissh.workload.entity.enums.RequestStatusEnum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SwapRepository extends JpaRepository<Swap, Integer> {
    List<Swap> findAllByStatusIsLike(RequestStatusEnum requestStatusEnum);
    Optional<Swap> findBySwapIdAndStatus(Integer swapId, RequestStatusEnum statusEnum);
    List<Swap> findByReceiver_WorkIdAndStatus(String workId, RequestStatusEnum statusEnum);
    List<Swap> findByReceiver_WorkIdAndStatusOrStatus(String workId, RequestStatusEnum statusA, RequestStatusEnum statusB);

    Optional<Swap> findByPublisher_WorkIdAndSwapId(String workId, Integer swapId);
}
