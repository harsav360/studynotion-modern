package com.studynotion_modern.backend.dtos;

import lombok.Data;

@Data
@lombok.EqualsAndHashCode(callSuper = true)
public class OtpResponseDto extends ApiResponseDto {
    private String otp;

    public OtpResponseDto(boolean success, String message, String otp) {
        super(success, message);
        this.otp = otp;
    }
}
