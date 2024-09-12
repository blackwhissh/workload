package com.blackwhissh.workload.controller;

import com.blackwhissh.workload.dto.request.RegisterEmployeeRequest;
import com.blackwhissh.workload.dto.response.RegisterEmployeeResponse;
import com.blackwhissh.workload.service.EmployeeService;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<RegisterEmployeeResponse> registerEmployee(@NonNull @RequestBody RegisterEmployeeRequest request){
        return ResponseEntity.ok(employeeService.registerEmployee(request));
    }
}
