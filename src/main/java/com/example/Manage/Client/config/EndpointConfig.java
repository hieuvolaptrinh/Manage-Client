package com.example.Manage.Client.config;

public class EndpointConfig {
        public static final String[] ADMIN = {
                        "/api/v1/users/**",
                        "/api/v1/auth/**"
        };

        public static final String[] USER_GET = {
                        "/api/v1/users/**"
        };
}
