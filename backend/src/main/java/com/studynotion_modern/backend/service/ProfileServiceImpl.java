package com.studynotion_modern.backend.service;

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
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl {

    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final CourseRepository courseRepository;
    private final CourseProgressRepository courseProgressRepository;
    private final CloudinaryUtils cloudinaryUtils;


    public ResponseEntity<?> updateProfile(UserDto dto, HttpServletRequest request) {
        String userId = request.getUserPrincipal().getName();
        Optional<User> userOpt = userRepository.findById(userId);

        if (userOpt.isEmpty())
            return ResponseEntity.badRequest().body("User not found");

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

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Profile updated successfully",
                "updatedUserDetails", userRepository.findById(userId).get()));
    }


    public ResponseEntity<?> deleteAccount(HttpServletRequest request) {
        String userId = request.getUserPrincipal().getName();
        User user = userRepository.findById(userId).orElse(null);

        if (user == null)
            return ResponseEntity.notFound().build();

        profileRepository.deleteById(user.getAdditionalDetails().getId());

        for (String courseId : user.getCourses()) {
            courseRepository.updateStudentUnenroll(courseId, userId);
        }

        courseProgressRepository.deleteByUserId(userId);
        userRepository.deleteById(userId);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "User deleted successfully"));
    }


    public ResponseEntity<?> getAllUserDetails(HttpServletRequest request) {
        String userId = request.getUserPrincipal().getName();
        User user = userRepository.findByIdWithProfile(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found"));

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "User Data fetched successfully",
                "data", user));
    }


    public ResponseEntity<?> updateDisplayPicture(MultipartFile file, HttpServletRequest request) {
        String userId = request.getUserPrincipal().getName();
        String url = cloudinaryUtils.uploadImage(file, 1000, 1000);

        User user = userRepository.findById(userId).orElseThrow();
        user.setImage(url);
        userRepository.save(user);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Image Updated successfully",
                "data", user));
    }


    public ResponseEntity<?> getEnrolledCourses(HttpServletRequest request) {
        String userId = request.getUserPrincipal().getName();
        User user = userRepository.findEnrolledCourses(userId).orElseThrow();

        List<Course> updatedCourses = user.getCourses().stream().map(course -> {
            int totalSeconds = course.getCourseContent().stream()
                    .flatMap(sec -> sec.getSubSection().stream())
                    .mapToInt(ss -> Integer.parseInt(ss.getTimeDuration()))
                    .sum();

            int totalSubSections = course.getCourseContent().stream()
                    .mapToInt(sec -> sec.getSubSection().size()).sum();

            int completed = courseProgressRepository.findCompletedCount(course.getId(), userId);
            double percentage = totalSubSections == 0 ? 100.0 : ((double) completed / totalSubSections) * 100.0;

            course.setTotalDuration(DurationUtils.convertSecondsToDuration(totalSeconds));
            course.setProgressPercentage(Math.round(percentage * 100.0) / 100.0);
            return course;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", updatedCourses));
    }


    public ResponseEntity<?> instructorDashboard(HttpServletRequest request) {
        String instructorId = request.getUserPrincipal().getName();
        List<Course> courses = courseRepository.findByInstructor(instructorId);

        List<Map<String, Object>> dashboardData = courses.stream().map(course -> Map.of(
                "_id", course.getId(),
                "courseName", course.getCourseName(),
                "courseDescription", course.getCourseDescription(),
                "totalStudentsEnrolled", course.getStudentsEnrolled().size(),
                "totalAmountGenerated", course.getStudentsEnrolled().size() * course.getPrice()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(Map.of("courses", dashboardData));
    }
}
