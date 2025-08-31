package com.studynotion_modern.backend.service;

import com.studynotion_modern.backend.dtos.ResetPasswordRequestDto;
import com.studynotion_modern.backend.dtos.ResetPasswordTokenRequestDto;
import com.studynotion_modern.backend.dtos.LoginRequestDto;
import com.studynotion_modern.backend.dtos.SignupRequestDto;
import com.studynotion_modern.backend.dtos.SendOtpRequestDto;
import com.studynotion_modern.backend.dtos.ChangePasswordRequestDto;
import com.studynotion_modern.backend.entities.User;
import com.studynotion_modern.backend.entities.OTP;
import com.studynotion_modern.backend.entities.Profile;
import com.studynotion_modern.backend.repository.UserRepository;
import com.studynotion_modern.backend.repository.OTPRepository;
import com.studynotion_modern.backend.repository.ProfileRepository;
import com.studynotion_modern.backend.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final String SUCCESS = "success";
    private static final String MESSAGE = "message";
    private static final String USER_NOT_FOUND = "User not found";

    private final UserRepository userRepository;
    private final OTPRepository otpRepository;
    private final ProfileRepository profileRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final MailService mailService;
    private final Random random = new Random();

    public ResponseEntity<Map<String, Object>> sendOTP(SendOtpRequestDto request) {
        try {
            // Validate email format
            if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(SUCCESS, false, MESSAGE, "Email is required"));
            }

            // Check if user already exists
            Optional<User> existingUser = userRepository.findByEmail(request.getEmail());
            if (existingUser.isPresent()) {
                return ResponseEntity.badRequest().body(Map.of(SUCCESS, false, MESSAGE, "User already exists"));
            }

            // Generate 6-digit OTP
            String otp = String.format("%06d", random.nextInt(999999));

            // Save OTP to database
            OTP otpEntity = OTP.builder()
                    .email(request.getEmail())
                    .otp(otp)
                    .createdAt(LocalDateTime.now())
                    .build();
            otpRepository.save(otpEntity);

            // Send OTP via email
            mailService.sendOtp(request.getEmail(), otp);

            return ResponseEntity.ok(Map.of(
                    SUCCESS, true,
                    MESSAGE, "OTP sent successfully"
            ));

        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                    SUCCESS, false,
                    MESSAGE, "Failed to send OTP: " + e.getMessage()
            ));
        }
    }

    public ResponseEntity<Map<String, Object>> signup(SignupRequestDto request) {
        try {
            // Validate required fields
            if (request.getFirstName() == null || request.getFirstName().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(SUCCESS, false, MESSAGE, "First name is required"));
            }

            if (request.getPassword() == null || !request.getPassword().equals(request.getConfirmPassword())) {
                return ResponseEntity.badRequest().body(Map.of(SUCCESS, false, MESSAGE, "Passwords do not match"));
            }

            // Check if user already exists
            Optional<User> existingUser = userRepository.findByEmail(request.getEmail());
            if (existingUser.isPresent()) {
                return ResponseEntity.badRequest().body(Map.of(SUCCESS, false, MESSAGE, "User already exists"));
            }

            // Verify OTP
            Optional<OTP> otpEntity = otpRepository.findTopByEmailOrderByCreatedAtDesc(request.getEmail());
            if (otpEntity.isEmpty() || !otpEntity.get().getOtp().equals(request.getOtp())) {
                return ResponseEntity.badRequest().body(Map.of(SUCCESS, false, MESSAGE, "Invalid OTP"));
            }

            // Check OTP expiry (5 minutes)
            if (otpEntity.get().getCreatedAt().isBefore(LocalDateTime.now().minusMinutes(5))) {
                return ResponseEntity.badRequest().body(Map.of(SUCCESS, false, MESSAGE, "OTP has expired"));
            }

            // Parse contact number to Long, handle null case
            Long contactNumber = null;
            if (request.getContactNumber() != null && !request.getContactNumber().trim().isEmpty()) {
                try {
                    contactNumber = Long.parseLong(request.getContactNumber());
                } catch (NumberFormatException e) {
                    return ResponseEntity.badRequest().body(Map.of(SUCCESS, false, MESSAGE, "Invalid contact number format"));
                }
            }

            // Create profile
            Profile profile = Profile.builder()
                    .gender(null)
                    .dateOfBirth(null)
                    .about(null)
                    .contactNumber(contactNumber)
                    .build();
            Profile savedProfile = profileRepository.save(profile);

            // Create user
            User user = User.builder()
                    .firstName(request.getFirstName())
                    .lastName(request.getLastName())
                    .email(request.getEmail())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .accountType(request.getAccountType() != null ? request.getAccountType() : "Student")
                    .active(true)
                    .approved(true)
                    .additionalDetails(savedProfile)
                    .courses(new ArrayList<>())
                    .courseProgress(new ArrayList<>())
                    .createdAt(new Date())
                    .updatedAt(new Date())
                    .build();

            User savedUser = userRepository.save(user);

            // Delete used OTP
            otpRepository.delete(otpEntity.get());

            // Generate JWT token
            String token = jwtUtil.generateToken(savedUser);

            return ResponseEntity.ok(Map.of(
                    SUCCESS, true,
                    MESSAGE, "User registered successfully",
                    "token", token,
                    "user", savedUser
            ));

        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                    SUCCESS, false,
                    MESSAGE, "Registration failed: " + e.getMessage()
            ));
        }
    }

    public ResponseEntity<Map<String, Object>> login(LoginRequestDto request) {
        try {
            // Validate required fields
            if (request.getEmail() == null || request.getPassword() == null) {
                return ResponseEntity.badRequest().body(Map.of(SUCCESS, false, MESSAGE, "Email and password are required"));
            }

            // Find user by email
            Optional<User> userOpt = userRepository.findByEmail(request.getEmail());
            if (userOpt.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(SUCCESS, false, MESSAGE, USER_NOT_FOUND));
            }

            User user = userOpt.get();

            // Verify password
            if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                return ResponseEntity.badRequest().body(Map.of(SUCCESS, false, MESSAGE, "Invalid credentials"));
            }

            // Check if user is active
            if (!user.getActive()) {
                return ResponseEntity.badRequest().body(Map.of(SUCCESS, false, MESSAGE, "Account is deactivated"));
            }

            // Generate JWT token
            String token = jwtUtil.generateToken(user);

            return ResponseEntity.ok(Map.of(
                    SUCCESS, true,
                    MESSAGE, "Login successful",
                    "token", token,
                    "user", user
            ));

        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                    SUCCESS, false,
                    MESSAGE, "Login failed: " + e.getMessage()
            ));
        }
    }

    public ResponseEntity<Map<String, Object>> sendResetPasswordToken(ResetPasswordTokenRequestDto request) {
        try {
            // Validate email
            if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(SUCCESS, false, MESSAGE, "Email is required"));
            }

            // Find user by email
            Optional<User> userOpt = userRepository.findByEmail(request.getEmail());
            if (userOpt.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(SUCCESS, false, MESSAGE, USER_NOT_FOUND));
            }

            User user = userOpt.get();

            // Generate reset token (UUID)
            String resetToken = UUID.randomUUID().toString();

            // Set token and expiry (1 hour from now)
            user.setToken(resetToken);
            user.setResetPasswordExpires(new Date(System.currentTimeMillis() + 3600000)); // 1 hour
            userRepository.save(user);

            // Create reset password email
            String resetUrl = "http://localhost:3000/reset-password/" + resetToken;
            String emailBody = """
                <html>
                <body>
                    <h2>Password Reset Request</h2>
                    <p>Dear %s,</p>
                    <p>You have requested to reset your password. Click the link below to reset your password:</p>
                    <p><a href="%s">Reset Password</a></p>
                    <p>This link will expire in 1 hour.</p>
                    <p>If you didn't request this, please ignore this email.</p>
                </body>
                </html>
                """.formatted(user.getFirstName(), resetUrl);

            // Send reset password email
            mailService.sendMail(user.getEmail(), "Password Reset Request", emailBody);

            return ResponseEntity.ok(Map.of(
                    SUCCESS, true,
                    MESSAGE, "Reset password link sent to your email"
            ));

        } catch (MessagingException e) {
            return ResponseEntity.status(500).body(Map.of(
                    SUCCESS, false,
                    MESSAGE, "Failed to send email: " + e.getMessage()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                    SUCCESS, false,
                    MESSAGE, "Failed to send reset token: " + e.getMessage()
            ));
        }
    }

    public ResponseEntity<Map<String, Object>> resetPassword(ResetPasswordRequestDto request) {
        try {
            // Validate required fields
            if (request.getToken() == null || request.getPassword() == null || request.getConfirmPassword() == null) {
                return ResponseEntity.badRequest().body(Map.of(SUCCESS, false, MESSAGE, "All fields are required"));
            }

            // Check if passwords match
            if (!request.getPassword().equals(request.getConfirmPassword())) {
                return ResponseEntity.badRequest().body(Map.of(SUCCESS, false, MESSAGE, "Passwords do not match"));
            }

            // Find user by reset token
            Optional<User> userOpt = userRepository.findAll().stream()
                    .filter(user -> request.getToken().equals(user.getToken()))
                    .findFirst();

            if (userOpt.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(SUCCESS, false, MESSAGE, "Invalid reset token"));
            }

            User user = userOpt.get();

            // Check if token has expired
            if (user.getResetPasswordExpires() == null || user.getResetPasswordExpires().before(new Date())) {
                return ResponseEntity.badRequest().body(Map.of(SUCCESS, false, MESSAGE, "Reset token has expired"));
            }

            // Update password
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setToken(null);
            user.setResetPasswordExpires(null);
            user.setUpdatedAt(new Date());
            userRepository.save(user);

            return ResponseEntity.ok(Map.of(
                    SUCCESS, true,
                    MESSAGE, "Password reset successfully"
            ));

        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                    SUCCESS, false,
                    MESSAGE, "Password reset failed: " + e.getMessage()
            ));
        }
    }

    public ResponseEntity<Map<String, Object>> changePassword(ChangePasswordRequestDto request, String userEmail) {
        try {
            // Find user by email
            Optional<User> userOpt = userRepository.findByEmail(userEmail);
            if (userOpt.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(SUCCESS, false, MESSAGE, USER_NOT_FOUND));
            }

            User user = userOpt.get();

            // Verify old password
            if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
                return ResponseEntity.badRequest().body(Map.of(SUCCESS, false, MESSAGE, "Old password is incorrect"));
            }

            // Check if new passwords match
            if (!request.getNewPassword().equals(request.getConfirmNewPassword())) {
                return ResponseEntity.badRequest().body(Map.of(SUCCESS, false, MESSAGE, "New passwords do not match"));
            }

            // Update password
            user.setPassword(passwordEncoder.encode(request.getNewPassword()));
            user.setUpdatedAt(new Date());
            userRepository.save(user);

            // Send confirmation email
            String emailBody = """
                <html>
                <body>
                    <h2>Password Changed Successfully</h2>
                    <p>Dear %s,</p>
                    <p>Your password has been changed successfully.</p>
                    <p>If you didn't make this change, please contact support immediately.</p>
                </body>
                </html>
                """.formatted(user.getFirstName());

            mailService.sendMail(user.getEmail(), "Password Changed", emailBody);

            return ResponseEntity.ok(Map.of(
                    SUCCESS, true,
                    MESSAGE, "Password changed successfully"
            ));

        } catch (MessagingException e) {
            return ResponseEntity.status(500).body(Map.of(
                    SUCCESS, false,
                    MESSAGE, "Password changed but failed to send confirmation email"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                    SUCCESS, false,
                    MESSAGE, "Failed to change password: " + e.getMessage()
            ));
        }
    }
}
