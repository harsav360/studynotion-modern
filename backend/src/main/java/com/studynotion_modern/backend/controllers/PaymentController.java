package com.studynotion_modern.backend.controllers;

import com.studynotion_modern.backend.dtos.PaymentEmailRequestDto;
import com.studynotion_modern.backend.dtos.PaymentVerifyRequestDto;
import com.studynotion_modern.backend.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;
import java.util.Map;

// PaymentController.java
@RestController
@RequestMapping("/api/v1/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/capture")
    public ResponseEntity<?> capturePayment(@RequestBody Map<String, List<String>> body, Principal principal) {
        return paymentService.capturePayment(body.get("courses"), principal.getName());
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyPayment(@RequestBody PaymentVerifyRequestDto request, Principal principal) {
        return paymentService.verifyPayment(
                request.getRazorpayOrderId(),
                request.getRazorpayPaymentId(),
                request.getRazorpaySignature(),
                request.getCourses(),
                principal.getName());
    }

    @PostMapping("/success-email")
    public ResponseEntity<?> sendSuccessMail(@RequestBody PaymentEmailRequestDto request, Principal principal) {
        return paymentService.sendPaymentSuccessEmail(
                principal.getName(),
                request.getOrderId(),
                request.getPaymentId(),
                request.getAmount());
    }
}
