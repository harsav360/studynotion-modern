package com.studynotion_modern.backend.controllers;

import java.time.LocalDateTime;
import java.util.Random;

import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import javax.validation.Valid;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.studynotion_modern.backend.dtos.ApiResponseDto;
import com.studynotion_modern.backend.dtos.ChangePasswordRequestDto;
import com.studynotion_modern.backend.dtos.LoginRequestDto;
import com.studynotion_modern.backend.dtos.LoginResponseDto;
import com.studynotion_modern.backend.dtos.OtpResponseDto;
import com.studynotion_modern.backend.dtos.SendOtpRequestDto;
import com.studynotion_modern.backend.dtos.SignUpResponseDto;
import com.studynotion_modern.backend.dtos.SignupRequestDto;
import com.studynotion_modern.backend.entities.OTP;
import com.studynotion_modern.backend.entities.Profile;
import com.studynotion_modern.backend.entities.User;
import com.studynotion_modern.backend.repository.OTPRepository;
import com.studynotion_modern.backend.repository.ProfileRepository;
import com.studynotion_modern.backend.repository.UserRepository;
import com.studynotion_modern.backend.service.MailService;
import com.studynotion_modern.backend.utils.JwtUtil;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final OTPRepository otpRepository;
    private final ProfileRepository profileRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final MailService mailService;

    // Signup
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody SignupRequestDto signupRequestDto) {

        if (!signupRequestDto.getPassword().equals(signupRequestDto.getConfirmPassword())) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponseDto(false, "Password and Confirm Password do not match. Please try again."));
        }
        if (userRepository.findByEmail(signupRequestDto.getEmail()).isPresent()) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponseDto(false, "User already exists. Please sign in to continue."));
        }
        // Validate OTP
        Optional<OTP> otpRecord = otpRepository.findTopByEmailOrderByCreatedAtDesc(signupRequestDto.getEmail());
        if (otpRecord.isEmpty() || !otpRecord.get().getOtp().equals(signupRequestDto.getOtp())) {
            return ResponseEntity.badRequest().body(new ApiResponseDto(false, "The OTP is not valid"));
        }
        // Hash password
        String hashedPassword = passwordEncoder.encode(signupRequestDto.getPassword());
        // Set approved based on accountType
        boolean approved = !"Instructor".equals(signupRequestDto.getAccountType());
        // Create profile
        Profile profile = new Profile();
        profile.setGender(null);
        profile.setDateOfBirth(null);
        profile.setAbout(null);
        profile.setContactNumber(null);
        profileRepository.save(profile);
        // Create user
        User user = new User();
        user.setFirstName(signupRequestDto.getFirstName());
        user.setLastName(signupRequestDto.getLastName());
        user.setEmail(signupRequestDto.getEmail());
        user.setAdditionalDetails(profile);
        user.setPassword(hashedPassword);
        user.setAccountType(signupRequestDto.getAccountType());
        user.setApproved(approved);
        user.setAdditionalDetails(profile);
        user.setImage("");
        userRepository.save(user);
        return ResponseEntity.ok(new SignUpResponseDto(true, user, "User registered successfully"));
    }

    // Login
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDto loginRequestDto) {
        Optional<User> userOpt = userRepository.findByEmail(loginRequestDto.getEmail());
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(401)
                    .body(new ApiResponseDto(false, "User is not Registered with Us Please SignUp to Continue"));
        }
        User user = userOpt.get();
        if (passwordEncoder.matches(loginRequestDto.getPassword(), user.getPassword())) {
            String token = jwtUtil.generateToken(user);
            user.setToken(token);
            user.setPassword(null); // Hide password
            LoginResponseDto loginResponseDto = new LoginResponseDto(true, token, user, "User Login Success");
            return ResponseEntity.ok(loginResponseDto);
        } else {
            return ResponseEntity.status(401).body(new ApiResponseDto(false, "Password is incorrect"));
        }
    }

    // Send OTP
    @PostMapping("/send-otp")
    public ResponseEntity<?> sendOtp(@Valid @RequestBody SendOtpRequestDto sendOtpRequestDto) {
        // Generate 6-digit OTP
        String otp = String.format("%06d", new Random().nextInt(999999));
        // Ensure OTP is unique
        while (otpRepository.findByOtp(otp).isPresent()) {
            otp = String.format("%06d", new Random().nextInt(999999));
        }
        OTP otpEntity = new OTP();
        otpEntity.setEmail(sendOtpRequestDto.getEmail());
        otpEntity.setOtp(otp);
        otpEntity.setCreatedAt(LocalDateTime.now());
        otpRepository.save(otpEntity);
        // Optionally, send OTP via email
        mailService.sendOtp(sendOtpRequestDto.getEmail(), otp);
        return ResponseEntity.ok(new OtpResponseDto(true, "OTP Sent Successfully", otp));
    }

    // Change Password
    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequestDto changePasswordRequestDto,
            @RequestHeader("userId") String userId) {
        Optional<User> userOpt = userRepository.findById(new ObjectId(userId));
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(401).body(new ApiResponseDto(false, "User not found"));
        }
        User user = userOpt.get();
        if (!passwordEncoder.matches(changePasswordRequestDto.getOldPassword(), user.getPassword())) {
            return ResponseEntity.status(401).body(new ApiResponseDto(false, "The password is incorrect"));
        }
        user.setPassword(passwordEncoder.encode(changePasswordRequestDto.getNewPassword()));
        userRepository.save(user);
        // Optionally, send notification email
        // mailService.sendPasswordUpdate(user.getEmail(), ...);
        return ResponseEntity.ok(new ApiResponseDto(true, "Password updated successfully"));
    }

}
