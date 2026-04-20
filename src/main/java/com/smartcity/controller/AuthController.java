package com.smartcity.controller;

import com.smartcity.dto.request.LoginRequest;
import com.smartcity.dto.request.RegisterRequest;
import com.smartcity.dto.response.ApiResponse;
import com.smartcity.dto.response.LoginResponse;
import com.smartcity.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> register(@Valid @RequestBody RegisterRequest request) {
        String message = authService.register(request);
        return new ResponseEntity<>(ApiResponse.success(message), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse loginResponse = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("Login successful", loginResponse));
    }
}
