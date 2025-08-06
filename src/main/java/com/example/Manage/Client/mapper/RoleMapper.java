package com.example.Manage.Client.mapper;



import com.example.Manage.Client.entity.Permission;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.Manage.Client.dto.request.RoleRequest;
import com.example.Manage.Client.dto.response.RoleResponse;
import com.example.Manage.Client.entity.Role;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface RoleMapper {


    //    @Mapping(target = "permissions", ignore = true)
    Role toRole(RoleRequest request);

    default Set<Permission> map(Set<String> permissionNames) {
        if (permissionNames == null) return null;
        return permissionNames.stream()
                .map(name -> Permission.builder().name(name).build())
                .collect(Collectors.toSet());
    }

    RoleResponse toRoleResponse(Role role);


}
