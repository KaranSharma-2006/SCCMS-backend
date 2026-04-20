package com.smartcity.config;

import com.smartcity.entity.User;
import com.smartcity.enums.Role;
import com.smartcity.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AdminDataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminDataLoader(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        // Create default admin if not exists
        String adminEmail = "admin@smartcity.com";
        Optional<User> adminOptional = userRepository.findByEmail(adminEmail);

        if (adminOptional.isEmpty()) {
            User admin = new User();
            admin.setName("System Admin");
            admin.setEmail(adminEmail);
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole(Role.ADMIN);
            userRepository.save(admin);
            System.out.println("Default admin user created: " + adminEmail);
        } else {
            System.out.println("Admin user already exists.");
        }
    }
}
