package com.events.config;

import com.events.model.User;
import com.events.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initData(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (!userRepository.existsByEmail("admin@events.com")) {
                User admin = User.builder()
                        .name("Admin")
                        .email("admin@events.com")
                        .password(passwordEncoder.encode("admin123"))
                        .role(User.Role.ADMIN)
                        .build();
                userRepository.save(admin);
                System.out.println("✅ Default Admin created → admin@events.com / admin123");
            }

            if (!userRepository.existsByEmail("user@events.com")) {
                User user = User.builder()
                        .name("Test User")
                        .email("user@events.com")
                        .password(passwordEncoder.encode("user123"))
                        .role(User.Role.USER)
                        .build();
                userRepository.save(user);
                System.out.println("✅ Default User created → user@events.com / user123");
            }
        };
    }
}
