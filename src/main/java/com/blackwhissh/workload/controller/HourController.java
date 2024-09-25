package com.blackwhissh.workload.controller;

import com.blackwhissh.workload.dto.HourDTO;
import com.blackwhissh.workload.dto.request.AddNewHourRequest;
import com.blackwhissh.workload.entity.Hour;
import com.blackwhissh.workload.service.HourService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/v1/hour")
@CrossOrigin(origins = "*", maxAge = 3600)
public class HourController {
    private final HourService hourService;

    public HourController(HourService hourService) {
        this.hourService = hourService;
    }

    @GetMapping()
    @Operation(summary = "Get schedule hours by schedule ID")
    public ResponseEntity<List<HourDTO>> getHoursByScheduleId(@RequestParam(name = "scheduleId") Integer scheduleId) {
        return ResponseEntity.ok(hourService.getHoursByScheduleId(scheduleId));
    }

    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @PatchMapping("/remove")
    @Operation(summary = "Remove hour by manager")
    public ResponseEntity<?> removeHourById(@RequestParam(name = "hourId") Integer hourId) {
        hourService.removeHourById(hourId);
        return ResponseEntity.ok("Hour removed successfully!");
    }

    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @PostMapping("/add")
    @Operation(summary = "Add hour by manager")
    public ResponseEntity<List<HourDTO>> addNewHour(@RequestBody AddNewHourRequest request) {
        return ResponseEntity.ok(hourService.addNewHour(request));
    }
}
