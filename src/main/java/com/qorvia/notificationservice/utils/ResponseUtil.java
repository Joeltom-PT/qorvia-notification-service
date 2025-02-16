package com.qorvia.notificationservice.utils;

import com.qorvia.notificationservice.dto.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ResponseUtil {
    public static <T> ResponseEntity<ApiResponse<T>> buildResponse(HttpStatus status, String message, T data) {
        ApiResponse<T> apiResponse = new ApiResponse<>(status.value(), message, data);
        return new ResponseEntity<>(apiResponse, status);
    }
}
