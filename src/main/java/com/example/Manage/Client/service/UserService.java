package com.example.Manage.Client.service;

import java.util.HashSet;
import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.Manage.Client.dto.request.UserCreationRequest;
import com.example.Manage.Client.dto.response.UserResponse;
import com.example.Manage.Client.repository.UserRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import com.example.Manage.Client.entity.User;
import com.example.Manage.Client.enums.Role;
import com.example.Manage.Client.exception.AppException;
import com.example.Manage.Client.exception.ErrorCode;
import com.example.Manage.Client.mapper.UserMapper;

@Service
@RequiredArgsConstructor // Tự động tạo constructor với tất cả các trường là final
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true) // private final fields
public class UserService {
    UserRepository userRepository;
    UserMapper userMapper;
    PasswordEncoder passwordEncoder; // Mã hóa mật khẩu với độ mạnh 10

    public User createUser(UserCreationRequest request) {

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        }
        User user = userMapper.toUser(request);
        user.setPassword(passwordEncoder.encode(user.getPassword())); // Mã hóa mật khẩu

        HashSet<String> roles = new HashSet<>();

        roles.add(Role.USER.name()); // Gán vai trò USER từ enum

        user.setRoles(roles); // Thiết lập vai trò cho người dùng
        return userRepository.save(user);
    }

    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id).orElse(null);
        return userMapper.toUserResponse(user);
    }

    public List<UserResponse> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(userMapper::toUserResponse)
                .toList();
    }

    public UserResponse updateUser(Long id, UserCreationRequest request) {
        User user = userRepository.findById(id).orElse(null);
        if (user != null) {
            userMapper.updateUser(user, request);

            return userMapper.toUserResponse(userRepository.save(user));
        }
        return null;
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}
