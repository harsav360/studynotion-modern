package com.studynotion_modern.backend.service;

import com.studynotion_modern.backend.dtos.SectionRequestDto;
import com.studynotion_modern.backend.entities.Course;
import com.studynotion_modern.backend.entities.Section;
import com.studynotion_modern.backend.entities.SubSection;
import com.studynotion_modern.backend.repository.CourseRepository;
import com.studynotion_modern.backend.repository.SectionRepository;
import com.studynotion_modern.backend.repository.SubSectionRepository;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SectionService {

    private final SectionRepository sectionRepository;
    private final CourseRepository courseRepository;
    private final SubSectionRepository subSectionRepository;

    public ResponseEntity<Map<String, Object>> createSection(SectionRequestDto request) {
        try {
            // Validate request
            if (request.getSectionName() == null || request.getSectionName().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Section name is required"));
            }

            if (request.getCourseId() == null || request.getCourseId().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Course ID is required"));
            }

            // Find the course
            ObjectId courseId = new ObjectId(request.getCourseId());
            Course course = courseRepository.findById(courseId).orElse(null);

            if (course == null) {
                return ResponseEntity.status(404).body(Map.of("success", false, "message", "Course not found"));
            }

            // Create new section
            Section section = Section.builder()
                    .sectionName(request.getSectionName())
                    .subSection(new ArrayList<>())
                    .build();

            Section savedSection = sectionRepository.save(section);

            // Add section to course's content
            if (course.getCourseContent() == null) {
                course.setCourseContent(new ArrayList<>());
            }
            course.getCourseContent().add(savedSection);
            courseRepository.save(course);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Section created successfully",
                    "data", savedSection
            ));

        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                    "success", false,
                    "message", "Failed to create section: " + e.getMessage()
            ));
        }
    }

    public ResponseEntity<Map<String, Object>> updateSection(SectionRequestDto request) {
        try {
            // Validate request
            if (request.getSectionId() == null || request.getSectionId().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Section ID is required"));
            }

            if (request.getSectionName() == null || request.getSectionName().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Section name is required"));
            }

            // Find the section
            ObjectId sectionId = new ObjectId(request.getSectionId());
            Section section = sectionRepository.findById(sectionId).orElse(null);

            if (section == null) {
                return ResponseEntity.status(404).body(Map.of("success", false, "message", "Section not found"));
            }

            // Update section
            section.setSectionName(request.getSectionName());
            Section updatedSection = sectionRepository.save(section);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Section updated successfully",
                    "data", updatedSection
            ));

        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                    "success", false,
                    "message", "Failed to update section: " + e.getMessage()
            ));
        }
    }

    public ResponseEntity<Map<String, Object>> deleteSection(SectionRequestDto request) {
        try {
            // Validate request
            if (request.getSectionId() == null || request.getSectionId().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Section ID is required"));
            }

            if (request.getCourseId() == null || request.getCourseId().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Course ID is required"));
            }

            // Find the section
            ObjectId sectionId = new ObjectId(request.getSectionId());
            Section section = sectionRepository.findById(sectionId).orElse(null);

            if (section == null) {
                return ResponseEntity.status(404).body(Map.of("success", false, "message", "Section not found"));
            }

            // Find the course
            ObjectId courseId = new ObjectId(request.getCourseId());
            Course course = courseRepository.findById(courseId).orElse(null);

            if (course == null) {
                return ResponseEntity.status(404).body(Map.of("success", false, "message", "Course not found"));
            }

            // Delete all subsections in this section
            if (section.getSubSection() != null && !section.getSubSection().isEmpty()) {
                for (SubSection subSection : section.getSubSection()) {
                    subSectionRepository.delete(subSection);
                }
            }

            // Remove section from course's content
            if (course.getCourseContent() != null) {
                course.getCourseContent().removeIf(s -> s.getId().equals(sectionId));
                courseRepository.save(course);
            }

            // Delete the section
            sectionRepository.delete(section);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Section deleted successfully"
            ));

        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                    "success", false,
                    "message", "Failed to delete section: " + e.getMessage()
            ));
        }
    }
}
