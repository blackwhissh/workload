package com.blackwhissh.workload.controller;

import com.blackwhissh.workload.dto.request.ChangeEmployeePasswordRequest;
import com.blackwhissh.workload.dto.request.EditEmployeeProfileRequest;
import com.blackwhissh.workload.dto.request.RegisterEmployeeRequest;
import com.blackwhissh.workload.dto.response.EmployeeProfileResponse;
import com.blackwhissh.workload.dto.response.RegisterEmployeeResponse;
import com.blackwhissh.workload.service.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.function.Supplier;

@RestController
@RequestMapping(value = "/v1/employee")
@CrossOrigin(origins = "*", maxAge = 3600)
public class EmployeeController {
    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @PostMapping("/register")
    @Operation(summary = "Register employee user by manager, used by manager")
    public ResponseEntity<RegisterEmployeeResponse> registerEmployee(@NonNull @RequestBody RegisterEmployeeRequest request){
        return ResponseEntity.ok(employeeService.registerEmployee(request));
    }

    @PreAuthorize("hasRole('ROLE_EMPLOYEE')")
    @PutMapping("/edit")
    @Operation(summary = "Edit currently logged in user's profile")
    public ResponseEntity<EmployeeProfileResponse> editCurrentEmployeeProfile(@NonNull @RequestBody EditEmployeeProfileRequest request) {
        return ResponseEntity.ok(employeeService.editCurrentEmployeeProfile(
                request.emergencyContact(),
                request.dob(),
                request.email(),
                request.address(),
                request.phoneNumber(),
                request.lastName(),
                request.firstName(),
                SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString()
        ));
    }

    @PreAuthorize("hasAnyRole('ROLE_EMPLOYEE','ROLE_MANAGER')")
    @GetMapping("/profile")
    @Operation(summary = "Get current user's profile")
    public ResponseEntity<EmployeeProfileResponse> getCurrentUserProfile() {
        String workId = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        return ResponseEntity.ok(employeeService.getUserProfile(workId));
    }
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @GetMapping("/profile/{workId}")
    @Operation(summary = "Get user profile by work id, used by manager")
    public ResponseEntity<EmployeeProfileResponse> getUserProfileByWorkId(@PathVariable(name = "workId") String workId) {
        return ResponseEntity.ok(employeeService.getUserProfile(workId));
    }

    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @PostMapping("/deactivate/{workId}")
    @Operation(summary = "Deactivate user")
    public ResponseEntity<?> deleteEmployee(@PathVariable(name = "workId") String workId) {
        if (employeeService.deactivateUserProfile(workId)) {
            return ResponseEntity.ok("Employee deactivated successfully!");
        }else {
            return ResponseEntity.badRequest().body("Error occurred during deleting employee");
        }
    }

    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @PostMapping("/change-password")
    @Operation(summary = "Change user password with workId, used by manager")
    public ResponseEntity<?> changeEmployeePassword(@RequestBody ChangeEmployeePasswordRequest request) {
        return ResponseEntity.ok(employeeService.changeEmployeePassword(request.workId(), request.password()));
    }
}