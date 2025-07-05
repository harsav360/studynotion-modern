package com.studynotion_modern.backend.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.studynotion_modern.backend.entities.OTP;

public interface OTPRepository extends MongoRepository<OTP, String> {

    Optional<OTP> findByOtp(String otp);

    Optional<OTP> findTopByEmailOrderByCreatedAtDesc(String email);
}
