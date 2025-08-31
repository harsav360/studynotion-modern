package com.studynotion_modern.backend.dtos;

import lombok.Data;

@Data
public class PaymentEmailRequestDto {
    private String orderId;
    private String paymentId;
    private double amount;
}

