package com.studynotion_modern.backend.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @PutMapping("/update")
    public ResponseEntity<?> updateProfile(@RequestBody UserDto dto, HttpServletRequest request) {
        return profileService.updateProfile(dto, request);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteAccount(HttpServletRequest request) {
        return profileService.deleteAccount(request);
    }

    @GetMapping("/details")
    public ResponseEntity<?> getAllUserDetails(HttpServletRequest request) {
        return profileService.getAllUserDetails(request);
    }

    @PostMapping("/update-display-picture")
    public ResponseEntity<?> updateDisplayPicture(@RequestParam MultipartFile displayPicture,
            HttpServletRequest request) {
        return profileService.updateDisplayPicture(displayPicture, request);
    }

    @GetMapping("/enrolled-courses")
    public ResponseEntity<?> getEnrolledCourses(HttpServletRequest request) {
        return profileService.getEnrolledCourses(request);
    }

    @GetMapping("/instructor-dashboard")
    public ResponseEntity<?> instructorDashboard(HttpServletRequest request) {
        return profileService.instructorDashboard(request);
    }

    @PostMapping("/reset-password-token")
    public ResponseEntity<?> sendResetPasswordToken(@RequestBody ResetPasswordTokenRequest request) {
        return authService.sendResetPasswordToken(request);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {
        return authService.resetPassword(request);
    }
}
