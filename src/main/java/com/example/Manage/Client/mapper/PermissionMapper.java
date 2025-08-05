package com.example.Manage.Client.mapper;

import com.example.Manage.Client.dto.request.PermissionRequest;
import com.example.Manage.Client.dto.request.UserCreationRequest;
import com.example.Manage.Client.dto.response.PermissionResponse;
import com.example.Manage.Client.dto.response.UserResponse;
import com.example.Manage.Client.entity.Permission;
import com.example.Manage.Client.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface PermissionMapper {


    Permission toUser(PermissionRequest request);



    PermissionResponse toPermissionResponse(Permission permission);
}
