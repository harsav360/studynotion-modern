package com.studynotion_modern.backend.repository;

import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.studynotion_modern.backend.entities.OTP;
import org.springframework.stereotype.Repository;

@Repository
public interface OTPRepository extends MongoRepository<OTP, ObjectId> {

    Optional<OTP> findByOtp(String otp);

    Optional<OTP> findTopByEmailOrderByCreatedAtDesc(String email);
}
