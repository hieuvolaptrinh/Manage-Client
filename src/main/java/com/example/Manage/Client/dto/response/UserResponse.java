package com.example.Manage.Client.dto.response;

import java.time.LocalDate;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@Builder
@NoArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE) // private fields
public class UserResponse {

    Long id;
    String username;

    String lastName;
    String firstName;
    LocalDate dob;
    Set<RoleResponse> roles;

    // You can add additional fields or methods as needed
}
