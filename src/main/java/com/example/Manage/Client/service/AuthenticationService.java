package com.example.Manage.Client.service;

import java.time.Instant;
import java.util.Date;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.Manage.Client.dto.request.AuthenticationRequest;
import com.example.Manage.Client.dto.request.IntrospectRequest;
import com.example.Manage.Client.dto.response.AuthenticationResponse;
import com.example.Manage.Client.dto.response.IntrospectResponse;
import com.example.Manage.Client.jwt.JwtUtil;
import com.example.Manage.Client.repository.UserRepository;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.SignedJWT;

import jakarta.persistence.criteria.CriteriaBuilder.In;
import lombok.RequiredArgsConstructor;
import lombok.var;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationService {

    UserRepository userRepository;
    PasswordEncoder passwordEncoder;
    JwtUtil jwtUtil;

    public AuthenticationResponse authenticated(AuthenticationRequest request) {
        var user = userRepository.findByUsername(request.getUsername());

        if (user == null) {
            throw new RuntimeException("Ko thấy user");
        }

        boolean authenticated = passwordEncoder.matches(request.getPassword(), user.getPassword());
        if (!authenticated) {
            throw new RuntimeException("Sai mật khẩu");
        }
        return AuthenticationResponse.builder().token(JwtUtil.generateToken(user.getUsername()))
                .authenticated(true).build();
    }

    public IntrospectResponse introspect(IntrospectRequest request) throws Exception {
        var token = request.getToken();
        return IntrospectResponse.builder()
                .valid(jwtUtil.validateToken(token))
                .build();

    }
}
