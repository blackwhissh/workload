package com.blackwhissh.workload.controller;

import com.blackwhissh.workload.entity.Hour;
import com.blackwhissh.workload.service.HourService;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<List<Hour>> getHoursByScheduleId(@RequestParam(name = "scheduleId") Integer scheduleId) {
        return ResponseEntity.ok(hourService.getHoursByScheduleId(scheduleId));
    }
}
