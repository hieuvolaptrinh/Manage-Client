package com.example.Manage.Client.service;

import com.example.Manage.Client.dto.request.RoleRequest;
import com.example.Manage.Client.dto.response.RoleResponse;
import com.example.Manage.Client.entity.Permission;
import com.example.Manage.Client.entity.Role;
import com.example.Manage.Client.exception.AppException;
import com.example.Manage.Client.exception.ErrorCode;
import com.example.Manage.Client.mapper.RoleMapper;
import com.example.Manage.Client.repository.PermissionRepository;
import com.example.Manage.Client.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static lombok.AccessLevel.PRIVATE;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class RoleService {

    RoleRepository roleRepository;
    PermissionRepository permissionRepository;
    RoleMapper roleMapper;

    // ✅ Create
    public RoleResponse create(RoleRequest request) {
        Role role = roleMapper.toRole(request);

        Set<Permission> permissions = permissionRepository.findAllById(request.getPermissions())
                .stream().collect(Collectors.toSet());
        role.setPermissions(permissions);

        roleRepository.save(role);
        return roleMapper.toRoleResponse(role);
    }

    // ✅ Get All
    public List<RoleResponse> getAll() {
        return roleRepository.findAll().stream()
                .map(roleMapper::toRoleResponse)
                .collect(Collectors.toList());
    }

    // ✅ Get by name (ID)
    public RoleResponse getByName(String name) {
        Role role = roleRepository.findById(name).get();

        return roleMapper.toRoleResponse(role);
    }

    // ✅ Update
    public RoleResponse update(String name, RoleRequest request) {
        Role role = roleRepository.findById(name).get();

        role.setDescription(request.getDescription());

        Set<Permission> permissions = permissionRepository.findAllById(request.getPermissions())
                .stream().collect(Collectors.toSet());
        role.setPermissions(permissions);

        roleRepository.save(role);
        return roleMapper.toRoleResponse(role);
    }

    // ✅ Delete
    public void delete(String name) {
        if (!roleRepository.existsById(name)) {
            throw new AppException(ErrorCode.NOT_FOUND);
        }
        roleRepository.deleteById(name);
    }
}
