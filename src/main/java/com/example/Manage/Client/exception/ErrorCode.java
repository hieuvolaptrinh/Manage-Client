package com.example.Manage.Client.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized exception", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_KEY(1001, "Uncategorized error", HttpStatus.BAD_REQUEST),
    UNAUTHENTICATED(401, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1006, "You are not authorized to access this resource", HttpStatus.FORBIDDEN),
    USER_EXISTED(1001, "User already exists", HttpStatus.BAD_REQUEST),
    USER_NOT_FOUND(1002, "User not found", HttpStatus.NOT_FOUND),
    INVALID_REQUEST(1003, "Invalid request", HttpStatus.BAD_REQUEST),
    USERNAME_INVALID(1004, "Username is invalid",
            HttpStatus.BAD_REQUEST),
    NOT_FOUND(1005, "Resource not found", HttpStatus.NOT_FOUND),
    INVALID_DATE_OF_BIRTH(1006, "Your age must be at least {min}", HttpStatus.BAD_REQUEST);

    private int code;
    private String message;
    private HttpStatus httpStatus;

    ErrorCode(int code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }

}
