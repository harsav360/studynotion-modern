package com.studynotion_modern.backend.controllers;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.studynotion_modern.backend.dtos.ContactRequestDto;
import com.studynotion_modern.backend.service.MailService;
import com.studynotion_modern.backend.utils.EmailTemplates;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/reach")
@RequiredArgsConstructor
public class ContactUsController {

    private final MailService mailService;

    @PostMapping
    public ResponseEntity<?> contactUs(@RequestBody ContactRequestDto contactRequest) {
        try {
            String html = EmailTemplates.contactUsEmail(
                    contactRequest.getEmail(), contactRequest.getFirstname(), contactRequest.getLastname(),
                    contactRequest.getMessage(), contactRequest.getPhoneNo(), contactRequest.getCountrycode());

            mailService.sendMail(
                    contactRequest.getEmail(),
                    "Your Data sent successfully",
                    html);

            return ResponseEntity.ok().body(
                    Map.of("success", true, "message", "Email sent successfully"));
        } catch (MessagingException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(
                    Map.of("success", false, "message", "Something went wrong..."));
        }
    }
}
