package com.studynotion_modern.backend.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.studynotion_modern.backend.dtos.CourseRequestDto;
import com.studynotion_modern.backend.entities.Category;
import com.studynotion_modern.backend.entities.Course;
import com.studynotion_modern.backend.entities.CourseProgress;
import com.studynotion_modern.backend.entities.Section;
import com.studynotion_modern.backend.entities.SubSection;
import com.studynotion_modern.backend.entities.User;
import com.studynotion_modern.backend.repository.CategoryRepository;
import com.studynotion_modern.backend.repository.CourseProgressRepository;
import com.studynotion_modern.backend.repository.CourseRepository;
import com.studynotion_modern.backend.repository.SectionRepository;
import com.studynotion_modern.backend.repository.SubSectionRepository;
import com.studynotion_modern.backend.repository.UserRepository;
import com.studynotion_modern.backend.utils.DurationUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final CloudinaryService cloudinaryService;
    private final CourseRepository courseRepository;
    private final SectionRepository sectionRepository;
    private final SubSectionRepository subSectionRepository;
    private final CourseProgressRepository courseProgressRepository;
    private ObjectMapper objectMapper = new ObjectMapper();

    public ResponseEntity<?> createCourse(CourseRequestDto courseRequest, MultipartFile thumbnail, String userEmail) {
        try {
            // Validate request
            if (courseRequest.getCourseName() == null || thumbnail == null) {
                return ResponseEntity.badRequest().body(Map.of("message", "All fields are mandatory"));
            }

            // Get instructor user
            User instructor = userRepository.findByEmailAndAccountType(userEmail, "Instructor")
                    .orElseThrow(() -> new RuntimeException("Instructor not found"));

            // Validate category
            String categoryIdStr = courseRequest.getCategory();
            ObjectId categoryId = new ObjectId(categoryIdStr);
            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new RuntimeException("Category not found"));

            // Upload to Cloudinary
            String thumbnailUrl = cloudinaryService.uploadImage(thumbnail, "course-thumbnails");

            // Convert JSON Strings
            ObjectMapper mapper = new ObjectMapper();
            List<String> tags = mapper.readValue(courseRequest.getTag(), new TypeReference<>() {
            });
            List<String> instructions = mapper.readValue(courseRequest.getInstructions(), new TypeReference<>() {
            });

            // Save course
            Course course = Course.builder()
                    .courseName(courseRequest.getCourseName())
                    .courseDescription(courseRequest.getCourseDescription())
                    .whatYouWillLearn(courseRequest.getWhatYouWillLearn())
                    .price(courseRequest.getPrice())
                    .thumbnail(thumbnailUrl)
                    .tag(tags)
                    .instructions(instructions)
                    .category(category)
                    .instructor(instructor)
                    .status(Optional.ofNullable(courseRequest.getStatus()).orElse("Draft"))
                    .createdAt(LocalDateTime.now())
                    .build();

            Course savedCourse = courseRepository.save(course);

            // Link course to instructor and category
            instructor.getCourses().add(savedCourse);
            userRepository.save(instructor);

            category.getCourses().add(savedCourse);
            categoryRepository.save(category);

            return ResponseEntity.ok(Map.of("success", true, "data", savedCourse));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    public ResponseEntity<?> editCourse(String courseId, Map<String, String> updates, MultipartFile thumbnail) {
        try {
            ObjectId coursObjectId = new ObjectId(courseId);
            Course course = courseRepository.findById(coursObjectId)
                    .orElseThrow(() -> new RuntimeException("Course not found"));

            if (thumbnail != null) {
                String url = cloudinaryService.uploadImage(thumbnail, "course-thumbnails");
                course.setThumbnail(url);
            }

            for (Map.Entry<String, String> entry : updates.entrySet()) {
                switch (entry.getKey()) {
                    case "courseName" -> course.setCourseName(entry.getValue());
                    case "courseDescription" -> course.setCourseDescription(entry.getValue());
                    case "whatYouWillLearn" -> course.setWhatYouWillLearn(entry.getValue());
                    case "price" -> course.setPrice(Double.parseDouble(entry.getValue()));
                    case "status" -> course.setStatus(entry.getValue());
                    case "tag" -> course.setTag(objectMapper.readValue(entry.getValue(), new TypeReference<>() {
                    }));
                    case "instructions" ->
                        course.setInstructions(objectMapper.readValue(entry.getValue(), new TypeReference<>() {
                        }));
                }
            }

            courseRepository.save(course);
            return ResponseEntity.ok(Map.of("success", true, "message", "Course updated successfully", "data", course));

        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    // 2. Get Course Details with Duration
    public ResponseEntity<?> getCourseDetails(String courseId) {
        try {
            ObjectId courseObjectId = new ObjectId(courseId);
            Course course = courseRepository.findById(courseObjectId)
                    .orElseThrow(() -> new RuntimeException("Course not found"));

            int totalSeconds = 0;
            for (Section section : course.getCourseContent()) {
                for (SubSection sub : section.getSubSection()) {
                    totalSeconds += Integer.parseInt(sub.getTimeDuration());
                }
            }

            String duration = DurationUtils.convertSecondsToDuration(totalSeconds);
            return ResponseEntity.ok(Map.of("success", true, "data", course, "totalDuration", duration));

        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    // 3. Get Instructor Courses
    public ResponseEntity<?> getInstructorCourses(String userEmail) {
        try {
            User instructor = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            List<Course> courses = courseRepository.findByInstructorId(instructor.getId());
            return ResponseEntity.ok(Map.of("success", true, "data", courses));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    // 4. Delete Course with Cleanup
    public ResponseEntity<?> deleteCourse(String courseId) {
        try {
            ObjectId courseObjectId = new ObjectId(courseId);
            Course course = courseRepository.findById(courseObjectId)
                    .orElseThrow(() -> new RuntimeException("Course not found"));

            for (User student : course.getStudentsEnrolled()) {
                student.getCourses().remove(course);
                userRepository.save(student);
            }

            for (Section section : course.getCourseContent()) {
                for (SubSection subSection : section.getSubSection()) {
                    subSectionRepository.delete(subSection);
                }
                sectionRepository.delete(section);
            }

            courseRepository.delete(course);
            return ResponseEntity.ok(Map.of("success", true, "message", "Course deleted successfully"));

        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    // 5. Update Course Progress
    public ResponseEntity<?> updateCourseProgress(String courseId, String subsectionId, String userId) {
        try {
            ObjectId subSectionObjectId = new ObjectId(subsectionId);
            SubSection subSection = subSectionRepository.findById(subSectionObjectId)
                    .orElseThrow(() -> new RuntimeException("Invalid subsection"));

            CourseProgress courseProgress = courseProgressRepository
                    .findByCourseIdAndUserId(new ObjectId(courseId), new ObjectId(userId));

            if (courseProgress == null) {
                return ResponseEntity.status(404)
                        .body(Map.of("success", false, "message", "Course progress does not exist"));
            }

            if (courseProgress.getCompletedVideos().contains(subSectionObjectId)) {
                return ResponseEntity.badRequest().body(Map.of("error", "Subsection already completed"));
            }

            courseProgress.getCompletedVideos().add(subSectionObjectId);
            courseProgressRepository.save(courseProgress);

            return ResponseEntity.ok(Map.of("message", "Course progress updated"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Internal server error"));
        }
    }

}
