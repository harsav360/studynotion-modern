package com.studynotion_modern.backend.dtos;


import lombok.Data;

import java.util.List;

@Data
public class PaymentVerifyRequestDto {
    private String razorpayOrderId;
    private String razorpayPaymentId;
    private String razorpaySignature;
    private List<String> courses;
}
