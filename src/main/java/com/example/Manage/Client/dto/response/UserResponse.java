package com.example.Manage.Client.dto.response;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class UserResponse {

    private Long id;
    private String username;
    private String password;
    private String lastName;
    private String firstName;
    private LocalDate dob;

}
