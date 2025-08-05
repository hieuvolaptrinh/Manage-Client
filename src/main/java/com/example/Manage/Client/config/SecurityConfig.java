package com.example.Manage.Client.config;

import java.lang.reflect.Method;

import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties.Jwt;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

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
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();

        // ở đây có thể cấu hình jwt ví dụ như SCOPE_ thành ROLE_ , ngăn cách giữa các
        // role, rồi claimName nữa
        jwtGrantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");
        // role,...
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);
        return jwtAuthenticationConverter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // Tắt CSRF cho API
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/api/v1/auth/**").permitAll()
                        .requestMatchers(HttpMethod.GET, EndpointConfig.USER_GET)
                        // .hasAnyAuthority("SCOPE_USER", "SCOPE_ADMIN")
                        // .anyRequest().hasAnyAuthority("SCOPE_ADMIN")
                        .hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")
                        .anyRequest().hasAnyAuthority("ROLE_ADMIN")
                // .requestMatchers("/api/auth/**").permitAll()
                )
                .httpBasic(httpBasic -> httpBasic.disable()); // Tắt HTTP Basic Auth
        // Với cấu hình Spring Security OAuth2 Resource Server như này, bạn KHÔNG CẦN
        // viết doFilter() hay tạo custom filter nữa!

        http.oauth2ResourceServer(oauth2 -> oauth2.jwt(jwtConfigure -> jwtConfigure.decoder(jwtDecoder())

                .jwtAuthenticationConverter(jwtAuthenticationConverter())));

        return http.build();
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        SecretKeySpec secretKeySpec = new SecretKeySpec(SECRET_KEY.getBytes(), "HS512");
        return NimbusJwtDecoder.withSecretKey(secretKeySpec)
                .macAlgorithm(MacAlgorithm.HS512)
                .build();
    };
}
