package com.studynotion_modern.backend.dtos;

import lombok.Data;
import javax.validation.constraints.NotBlank;

@Data
public class ResetPasswordRequestDto {
    @NotBlank(message = "Token is required")
    private String token;

    @NotBlank(message = "New password is required")
    private String password;

    @NotBlank(message = "Confirm password is required")
    private String confirmPassword;
}
