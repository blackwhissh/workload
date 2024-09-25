package com.blackwhissh.workload.controller;

import com.blackwhissh.workload.dto.request.ScheduleByYearMonthAndWorkIdRequest;
import com.blackwhissh.workload.dto.request.ScheduleByYearMonthRequest;
import com.blackwhissh.workload.dto.request.ScheduleByYearMonthShiftRequest;
import com.blackwhissh.workload.dto.response.ScheduleByYearMonthResponse;
import com.blackwhissh.workload.service.ScheduleService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
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

    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @PostMapping("/get-by-workId")
    @Operation(summary = "Get employee monthly schedule by date and work id, by manager")
    public ResponseEntity<List<ScheduleByYearMonthResponse>> getScheduleByYearMonthAndWorkId(@RequestBody ScheduleByYearMonthAndWorkIdRequest request) {
        return ResponseEntity.ok(scheduleService.getScheduleByYearMonthAndWorkId(request.year(), request.month(), request.workId()));
    }

    @PostMapping("/current")
    @Operation(summary = "Get currently logged in employee's monthly schedule by date")
    public ResponseEntity<List<ScheduleByYearMonthResponse>> getCurrentEmployeeSchedule(@RequestBody ScheduleByYearMonthRequest request) {
        return ResponseEntity.ok(scheduleService.getScheduleByYearMonthAndWorkId(request.year(), request.month(), SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString()));
    }

    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @PostMapping("/all-by-month")
    @Operation(summary = "Get all schedules by year and month, by manager")
    public ResponseEntity<List<ScheduleByYearMonthResponse>> getScheduleByYearMonth(@RequestBody ScheduleByYearMonthRequest request) {
        return ResponseEntity.ok(scheduleService.getScheduleByYearMonth(request));
    }

    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @PostMapping("/all-by-shift")
    @Operation(summary = "Get all schedules by year, month, and shift, by manager")
    public ResponseEntity<List<ScheduleByYearMonthResponse>> getScheduleByYearMonthAndShift(@RequestBody ScheduleByYearMonthShiftRequest request) {
        return ResponseEntity.ok(scheduleService.getScheduleByYearMonthAndShift(request));
    }

}
