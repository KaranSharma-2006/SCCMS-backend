package com.smartcity.controller;

import com.smartcity.dto.response.ApiResponse;
import com.smartcity.dto.response.UserResponse;
import com.smartcity.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse> getProfile() {
        UserResponse response = userService.getCurrentUserProfile();
        return ResponseEntity.ok(ApiResponse.success("Profile retrieved successfully", response));
    }
}
