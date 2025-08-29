package com.studynotion_modern.backend.dtos;

import lombok.Data;

@Data
public class ChangePasswordRequestDto {

    private String oldPassword;
    private String newPassword;
}
