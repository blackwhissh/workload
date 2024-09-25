package com.blackwhissh.workload.controller;

import com.blackwhissh.workload.dto.request.SignInRequest;
import com.blackwhissh.workload.dto.request.TokenRefreshRequest;
import com.blackwhissh.workload.dto.response.JwtResponse;
import com.blackwhissh.workload.dto.response.MessageResponse;
import com.blackwhissh.workload.dto.response.TokenRefreshResponse;
import com.blackwhissh.workload.entity.Employee;
import com.blackwhissh.workload.entity.RefreshToken;
import com.blackwhissh.workload.entity.User;
import com.blackwhissh.workload.exceptions.list.EmployeeNotFoundException;
import com.blackwhissh.workload.exceptions.list.RefreshTokenNotFoundException;
import com.blackwhissh.workload.exceptions.list.UserIsInactiveException;
import com.blackwhissh.workload.exceptions.list.UserNotFoundException;
import com.blackwhissh.workload.repository.EmployeeRepository;
import com.blackwhissh.workload.repository.UserRepository;
import com.blackwhissh.workload.security.ApplicationUser;
import com.blackwhissh.workload.security.jwt.JwtUtils;
import com.blackwhissh.workload.security.jwt.RefreshTokenService;
import io.swagger.v3.oas.annotations.OpenAPI31;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;
    private final RefreshTokenService refreshTokenService;
    private final EmployeeRepository employeeRepository;

    @Autowired
    public AuthController(AuthenticationManager authenticationManager, JwtUtils jwtUtils, UserRepository userRepository, RefreshTokenService refreshTokenService, EmployeeRepository employeeRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.userRepository = userRepository;
        this.refreshTokenService = refreshTokenService;
        this.employeeRepository = employeeRepository;
    }

    @PostMapping("/login")
    @Operation(summary = "Login user by work id and password")
    public ResponseEntity<?> authenticateUser(@RequestBody SignInRequest signInRequest) {
        User user = userRepository.findByEmployee_WorkId(signInRequest.username())
                .orElseThrow(UserNotFoundException::new);
        refreshTokenService.deleteByUserId(user.getUserId());
        if (!user.getActive()) {
            throw new UserIsInactiveException();
        }
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        signInRequest.username(),
                        signInRequest.password()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        ApplicationUser applicationUser = (ApplicationUser) authentication.getPrincipal();
        Employee employee = employeeRepository.findByUser(user).orElseThrow(EmployeeNotFoundException::new);

        String jwt = jwtUtils.generateJwtToken(applicationUser.getUsername(), user.getRole());

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getEmployee().getWorkId());
        return ResponseEntity.ok(new JwtResponse(
                jwt,
                refreshToken.getToken(),
                applicationUser.getId(),
                employee.getUser().getEmail(),
                applicationUser.getUsername(),
                applicationUser.getAuthorities().stream().findFirst().get().toString()
        ));
    }

    @PostMapping("/refreshtoken")
    @Operation(summary = "Refresh access token")
    public ResponseEntity<?> refreshToken(@RequestBody TokenRefreshRequest request) {
        String requestRefreshToken = request.getRefreshToken();

        return refreshTokenService.findByToken(requestRefreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    Employee employee = employeeRepository.findByUser(user).orElseThrow(EmployeeNotFoundException::new);
                    String token = jwtUtils.generateJwtToken(user.getEmployee().getWorkId(), user.getRole());
                    return ResponseEntity.ok(new TokenRefreshResponse(token, requestRefreshToken));
                })
                .orElseThrow(RefreshTokenNotFoundException::new);
    }

    @PostMapping("/logout")
    @Operation(summary = "Log out user")
    public ResponseEntity<?> logoutUser() {
        User user = userRepository.findByEmployee_WorkId(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString())
                .orElseThrow(UserNotFoundException::new);
        Integer userId = user.getUserId();
        refreshTokenService.deleteByUserId(userId);
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok(new MessageResponse("Log out successful!"));
    }
}
