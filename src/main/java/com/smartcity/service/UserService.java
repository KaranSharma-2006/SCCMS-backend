package com.smartcity.service;

import com.smartcity.dto.response.UserResponse;
import com.smartcity.entity.User;
import com.smartcity.exception.ResourceNotFoundException;
import com.smartcity.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserResponse getCurrentUserProfile() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        logger.info("Fetching profile for user: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole().name(),
                user.getCreatedAt()
        );
    }
}
