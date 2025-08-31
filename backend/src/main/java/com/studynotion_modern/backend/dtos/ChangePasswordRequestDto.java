package com.studynotion_modern.backend.dtos;

import lombok.Data;
import javax.validation.constraints.NotBlank;

@Data
public class ChangePasswordRequestDto {
    @NotBlank(message = "Old password is required")
    private String oldPassword;

    @NotBlank(message = "New password is required")
    private String newPassword;

    @NotBlank(message = "Confirm new password is required")
    private String confirmNewPassword;
}
