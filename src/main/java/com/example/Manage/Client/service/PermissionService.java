package com.example.Manage.Client.service;

import com.example.Manage.Client.dto.request.PermissionRequest;
import com.example.Manage.Client.dto.response.PermissionResponse;
import com.example.Manage.Client.mapper.UserMapper;
import com.example.Manage.Client.repository.PermissionRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor // Tự động tạo constructor với tất cả các trường là final
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true) // private final fields
public class PermissionService {
    PermissionRepository permissionRepository;

    PermissionResponse create (PermissionRequest request) {

    }

}
