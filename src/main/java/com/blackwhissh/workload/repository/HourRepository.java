package com.blackwhissh.workload.repository;

import com.blackwhissh.workload.entity.Hour;
import com.blackwhissh.workload.entity.RotationItem;
import com.blackwhissh.workload.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface HourRepository extends JpaRepository<Hour, Integer> {
    List<Hour> findBySchedule(Schedule schedule);
    @Query("select h from Hour h where h.start >= :start and h.end <= :end and h.schedule.scheduleId = :scheduleId")
    List<Hour> findBetweenStartEndAndSchedule_Id(@Param("start") LocalTime start,
                                              @Param("end") LocalTime end,
                                              @Param("scheduleId") int scheduleId);
    List<Hour> findAllByStartAndEndAndSchedule_Employee_WorkId(LocalTime start, LocalTime end, String workId);
    boolean existsByStartAndEndAndSchedule_ScheduleId(LocalTime start, LocalTime end, int scheduleId);
}
