package com.studynotion_modern.backend.service;

import com.studynotion_modern.backend.dtos.ResetPasswordRequestDto;
import com.studynotion_modern.backend.dtos.ResetPasswordTokenRequestDto;
import org.springframework.http.ResponseEntity;

public class AuthService {


    public ResponseEntity<?> sendResetPasswordToken(ResetPasswordTokenRequestDto request) {
        return  null;
    }

    public ResponseEntity<?> resetPassword(ResetPasswordRequestDto request) {
        return null;
    }
}
