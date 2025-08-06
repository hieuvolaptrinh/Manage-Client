package com.example.Manage.Client.mapper;

import com.example.Manage.Client.dto.request.PermissionRequest;
import com.example.Manage.Client.dto.response.PermissionResponse;

import com.example.Manage.Client.entity.Permission;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PermissionMapper {

    Permission toPermission(PermissionRequest request);

    PermissionResponse toPermissionResponse(Permission permission);
}
