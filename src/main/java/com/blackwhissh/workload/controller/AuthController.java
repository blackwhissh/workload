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
import com.blackwhissh.workload.exceptions.list.UserNotFoundException;
import com.blackwhissh.workload.repository.EmployeeRepository;
import com.blackwhissh.workload.repository.UserRepository;
import com.blackwhissh.workload.security.ApplicationUser;
import com.blackwhissh.workload.security.jwt.JwtUtils;
import com.blackwhissh.workload.security.jwt.RefreshTokenService;
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
    public ResponseEntity<?> authenticateUser(@RequestBody SignInRequest signInRequest) {
        User user = userRepository.findByEmail(signInRequest.username())
                .orElseThrow(UserNotFoundException::new);
        refreshTokenService.deleteByUserId(user.getUserId());
        if (!user.getActive()) {
            user.setActive(true);
            userRepository.save(user);
        }
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        signInRequest.username(),
                        signInRequest.password()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        ApplicationUser applicationUser = (ApplicationUser) authentication.getPrincipal();
        Employee employee = employeeRepository.findByUser(user).orElseThrow(EmployeeNotFoundException::new);

        String jwt = jwtUtils.generateJwtToken(applicationUser.getUsername(), user.getRole(), employee.getWorkId());

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getEmail());
        return ResponseEntity.ok(new JwtResponse(
                jwt,
                refreshToken.getToken(),
                applicationUser.getId(),
                applicationUser.getUsername(),
                employee.getWorkId(),
                applicationUser.getAuthorities().stream().findFirst().get().toString()
        ));
    }

    @PostMapping("/refreshtoken")
    public ResponseEntity<?> refreshToken(@RequestBody TokenRefreshRequest request) {
        String requestRefreshToken = request.getRefreshToken();

        return refreshTokenService.findByToken(requestRefreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    Employee employee = employeeRepository.findByUser(user).orElseThrow(EmployeeNotFoundException::new);
                    String token = jwtUtils.generateJwtToken(user.getEmail(), user.getRole(), employee.getWorkId());
                    return ResponseEntity.ok(new TokenRefreshResponse(token, requestRefreshToken));
                })
                .orElseThrow(RefreshTokenNotFoundException::new);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser() {
        User user = userRepository.findByEmail(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString())
                .orElseThrow(UserNotFoundException::new);
        Integer userId = user.getUserId();
        refreshTokenService.deleteByUserId(userId);
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok(new MessageResponse("Log out successful!"));
    }
}
