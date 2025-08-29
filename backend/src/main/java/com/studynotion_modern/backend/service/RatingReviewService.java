package com.studynotion_modern.backend.service;

import com.studynotion_modern.backend.entities.Course;
import com.studynotion_modern.backend.entities.RatingAndReview;
import com.studynotion_modern.backend.repository.CourseRepository;
import com.studynotion_modern.backend.repository.UserRepository;
import com.studynotion_modern.backend.repository.RatingAndReviewRepository;
import com.studynotion_modern.backend.dtos.RatingRequestDto;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RatingReviewService {

    private final CourseRepository courseRepository;
    private final RatingAndReviewRepository ratingReviewRepository;
    private final UserRepository userRepository;

    public ResponseEntity<?> createRating(RatingRequestDto request, HttpServletRequest httpRequest) {
        String userId = (String) httpRequest.getAttribute("userId");
        Course course = courseRepository.findById(request.getCourseId()).orElse(null);

        if (course == null || !course.getStudentsEnrolled().contains(userId)) {
            return ResponseEntity.status(404).body("Student is not enrolled in the course");
        }

        boolean alreadyReviewed = ratingReviewRepository
                .existsByUserAndCourse(new ObjectId(userId), new ObjectId(request.getCourseId()));
        if (alreadyReviewed) {
            return ResponseEntity.status(403).body("Course is already reviewed by the user");
        }

        RatingAndReview review = new RatingAndReview();
        review.setUser(new ObjectId(userId));
        review.setCourse(new ObjectId(request.getCourseId()));
        review.setRating(request.getRating());
        review.setReview(request.getReview());
        ratingReviewRepository.save(review);

        course.getRatingAndReviews().add(review.getId());
        courseRepository.save(course);

        return ResponseEntity.ok().body(review);
    }

    public ResponseEntity<?> getAverageRating(String courseId) {
        Double averageRating = ratingReviewRepository.findAverageRatingByCourse(new ObjectId(courseId));
        return ResponseEntity.ok().body(
                averageRating != null
                        ? averageRating
                        : "Average Rating is 0, no ratings given till now");
    }

    public ResponseEntity<?> getAllRatings() {
        List<RatingAndReview> reviews = ratingReviewRepository.findAllSortedWithDetails();
        return ResponseEntity.ok().body(reviews);
    }
}
