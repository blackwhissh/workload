package com.blackwhissh.workload.controller;

import com.blackwhissh.workload.dto.request.ScheduleByYearMonthRequest;
import com.blackwhissh.workload.dto.request.ScheduleByYearMonthAndWorkIdRequest;
import com.blackwhissh.workload.dto.response.ScheduleByYearMonthResponse;
import com.blackwhissh.workload.entity.Schedule;
import com.blackwhissh.workload.service.ScheduleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/v1/schedule")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ScheduleController {
    private final ScheduleService scheduleService;

    public ScheduleController(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }
    @PostMapping("/get-by-workId")
    private ResponseEntity<List<Schedule>> getScheduleByYearMonthAndWorkId(@RequestBody ScheduleByYearMonthAndWorkIdRequest request){
        return ResponseEntity.ok(scheduleService.getScheduleByYearMonthAndWorkId(request));
    }
    @PostMapping("/all-by-month")
    private ResponseEntity<List<ScheduleByYearMonthResponse>> getScheduleByYearMonth(@RequestBody ScheduleByYearMonthRequest request){
        return ResponseEntity.ok(scheduleService.getScheduleByYearMonth(request));
    }
}
