package com.example.Manage.Client.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL) // nghĩa là nếu data là null thì sẽ không được serialize
public class ApiResponse<T> {
    private int code;
    private String message;
    private T data;
}
