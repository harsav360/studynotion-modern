package com.studynotion_modern.backend.dtos;

import lombok.Data;

@Data
@lombok.EqualsAndHashCode(callSuper = true)
public class OtpResponseDto extends ApiResponseDto {

    public OtpResponseDto(boolean success, String message) {
        super(success, message);
    }
}
