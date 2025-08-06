package com.example.Manage.Client.dto.request;

import java.time.LocalDate;
import java.util.List;

import com.example.Manage.Client.validator.DobConstraint;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class UserRequest {

    @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
    String username;

    @Size(min = 6, message = "Password must be at least 6 characters long")
    @Size(max = 20, message = "Password must not exceed 20 characters")
    String password;
    String lastName;
    String firstName;

    @DobConstraint(min = 18, message = "INVALID_DATE_OF_BIRTH")
    LocalDate dob;

    List<String> roles;

}
