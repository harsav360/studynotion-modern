package com.studynotion_modern.backend.controllers;

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
    public ResponseEntity<?> verifyPayment(@RequestBody PaymentVerifyRequest request, Principal principal) {
        return paymentService.verifyPayment(
                request.getRazorpay_order_id(),
                request.getRazorpay_payment_id(),
                request.getRazorpay_signature(),
                request.getCourses(),
                principal.getName());
    }

    @PostMapping("/success-email")
    public ResponseEntity<?> sendSuccessMail(@RequestBody PaymentEmailRequest request, Principal principal) {
        return paymentService.sendPaymentSuccessEmail(
                principal.getName(),
                request.getOrderId(),
                request.getPaymentId(),
                request.getAmount());
    }
}
