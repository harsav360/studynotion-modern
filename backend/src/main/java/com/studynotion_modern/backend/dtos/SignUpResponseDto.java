package com.studynotion_modern.backend.dtos;

import com.studynotion_modern.backend.entities.User;

import lombok.Data;

@Data
@lombok.EqualsAndHashCode(callSuper = true)
public class SignUpResponseDto extends ApiResponseDto {
    private User user;

    public SignUpResponseDto(boolean success, User user, String message) {
        super(success, message);
        this.user = user;
    }
}
