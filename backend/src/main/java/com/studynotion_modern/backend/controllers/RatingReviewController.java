package com.studynotion_modern.backend.controllers;

import com.studynotion_modern.backend.service.RatingReviewService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/review")
@RequiredArgsConstructor
public class RatingReviewController {

    private final RatingReviewService ratingReviewService;

    @PostMapping("/create")
    public ResponseEntity<?> createRating(@RequestBody RatingRequest request, HttpServletRequest servletRequest) {
        return ratingReviewService.createRating(request, servletRequest);
    }

    @PostMapping("/average")
    public ResponseEntity<?> getAverageRating(@RequestBody RatingRequest request) {
        return ratingReviewService.getAverageRating(request.getCourseId());
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllRatings() {
        return ratingReviewService.getAllRatings();
    }
}