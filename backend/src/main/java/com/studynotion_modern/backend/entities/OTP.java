package com.studynotion_modern.backend.entities;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "otp")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OTP {
    @Id
    private String id;

    private String email;
    private String otp;

    @CreatedDate
    private LocalDateTime createdAt = LocalDateTime.now();
}
