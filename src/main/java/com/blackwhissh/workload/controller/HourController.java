package com.blackwhissh.workload.controller;

import com.blackwhissh.workload.dto.HourDTO;
import com.blackwhissh.workload.dto.request.AddNewHourRequest;
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
    public ResponseEntity<List<HourDTO>> getHoursByScheduleId(@RequestParam(name = "scheduleId") Integer scheduleId) {
        return ResponseEntity.ok(hourService.getHoursByScheduleId(scheduleId));
    }

    @PatchMapping("/remove")
    public ResponseEntity<?> removeHourById(@RequestParam(name = "hourId") Integer hourId) {
        hourService.removeHourById(hourId);
        return ResponseEntity.ok("Hour removed successfully!");
    }

    @PostMapping("/add")
    public ResponseEntity<List<HourDTO>> addNewHour(@RequestBody AddNewHourRequest request) {
        return ResponseEntity.ok(hourService.addNewHour(request));
    }
}
