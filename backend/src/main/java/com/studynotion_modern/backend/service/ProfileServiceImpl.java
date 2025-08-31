package com.studynotion_modern.backend.service;

import com.studynotion_modern.backend.dtos.UserDto;
import com.studynotion_modern.backend.entities.Course;
import com.studynotion_modern.backend.entities.Profile;
import com.studynotion_modern.backend.entities.User;
import com.studynotion_modern.backend.repository.CourseProgressRepository;
import com.studynotion_modern.backend.repository.CourseRepository;
import com.studynotion_modern.backend.repository.ProfileRepository;
import com.studynotion_modern.backend.repository.UserRepository;
import com.studynotion_modern.backend.utils.DurationUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl {

    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final CourseRepository courseRepository;
    private final CourseProgressRepository courseProgressRepository;
    private final CloudinaryService cloudinaryService;

    public ResponseEntity<Map<String, Object>> updateProfile(UserDto dto, HttpServletRequest request) {
        ObjectId userId = new ObjectId(request.getUserPrincipal().getName());
        Optional<User> userOpt = userRepository.findById(userId);

        if (userOpt.isEmpty())
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "User not found"));

        User user = userOpt.get();
        Profile profile = profileRepository.findById(user.getAdditionalDetails().getId())
                .orElseThrow(() -> new NoSuchElementException("Profile not found"));

        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        userRepository.save(user);

        profile.setDateOfBirth(dto.getDateOfBirth());
        profile.setAbout(dto.getAbout());
        profile.setContactNumber(dto.getContactNumber());
        profile.setGender(dto.getGender());
        profileRepository.save(profile);

        User updatedUser = userRepository.findById(userId).orElseThrow();
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Profile updated successfully",
                "updatedUserDetails", updatedUser));
    }

    public ResponseEntity<Map<String, Object>> deleteAccount(HttpServletRequest request) {
        ObjectId userId = new ObjectId(request.getUserPrincipal().getName());
        User user = userRepository.findById(userId).orElse(null);

        if (user == null)
            return ResponseEntity.notFound().build();

        profileRepository.deleteById(user.getAdditionalDetails().getId());

        // Remove user from enrolled courses
        for (Course course : user.getCourses()) {
            course.getStudentsEnrolled().remove(user);
            courseRepository.save(course);
        }

        // Delete course progress records for this user
        courseProgressRepository.findAll().stream()
                .filter(progress -> progress.getUserId().equals(user))
                .forEach(courseProgressRepository::delete);

        userRepository.deleteById(userId);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "User deleted successfully"));
    }

    public ResponseEntity<Map<String, Object>> getAllUserDetails(HttpServletRequest request) {
        ObjectId userId = new ObjectId(request.getUserPrincipal().getName());
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found"));

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "User Data fetched successfully",
                "data", user));
    }

    public ResponseEntity<Map<String, Object>> updateDisplayPicture(MultipartFile file, HttpServletRequest request) {
        ObjectId userId = new ObjectId(request.getUserPrincipal().getName());

        try {
            String url = cloudinaryService.uploadImage(file, "profile_pictures");
            User user = userRepository.findById(userId).orElseThrow();
            user.setImage(url);
            userRepository.save(user);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Image Updated successfully",
                    "data", user));
        } catch (IOException e) {
            return ResponseEntity.status(500).body(Map.of(
                    "success", false,
                    "message", "Failed to upload image: " + e.getMessage()));
        }
    }

    public ResponseEntity<Map<String, Object>> getEnrolledCourses(HttpServletRequest request) {
        ObjectId userId = new ObjectId(request.getUserPrincipal().getName());
        User user = userRepository.findById(userId).orElseThrow();

        List<Map<String, Object>> updatedCourses = user.getCourses().stream().map(course -> {
            int totalSeconds = course.getCourseContent().stream()
                    .flatMap(sec -> sec.getSubSection().stream())
                    .mapToInt(ss -> Integer.parseInt(ss.getTimeDuration()))
                    .sum();

            int totalSubSections = course.getCourseContent().stream()
                    .mapToInt(sec -> sec.getSubSection().size()).sum();

            // Count completed videos for this user
            long completed = courseProgressRepository.findAll().stream()
                    .filter(progress -> progress.getCourseID().equals(course) && progress.getUserId().equals(user))
                    .mapToLong(progress -> progress.getCompletedVideos().size())
                    .sum();

            double percentage = totalSubSections == 0 ? 100.0 : ((double) completed / totalSubSections) * 100.0;

            return Map.of(
                    "course", course,
                    "totalDuration", DurationUtils.convertSecondsToDuration(totalSeconds),
                    "progressPercentage", Math.round(percentage * 100.0) / 100.0
            );
        }).toList();

        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", updatedCourses));
    }

    public ResponseEntity<Map<String, Object>> instructorDashboard(HttpServletRequest request) {
        ObjectId instructorId = new ObjectId(request.getUserPrincipal().getName());
        List<Course> courses = courseRepository.findAll().stream()
                .filter(course -> course.getInstructor().getId().equals(instructorId))
                .toList();

        List<Map<String, Object>> dashboardData = courses.stream()
                .map(course -> Map.<String, Object>of(
                        "_id", course.getId(),
                        "courseName", course.getCourseName(),
                        "courseDescription", course.getCourseDescription(),
                        "totalStudentsEnrolled", course.getStudentsEnrolled().size(),
                        "totalAmountGenerated", course.getStudentsEnrolled().size() * course.getPrice()))
                .toList();

        return ResponseEntity.ok(Map.of("courses", dashboardData));
    }
}
