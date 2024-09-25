package com.blackwhissh.workload.controller;

import com.blackwhissh.workload.dto.request.AddNewStudioRequest;
import com.blackwhissh.workload.entity.enums.RotationAction;
import com.blackwhissh.workload.service.StudioService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/v1/studio")
@CrossOrigin(origins = "*", maxAge = 3600)
public class StudioController {
    private final StudioService studioService;

    public StudioController(StudioService studioService) {
        this.studioService = studioService;
    }

    @GetMapping("/all")
    @Operation(summary = "Get all studios")
    public ResponseEntity<?> getAllStudios() {
        return ResponseEntity.ok(studioService.getAllStudios());
    }

    @GetMapping("/actions")
    @Operation(summary = "Get studio tables/actions by studio ID")
    public ResponseEntity<?> getStudioActionsByStudioId(@RequestParam("studioId") int studioId){
        return ResponseEntity.ok(studioService.getStudioActionsByStudioId(studioId));
    }

    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @PostMapping("/add")
    @Operation(summary = "Add new studio, by manager")
    public ResponseEntity<?> addNewStudio(@RequestBody AddNewStudioRequest request) {
        return ResponseEntity.ok(studioService.addNewStudio(request.actions(), request.bonus()));
    }
}
