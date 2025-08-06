package com.example.Manage.Client.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.Manage.Client.dto.request.UserRequest;
import com.example.Manage.Client.dto.response.RoleResponse;
import com.example.Manage.Client.dto.response.UserResponse;
import com.example.Manage.Client.repository.RoleRepository;
import com.example.Manage.Client.repository.UserRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import com.example.Manage.Client.entity.User;
import com.example.Manage.Client.enums.RoleEnum;
import com.example.Manage.Client.exception.AppException;
import com.example.Manage.Client.exception.ErrorCode;
import com.example.Manage.Client.mapper.RoleMapper;
import com.example.Manage.Client.mapper.UserMapper;

@Service
@RequiredArgsConstructor // Tự động tạo constructor với tất cả các trường là final
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true) // private final fields
public class UserService {
    UserRepository userRepository;
    UserMapper userMapper;
    PasswordEncoder passwordEncoder;
    RoleRepository roleRepository;
    RoleMapper roleMapper;

    public User createUser(UserRequest request) {

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new AppException(ErrorCode.USERNAME_INVALID);
        }
        User user = userMapper.toUser(request);
        user.setPassword(passwordEncoder.encode(user.getPassword())); // Mã hóa mật khẩu

        HashSet<String> roles = new HashSet<>();

        roles.add(RoleEnum.USER.name());

        // user.setRoles(roles); // Thiết lập vai trò cho người dùng
        return userRepository.save(user);
    }

    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id).orElse(null);

        if (user == null) {
            return null;
        }
        UserResponse userResponse = userMapper.toUserResponse(user);
        if (user.getRoles() != null && !user.getRoles().isEmpty()) {
            Set<RoleResponse> roleResponses = user.getRoles().stream()
                    .map(roleMapper::toRoleResponse) // Chuyển đổi Role -> RoleResponse (kèm permissions)
                    .collect(Collectors.toSet());

            userResponse.setRoles(roleResponses);
        }

        return userResponse;
    }

    public List<UserResponse> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream().map(user -> {
            UserResponse userResponse = userMapper.toUserResponse(user);
            if (user.getRoles() != null && !user.getRoles().isEmpty()) {
                Set<RoleResponse> roleResponses = user.getRoles().stream()
                        .map(role -> {
                            return roleMapper.toRoleResponse(role);
                        })
                        .collect(Collectors.toSet());
                userResponse.setRoles(roleResponses);
            }
            return userResponse;
        }).collect(Collectors.toList());
    }

    public UserResponse updateUser(Long id, UserRequest request) {
        User user = userRepository.findById(id).orElse(null);
        if (user != null) {
            userMapper.updateUser(user, request);
            user.setPassword(passwordEncoder.encode(user.getPassword())); // Mã hóa mật khẩu
            var roles = roleRepository.findAllById(request.getRoles());
            user.setRoles(new HashSet<>(roles));

            return userMapper.toUserResponse(userRepository.save(user));
        }

        return null;
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public UserResponse getMyInfo() {
        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();

        User user = userRepository.findByUsername(name);

        if (user == null) {
            return null;
        }

        // Mapper chỉ map các field cơ bản, bỏ qua roles
        UserResponse userResponse = userMapper.toUserResponse(user);

        // Xử lý thủ công roles và permissions
        if (user.getRoles() != null && !user.getRoles().isEmpty()) {
            Set<RoleResponse> roleResponses = user.getRoles().stream()
                    .map(roleMapper::toRoleResponse) // Chuyển đổi Role -> RoleResponse (kèm permissions)
                    .collect(Collectors.toSet());

            userResponse.setRoles(roleResponses);
        }

        return userResponse;
    }
}
