package com.studynotion_modern.backend.dtos;

import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class LoginRequestDto {
    @NotBlank(message = "Email is require")
    private String email;

    @NotBlank(message = "Password is require")
    private String password;
}
