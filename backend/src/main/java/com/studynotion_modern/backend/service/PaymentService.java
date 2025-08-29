package com.studynotion_modern.backend.service;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final RazorpayClient razorpayClient;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final CourseProgressRepository courseProgressRepository;
    private final MailService mailService;
    private final Environment env;

    public ResponseEntity<?> capturePayment(List<String> courseIds, String userId) {
        if (courseIds.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Please provide Course ID"));
        }

        double totalAmount = 0;
        for (String courseId : courseIds) {
            Course course = courseRepository.findById(courseId).orElse(null);
            if (course == null) {
                return ResponseEntity.status(404).body(Map.of("success", false, "message", "Course not found"));
            }

            if (course.getStudentsEnrolled().contains(userId)) {
                return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Already Enrolled"));
            }

            totalAmount += course.getPrice();
        }

        JSONObject options = new JSONObject();
        options.put("amount", (int) (totalAmount * 100));
        options.put("currency", "INR");
        options.put("receipt", UUID.randomUUID().toString());

        try {
            Order order = razorpayClient.orders.create(options);
            return ResponseEntity.ok(Map.of("success", true, "data", order.toJson()));
        } catch (RazorpayException e) {
            return ResponseEntity.status(500).body(Map.of("success", false, "message", "Could not initiate order"));
        }
    }

    public ResponseEntity<?> verifyPayment(String orderId, String paymentId, String signature, List<String> courseIds,
            String userId) {
        if (orderId == null || paymentId == null || signature == null || courseIds == null || userId == null) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Payment Failed"));
        }

        String secret = env.getProperty("razorpay.secret");
        String payload = orderId + "|" + paymentId;
        String expectedSignature = null;

        try {
            Mac sha256HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
            sha256HMAC.init(secretKey);
            expectedSignature = Hex.encodeHexString(sha256HMAC.doFinal(payload.getBytes()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("success", false, "message", e.getMessage()));
        }

        if (expectedSignature.equals(signature)) {
            return enrollStudents(courseIds, userId);
        } else {
            return ResponseEntity.status(400).body(Map.of("success", false, "message", "Invalid signature"));
        }
    }

    public ResponseEntity<?> sendPaymentSuccessEmail(String userId, String orderId, String paymentId, int amount) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return ResponseEntity.status(400).body(Map.of("success", false, "message", "User not found"));
        }

        try {
            String body = EmailTemplate.paymentSuccessEmail(user.getFirstName() + " " + user.getLastName(),
                    amount / 100.0, orderId, paymentId);
            mailService.sendMail(user.getEmail(), "Payment Received", body);
            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("success", false, "message", "Could not send email"));
        }
    }

    private ResponseEntity<?> enrollStudents(List<String> courseIds, String userId) {
        for (String courseId : courseIds) {
            Course course = courseRepository.findById(courseId).orElse(null);
            if (course == null) {
                return ResponseEntity.status(400).body(Map.of("success", false, "message", "Course not found"));
            }

            course.getStudentsEnrolled().add(userId);
            courseRepository.save(course);

            CourseProgress progress = CourseProgress.builder()
                    .courseID(courseId)
                    .userId(userId)
                    .completedVideos(new ArrayList<>())
                    .build();
            courseProgressRepository.save(progress);

            User user = userRepository.findById(userId).orElse(null);
            if (user != null) {
                user.getCourses().add(course);
                user.getCourseProgress().add(progress);
                userRepository.save(user);

                String body = EmailTemplate.courseEnrollmentEmail(course.getCourseName(),
                        user.getFirstName() + " " + user.getLastName());
                mailService.sendMail(user.getEmail(), "Successfully Enrolled into " + course.getCourseName(), body);
            }
        }

        return ResponseEntity.ok(Map.of("success", true, "message", "Enrolled successfully"));
    }
}
