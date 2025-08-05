package com.example.Manage.Client.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.Manage.Client.dto.request.UserCreationRequest;
import com.example.Manage.Client.dto.response.UserResponse;
import com.example.Manage.Client.entity.User;
import com.example.Manage.Client.exception.AppException;
import com.example.Manage.Client.exception.ErrorCode;
import com.example.Manage.Client.mapper.UserMapper;
import com.example.Manage.Client.repository.UserRepository;
import com.example.Manage.Client.service.UserService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("")
    public User createUser(@RequestBody @Valid UserCreationRequest request) {
        return userService.createUser(request);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        UserResponse user = userService.getUserById(id);
        if (user != null) {
            return ResponseEntity.ok(user);
        }
        throw new AppException(ErrorCode.USER_NOT_FOUND);
    }

    @GetMapping("")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long id,
            @RequestBody @Valid UserCreationRequest request) {
        UserResponse updatedUser = userService.updateUser(id, request);
        if (updatedUser != null) {
            return ResponseEntity.ok(updatedUser);
        }
        throw new AppException(ErrorCode.USER_NOT_FOUND);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
