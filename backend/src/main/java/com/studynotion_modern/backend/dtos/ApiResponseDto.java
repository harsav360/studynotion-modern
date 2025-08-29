package com.studynotion_modern.backend.dtos;

import lombok.Data;

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
