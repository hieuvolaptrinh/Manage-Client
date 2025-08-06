package com.example.Manage.Client.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.Manage.Client.dto.request.AuthenticationRequest;
import com.example.Manage.Client.dto.request.IntrospectRequest;
import com.example.Manage.Client.dto.request.LogoutRequest;
import com.example.Manage.Client.dto.response.AuthenticationResponse;
import com.example.Manage.Client.dto.response.IntrospectResponse;
import com.example.Manage.Client.service.AuthenticationService;
import com.nimbusds.jose.JOSEException;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.text.ParseException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true) // private fields
public class AuthenticationController {

    AuthenticationService authenticationService;

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody AuthenticationRequest request) {
        return ResponseEntity.ok(authenticationService.authenticated(request));
    }

    @PostMapping("/introspect")
    public ResponseEntity<IntrospectResponse> introspect(@RequestBody IntrospectRequest request) throws Exception {
        return ResponseEntity.ok(authenticationService.introspect(request));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody LogoutRequest logoutRequest) throws JOSEException, ParseException {
        authenticationService.logout(logoutRequest);
        return ResponseEntity.ok().build();
    }
}
