package com.blackwhissh.workload.controller;

import com.blackwhissh.workload.dto.request.ScheduleByYearMonthRequest;
import com.blackwhissh.workload.dto.request.ScheduleByYearMonthAndWorkIdRequest;
import com.blackwhissh.workload.dto.response.ScheduleByYearMonthResponse;
import com.blackwhissh.workload.security.jwt.JwtUtils;
import com.blackwhissh.workload.service.ScheduleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/v1/schedule")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ScheduleController {
    private final ScheduleService scheduleService;
    private final JwtUtils jwtUtils;

    public ScheduleController(ScheduleService scheduleService, JwtUtils jwtUtils) {
        this.scheduleService = scheduleService;
        this.jwtUtils = jwtUtils;
    }
    @PostMapping("/get-by-workId")
    private ResponseEntity<List<ScheduleByYearMonthResponse>> getScheduleByYearMonthAndWorkId(@RequestBody ScheduleByYearMonthAndWorkIdRequest request){
        return ResponseEntity.ok(scheduleService.getScheduleByYearMonthAndWorkId(request.year(), request.month(), request.workId()));
    }
    @PostMapping("/current")
    private ResponseEntity<List<ScheduleByYearMonthResponse>> getCurrentEmployeeSchedule(@RequestHeader("Authorization") String jwt,
                                                                                         @RequestBody ScheduleByYearMonthRequest request) {
        jwt = jwt.substring(7);
        return ResponseEntity.ok(scheduleService.getScheduleByYearMonthAndWorkId(request.year(), request.month(), jwtUtils.getWorkIdFromJwtToken(jwt)));
    }
    @PostMapping("/all-by-month")
    private ResponseEntity<List<ScheduleByYearMonthResponse>> getScheduleByYearMonth(@RequestBody ScheduleByYearMonthRequest request){
        return ResponseEntity.ok(scheduleService.getScheduleByYearMonth(request));
    }
}
