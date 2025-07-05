package com.studynotion_modern.backend.dtos;

import org.springframework.stereotype.Component;

import com.studynotion_modern.backend.entities.User;

import lombok.Data;

@Component
@Data
@lombok.EqualsAndHashCode(callSuper = true)
public class LoginResponseDto extends ApiResponseDto {
    private String token;
    private User user;

    public LoginResponseDto(boolean success, String token, User user, String message) {
        super(success, message);
        this.token = token;
        this.user = user;
    }
}
