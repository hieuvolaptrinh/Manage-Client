package com.example.Manage.Client.config;

import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${jwt.signerKey}")
    private String SECRET_KEY;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // Tắt CSRF cho API
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/api/v1/auth/**").permitAll() // Cho phép truy cập không cần xác thực
                        .anyRequest().permitAll()
                // .requestMatchers("/api/auth/**").permitAll()
                )
                .httpBasic(httpBasic -> httpBasic.disable()); // Tắt HTTP Basic Auth
        http.oauth2ResourceServer(oauth2 -> oauth2.jwt(
                jwtConfigure -> jwtConfigure.decoder(jwtDecoder())));

        return http.build();
    }

    @Bean
    JwtDecoder jwtDecoder() {
        SecretKeySpec secretKeySpec = new SecretKeySpec(SECRET_KEY.getBytes(), "HS512");
        return NimbusJwtDecoder.withSecretKey(secretKeySpec)
                .build();
    };
}
