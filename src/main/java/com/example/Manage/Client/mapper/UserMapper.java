package com.example.Manage.Client.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.example.Manage.Client.dto.request.UserRequest;
import com.example.Manage.Client.dto.response.UserResponse;
import com.example.Manage.Client.entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    // @Mapping(source = "...", target = "...") — chỉ rõ DTO.field → Entity.field.
    // @Mapping(source = "fullName", target = "name")
    @Mapping(target = "roles", ignore = true)
    User toUser(UserRequest request);

    @Mapping(target = "roles", ignore = true)
    void updateUser(@MappingTarget User user, UserRequest request);

    @Mapping(target = "roles", ignore = true)

    UserResponse toUserResponse(User user);
}
