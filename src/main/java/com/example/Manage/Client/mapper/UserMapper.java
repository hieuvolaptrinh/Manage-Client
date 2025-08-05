package com.example.Manage.Client.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.example.Manage.Client.dto.request.UserCreationRequest;
import com.example.Manage.Client.dto.response.UserResponse;
import com.example.Manage.Client.entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    // @Mapping(source = "...", target = "...") — chỉ rõ DTO.field → Entity.field.
    // @Mapping(source = "fullName", target = "name")
    User toUser(UserCreationRequest request);

    // @Mapping(target = "dob", ignore = true) ko map trường dob
    void updateUser(@MappingTarget User user, UserCreationRequest request);


    UserResponse toUserResponse(User user);
}
