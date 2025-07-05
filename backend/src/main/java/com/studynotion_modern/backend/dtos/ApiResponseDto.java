package com.studynotion_modern.backend.dtos;

import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@Data
public class ApiResponseDto {
    private boolean success;
    private String message;
    private Object data;

    public ApiResponseDto(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public ApiResponseDto(boolean success, Object data) {
        this.success = success;
        this.data = data;
    }
}
