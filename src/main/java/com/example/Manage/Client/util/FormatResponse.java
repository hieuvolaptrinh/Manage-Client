package com.example.Manage.Client.util;

import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import com.example.Manage.Client.dto.request.ApiResponse;
import com.example.Manage.Client.dto.response.Notification;

import jakarta.servlet.http.HttpServletResponse;

@ControllerAdvice
public class FormatResponse implements ResponseBodyAdvice<Object> {

    @Override
    public Object beforeBodyWrite(Object body,
            MethodParameter returnType,
            MediaType selectedContentType,
            Class<? extends HttpMessageConverter<?>> selectedConverterType,
            ServerHttpRequest request, ServerHttpResponse response) {
        HttpServletResponse servletResponse = ((ServletServerHttpResponse) response).getServletResponse();
        int status = servletResponse.getStatus();

        if (body instanceof ApiResponse) {
            return body;
        }

        ApiResponse<Object> apiResponse = new ApiResponse<>();
        apiResponse.setCode(status);

        // Xử lý lỗi
        if (status >= 400) {
            if (body instanceof String) {
                apiResponse.setMessage(new Notification((String) body));
            } else if (body instanceof ApiResponse) {
                // Nếu body đã là ApiResponse, return luôn để tránh double wrapping
                return body;
            } else {
                apiResponse.setMessage("Error occurred");
                apiResponse.setData(body);
            }
            return apiResponse;
        }

        apiResponse.setData(body);
        apiResponse.setMessage("Success");

        return apiResponse;
    }

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

}
