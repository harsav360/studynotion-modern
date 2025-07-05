package com.studynotion_modern.backend.dtos;

import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Data;

@Component
@AllArgsConstructor
@Data
public class ChangePasswordRequestDto {

    private String oldPassword;
    private String newPassword;
}
