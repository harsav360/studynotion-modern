package com.studynotion_modern.backend.service;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.studynotion_modern.backend.entities.Course;
import com.studynotion_modern.backend.entities.CourseProgress;
import com.studynotion_modern.backend.entities.User;
import com.studynotion_modern.backend.repository.CourseProgressRepository;
import com.studynotion_modern.backend.repository.CourseRepository;
import com.studynotion_modern.backend.repository.UserRepository;
import com.studynotion_modern.backend.utils.EmailTemplates;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.binary.Hex;
import org.json.JSONObject;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bson.types.ObjectId;

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
        double totalAmount = 0.0;
        User user = userRepository.findById(new ObjectId(userId)).orElse(null);
        if (user == null) {
            return ResponseEntity.status(404).body(Map.of("success", false, "message", "User not found"));
        }
        for (String courseId : courseIds) {
            Course course = courseRepository.findById(new ObjectId(courseId)).orElse(null);
            if (course == null) {
                return ResponseEntity.status(404).body(Map.of("success", false, "message", "Course not found"));
            }
            if (course.getStudentsEnrolled().contains(user)) {
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
        if (secret == null) {
            return ResponseEntity.status(500).body(Map.of("success", false, "message", "Razorpay secret not configured"));
        }
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

    public ResponseEntity<?> sendPaymentSuccessEmail(String userId, String orderId, String paymentId, double amount) {
        User user = userRepository.findById(new ObjectId(userId)).orElse(null);
        if (user == null) {
            return ResponseEntity.status(400).body(Map.of("success", false, "message", "User not found"));
        }

        try {
            String body = EmailTemplates.paymentSuccessEmail(user.getFirstName() + " " + user.getLastName(),
                    amount / 100.0, orderId, paymentId);
            mailService.sendMail(user.getEmail(), "Payment Received", body);
            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("success", false, "message", "Could not send email"));
        }
    }

    private ResponseEntity<?> enrollStudents(List<String> courseIds, String userId) {
        User user = userRepository.findById(new ObjectId(userId)).orElse(null);
        if (user == null) {
            return ResponseEntity.status(400).body(Map.of("success", false, "message", "User not found"));
        }
        for (String courseId : courseIds) {
            Course course = courseRepository.findById(new ObjectId(courseId)).orElse(null);
            if (course == null) {
                return ResponseEntity.status(400).body(Map.of("success", false, "message", "Course not found"));
            }
            if (!course.getStudentsEnrolled().contains(user)) {
                course.getStudentsEnrolled().add(user);
            }
            courseRepository.save(course);
            CourseProgress progress = CourseProgress.builder()
                    .courseID(course)
                    .userId(user)
                    .completedVideos(new ArrayList<>())
                    .build();
            courseProgressRepository.save(progress);
            if (!user.getCourses().contains(course)) {
                user.getCourses().add(course);
            }
            if (!user.getCourseProgress().contains(progress)) {
                user.getCourseProgress().add(progress);
            }
            userRepository.save(user);
            try {
                String body = EmailTemplates.courseEnrollmentEmail(course.getCourseName(),
                        user.getFirstName() + " " + user.getLastName());
                mailService.sendMail(user.getEmail(), "Successfully Enrolled into " + course.getCourseName(), body);
            } catch (Exception e) {
                // Optionally log or handle email failure
            }
        }
        return ResponseEntity.ok(Map.of("success", true, "message", "Enrolled successfully"));
    }
}
