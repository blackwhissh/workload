package com.blackwhissh.workload.controller;

import com.blackwhissh.workload.dto.RotationDTO;
import com.blackwhissh.workload.dto.request.CreateRotationRequest;
import com.blackwhissh.workload.dto.request.GetEmployeesForCurrentShiftRotationRequest;
import com.blackwhissh.workload.dto.request.GetRotationByShiftAndDateRequest;
import com.blackwhissh.workload.entity.Rotation;
import com.blackwhissh.workload.service.RotationItemService;
import com.blackwhissh.workload.service.RotationService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static com.blackwhissh.workload.utils.MapToDTOUtils.mapRotationToDTO;

@RestController
@RequestMapping("/v1/rotation")
@CrossOrigin(origins = "*", maxAge = 3600)
public class RotationController {
    private final RotationService rotationService;
    private final RotationItemService rotationItemService;

    public RotationController(RotationService rotationService, RotationItemService rotationItemService) {
        this.rotationService = rotationService;
        this.rotationItemService = rotationItemService;
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @Operation(summary = "Create new rotation by manager")
    public ResponseEntity<RotationDTO> createRotation(@RequestBody CreateRotationRequest request) {
        Rotation rotation = rotationService.createRotation(request.shift(), request.rotationDate());
        return ResponseEntity.ok(mapRotationToDTO(rotation));
    }

    @GetMapping("/get")
    @Operation(summary = "Get rotation by shift and date")
    public ResponseEntity<RotationDTO> getRotationByShiftAndDate(@RequestBody GetRotationByShiftAndDateRequest request){
        Rotation rotation = rotationService.getRotationByShiftAndDate(request.shift(), request.date());
        return ResponseEntity.ok(mapRotationToDTO(rotation));
    }

    @GetMapping("/get-employees")
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @Operation(summary = "Get employee with hours eligible for current rotation, by manager")
    public ResponseEntity<?> getEmployeesWithHoursForCurrentShiftRotation(@RequestBody GetEmployeesForCurrentShiftRotationRequest request) {
        return ResponseEntity.ok(rotationItemService.getEmployeesWithHoursForCurrentShiftRotation(request.shift(),request.date()));
    }
}
