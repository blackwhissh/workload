package com.blackwhissh.workload.controller;

import com.blackwhissh.workload.dto.RotationItemDTO;
import com.blackwhissh.workload.dto.request.AddRotationItemRequest;
import com.blackwhissh.workload.dto.request.EditRotationItemRequest;
import com.blackwhissh.workload.dto.request.GetEmployeesForCurrentShiftRotationRequest;
import com.blackwhissh.workload.entity.Rotation;
import com.blackwhissh.workload.entity.RotationItem;
import com.blackwhissh.workload.exceptions.list.RotationNotFoundException;
import com.blackwhissh.workload.repository.RotationRepository;
import com.blackwhissh.workload.service.RotationItemService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static com.blackwhissh.workload.utils.MapToDTOUtils.mapRotationItemToDTO;

@RestController
@RequestMapping("/v1/rotation/item")
@CrossOrigin(origins = "*", maxAge = 3600)
public class RotationItemController {
    private final RotationItemService rotationItemService;
    private final RotationRepository rotationRepository;

    public RotationItemController(RotationItemService rotationItemService, RotationRepository rotationRepository) {
        this.rotationItemService = rotationItemService;
        this.rotationRepository = rotationRepository;
    }

    @PostMapping("/add")
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @Operation(summary = "Add employee to rotation")
    public ResponseEntity<?> addRotationItem(@RequestBody AddRotationItemRequest request) {
        Rotation rotation = rotationRepository.findById(request.rotationId())
                .orElseThrow(RotationNotFoundException::new);
        RotationItem rotationItem = rotationItemService.addRotationItem(
                rotation,
                request.employeeWorkId(),
                request.start(),
                request.end(),
                request.action(),
                request.studioId(),
                request.uniform());
        return ResponseEntity.ok(mapRotationItemToDTO(rotationItem));
    }

    @PostMapping("/edit")
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @Operation(summary = "Edit employee in rotation")
    public ResponseEntity<RotationItemDTO> editRotationItem(@RequestBody EditRotationItemRequest request) {
        return ResponseEntity.ok(mapRotationItemToDTO(rotationItemService.editRotationItem(
                request.rotationItemId(),
                request.start(),
                request.end(),
                request.action(),
                request.uniform()
        )));
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @Operation(summary = "Remove employee from rotation")
    public ResponseEntity<?> deleteRotationItem(@PathVariable(name = "id") int rotationItemId) {
        rotationItemService.deleteRotationItem(rotationItemId);
        return ResponseEntity.ok("Rotation item (employee) deleted successfully");
    }


}
