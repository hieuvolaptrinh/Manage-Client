package com.example.Manage.Client.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.Manage.Client.dto.request.RoleRequest;
import com.example.Manage.Client.dto.response.RoleResponse;
import com.example.Manage.Client.entity.Role;

@Mapper(componentModel = "spring")
public interface RoleMapper {

    @Mapping(target = "permissions", ignore = true)
    Role toRole(RoleRequest request);

    RoleResponse toRoleResponse(Role role);

    default java.util.Set<String> map(java.util.Set<com.example.Manage.Client.entity.Permission> permissions) {
        if (permissions == null) {
            return null;
        }
        java.util.Set<String> result = new java.util.HashSet<>();
        for (com.example.Manage.Client.entity.Permission permission : permissions) {
            result.add(permission.getName());
        }
        return result;
    }
}
