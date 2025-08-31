package com.studynotion_modern.backend.service;

import com.studynotion_modern.backend.entities.Course;
import com.studynotion_modern.backend.entities.RatingAndReview;
import com.studynotion_modern.backend.entities.User;
import com.studynotion_modern.backend.repository.CourseRepository;
import com.studynotion_modern.backend.repository.UserRepository;
import com.studynotion_modern.backend.repository.RatingAndReviewRepository;
import com.studynotion_modern.backend.dtos.RatingRequestDto;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RatingReviewService {

    private final CourseRepository courseRepository;
    private final RatingAndReviewRepository ratingReviewRepository;
    private final UserRepository userRepository;

    public ResponseEntity<Map<String, Object>> createRating(RatingRequestDto request, HttpServletRequest httpRequest) {
        String userId = (String) httpRequest.getAttribute("userId");
        ObjectId userObjectId = new ObjectId(userId);
        ObjectId courseObjectId = new ObjectId(request.getCourseId());

        Course course = courseRepository.findById(courseObjectId).orElse(null);
        User user = userRepository.findById(userObjectId).orElse(null);

        if (course == null) {
            return ResponseEntity.status(404).body(Map.of("success", false, "message", "Course not found"));
        }

        if (user == null) {
            return ResponseEntity.status(404).body(Map.of("success", false, "message", "User not found"));
        }

        // Check if user is enrolled in the course
        if (!course.getStudentsEnrolled().contains(user)) {
            return ResponseEntity.status(404).body(Map.of("success", false, "message", "Student is not enrolled in the course"));
        }

        // Check if user already reviewed this course
        boolean alreadyReviewed = ratingReviewRepository.findAll().stream()
                .anyMatch(review -> review.getUser().equals(user) && review.getCourse().equals(course));

        if (alreadyReviewed) {
            return ResponseEntity.status(403).body(Map.of("success", false, "message", "Course is already reviewed by the user"));
        }

        RatingAndReview review = new RatingAndReview();
        review.setUser(user);
        review.setCourse(course);
        review.setRating(Double.valueOf(request.getRating()));
        review.setReview(request.getReview());
        ratingReviewRepository.save(review);

        // Add review to course's rating and reviews list
        course.getRatingAndReviews().add(review);
        courseRepository.save(course);

        return ResponseEntity.ok(Map.of("success", true, "message", "Rating created successfully", "data", review));
    }

    public ResponseEntity<Map<String, Object>> getAverageRating(String courseId) {
        ObjectId courseObjectId = new ObjectId(courseId);
        Course course = courseRepository.findById(courseObjectId).orElse(null);

        if (course == null) {
            return ResponseEntity.status(404).body(Map.of("success", false, "message", "Course not found"));
        }

        // Calculate average rating from all reviews for this course
        List<RatingAndReview> reviews = ratingReviewRepository.findAll().stream()
                .filter(review -> review.getCourse().equals(course))
                .toList();

        if (reviews.isEmpty()) {
            return ResponseEntity.ok(Map.of("success", true, "message", "No ratings yet", "averageRating", 0.0));
        }

        double averageRating = reviews.stream()
                .mapToDouble(RatingAndReview::getRating)
                .average()
                .orElse(0.0);

        return ResponseEntity.ok(Map.of("success", true, "averageRating", Math.round(averageRating * 100.0) / 100.0));
    }

    public ResponseEntity<Map<String, Object>> getAllRatings() {
        List<RatingAndReview> reviews = ratingReviewRepository.findAll();
        return ResponseEntity.ok(Map.of("success", true, "data", reviews));
    }
}
