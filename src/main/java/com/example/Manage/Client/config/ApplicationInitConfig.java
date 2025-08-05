package com.example.Manage.Client.config;

import java.util.HashSet;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.Manage.Client.entity.User;
import com.example.Manage.Client.repository.UserRepository;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.experimental.FieldNameConstants;
import lombok.extern.slf4j.Slf4j;

@Configuration
@FieldNameConstants
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ApplicationInitConfig {
    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);

    @Bean
    ApplicationRunner applicationRunner(UserRepository userRepository) {
        return args -> {
            if (userRepository.findByUsername("admin") == null) {
                var roles = new HashSet<String>();
                roles.add("ADMIN");
                roles.add("USER");

                User adminUser = new User().builder()
                        .username("admin")
                        .password(passwordEncoder.encode("123456"))
                        // .roles(roles)
                        .build();
                userRepository.save(adminUser);
                log.info("Admin user created successfully.");
            } else {
                log.warn("Admin user already exists, skipping creation.");
            }
        };
    }
}