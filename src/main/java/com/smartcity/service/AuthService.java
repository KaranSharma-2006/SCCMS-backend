package com.smartcity.service;

import com.smartcity.dto.request.LoginRequest;
import com.smartcity.dto.request.RegisterRequest;
import com.smartcity.dto.response.LoginResponse;
import com.smartcity.entity.User;
import com.smartcity.enums.Role;
import com.smartcity.exception.BadRequestException;
import com.smartcity.repository.UserRepository;
import com.smartcity.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public String register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            logger.warn("Registration failed: Email {} already exists", request.getEmail());
            throw new BadRequestException("Email already registered");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.USER);

        userRepository.save(user);
        logger.info("User registered successfully: {}", request.getEmail());
        return "User registered successfully";
    }

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> {
                    logger.warn("Login failed: User not found with email {}", request.getEmail());
                    return new BadCredentialsException("Invalid email or password");
                });

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            logger.warn("Login failed: Password mismatch for email {}", request.getEmail());
            throw new BadCredentialsException("Invalid email or password");
        }

        logger.info("User logged in successfully: {}", request.getEmail());
        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());

        return new LoginResponse(token, user.getRole().name(), user.getName(), user.getEmail());
    }
}
