package com.example.Manage.Client.exception;

public enum ErrorCode {
    USER_EXISTED(1001, "User already exists"),
    USER_NOT_FOUND(1002, "User not found"),
    INVALID_REQUEST(1003, "Invalid request"),
    USERNAME_INVALID(1004, "Username is invalid");

    private final int code;
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
