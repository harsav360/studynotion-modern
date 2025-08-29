package com.studynotion_modern.backend.dtos;

import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class SendOtpRequestDto {

    @NotBlank(message = "Email is require")
    private String email;
}
