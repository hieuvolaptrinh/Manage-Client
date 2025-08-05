package com.example.Manage.Client.service;

import com.example.Manage.Client.dto.request.PermissionRequest;
import com.example.Manage.Client.dto.response.PermissionResponse;
import com.example.Manage.Client.entity.Permission;

import com.example.Manage.Client.mapper.PermissionMapper;
import com.example.Manage.Client.repository.PermissionRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PermissionService {

    PermissionRepository permissionRepository;
    PermissionMapper permissionMapper;

    // ✅ Create
    public PermissionResponse create(PermissionRequest request) {
        Permission permission = permissionMapper.toPermission(request);
        permissionRepository.save(permission);
        return permissionMapper.toPermissionResponse(permission);
    }

    // ✅ Read - Get All
    public List<PermissionResponse> getAll() {
        return permissionRepository.findAll().stream()
                .map(permissionMapper::toPermissionResponse)
                .collect(Collectors.toList());
    }

    // ✅ Read - Get by ID
    public PermissionResponse getByName(String id) {
        Permission permission = permissionRepository.findById(id).get();

        return permissionMapper.toPermissionResponse(permission);
    }

    // ✅ Update
    public PermissionResponse update(String id, PermissionRequest request) {
        Permission permission = permissionRepository.findById(id).get();

        permission.setName(request.getName());
        permission.setDescription(request.getDescription());

        permissionRepository.save(permission);
        return permissionMapper.toPermissionResponse(permission);
    }

    // ✅ Delete
    public void deleteById(String permission) {
        permissionRepository.deleteById(permission);
    }
}
