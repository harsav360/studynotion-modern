package com.studynotion_modern.backend.service;

import com.studynotion_modern.backend.dtos.SubSectionRequestDto;
import com.studynotion_modern.backend.entities.Section;
import com.studynotion_modern.backend.entities.SubSection;
import com.studynotion_modern.backend.repository.SectionRepository;
import com.studynotion_modern.backend.repository.SubSectionRepository;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SubSectionService {

    private final SubSectionRepository subSectionRepository;
    private final SectionRepository sectionRepository;
    private final CloudinaryService cloudinaryService;

    public ResponseEntity<Map<String, Object>> createSubSection(SubSectionRequestDto request) {
        try {
            // Validate request
            if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Title is required"));
            }

            if (request.getSectionId() == null || request.getSectionId().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Section ID is required"));
            }

            if (request.getDescription() == null || request.getDescription().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Description is required"));
            }

            if (request.getTimeDuration() == null || request.getTimeDuration().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Time duration is required"));
            }

            // Find the section
            ObjectId sectionId = new ObjectId(request.getSectionId());
            Section section = sectionRepository.findById(sectionId).orElse(null);

            if (section == null) {
                return ResponseEntity.status(404).body(Map.of("success", false, "message", "Section not found"));
            }

            // Create new subsection
            SubSection subSection = SubSection.builder()
                    .title(request.getTitle())
                    .description(request.getDescription())
                    .timeDuration(request.getTimeDuration())
                    .videoUrl(request.getVideoUrl()) // Can be null initially
                    .build();

            SubSection savedSubSection = subSectionRepository.save(subSection);

            // Add subsection to section's subsection list
            if (section.getSubSection() == null) {
                section.setSubSection(new ArrayList<>());
            }
            section.getSubSection().add(savedSubSection);
            sectionRepository.save(section);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "SubSection created successfully",
                    "data", savedSubSection
            ));

        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                    "success", false,
                    "message", "Failed to create subsection: " + e.getMessage()
            ));
        }
    }

    public ResponseEntity<Map<String, Object>> updateSubSection(SubSectionRequestDto request) {
        try {
            // Validate request
            if (request.getSubSectionId() == null || request.getSubSectionId().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("success", false, "message", "SubSection ID is required"));
            }

            // Find the subsection
            ObjectId subSectionId = new ObjectId(request.getSubSectionId());
            SubSection subSection = subSectionRepository.findById(subSectionId).orElse(null);

            if (subSection == null) {
                return ResponseEntity.status(404).body(Map.of("success", false, "message", "SubSection not found"));
            }

            // Update fields if provided
            if (request.getTitle() != null && !request.getTitle().trim().isEmpty()) {
                subSection.setTitle(request.getTitle());
            }

            if (request.getDescription() != null && !request.getDescription().trim().isEmpty()) {
                subSection.setDescription(request.getDescription());
            }

            if (request.getTimeDuration() != null && !request.getTimeDuration().trim().isEmpty()) {
                subSection.setTimeDuration(request.getTimeDuration());
            }

            if (request.getVideoUrl() != null && !request.getVideoUrl().trim().isEmpty()) {
                subSection.setVideoUrl(request.getVideoUrl());
            }

            SubSection updatedSubSection = subSectionRepository.save(subSection);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "SubSection updated successfully",
                    "data", updatedSubSection
            ));

        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                    "success", false,
                    "message", "Failed to update subsection: " + e.getMessage()
            ));
        }
    }

    public ResponseEntity<Map<String, Object>> deleteSubSection(SubSectionRequestDto request) {
        try {
            // Validate request
            if (request.getSubSectionId() == null || request.getSubSectionId().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("success", false, "message", "SubSection ID is required"));
            }

            if (request.getSectionId() == null || request.getSectionId().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Section ID is required"));
            }

            // Find the subsection
            ObjectId subSectionId = new ObjectId(request.getSubSectionId());
            SubSection subSection = subSectionRepository.findById(subSectionId).orElse(null);

            if (subSection == null) {
                return ResponseEntity.status(404).body(Map.of("success", false, "message", "SubSection not found"));
            }

            // Find the section
            ObjectId sectionId = new ObjectId(request.getSectionId());
            Section section = sectionRepository.findById(sectionId).orElse(null);

            if (section == null) {
                return ResponseEntity.status(404).body(Map.of("success", false, "message", "Section not found"));
            }

            // Remove subsection from section's subsection list
            if (section.getSubSection() != null) {
                section.getSubSection().removeIf(ss -> ss.getId().equals(subSectionId));
                sectionRepository.save(section);
            }

            // Delete the subsection
            subSectionRepository.delete(subSection);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "SubSection deleted successfully"
            ));

        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                    "success", false,
                    "message", "Failed to delete subsection: " + e.getMessage()
            ));
        }
    }

    public ResponseEntity<Map<String, Object>> uploadVideo(MultipartFile video, String subSectionId) {
        try {
            // Validate request
            if (video == null || video.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Video file is required"));
            }

            if (subSectionId == null || subSectionId.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("success", false, "message", "SubSection ID is required"));
            }

            // Find the subsection
            ObjectId subSectionObjectId = new ObjectId(subSectionId);
            SubSection subSection = subSectionRepository.findById(subSectionObjectId).orElse(null);

            if (subSection == null) {
                return ResponseEntity.status(404).body(Map.of("success", false, "message", "SubSection not found"));
            }

            // Upload video to Cloudinary
            String videoUrl = cloudinaryService.uploadImage(video, "course_videos");

            // Update subsection with video URL
            subSection.setVideoUrl(videoUrl);
            SubSection updatedSubSection = subSectionRepository.save(subSection);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Video uploaded successfully",
                    "data", updatedSubSection
            ));

        } catch (IOException e) {
            return ResponseEntity.status(500).body(Map.of(
                    "success", false,
                    "message", "Failed to upload video: " + e.getMessage()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                    "success", false,
                    "message", "Failed to process video upload: " + e.getMessage()
            ));
        }
    }

    public ResponseEntity<Map<String, Object>> getSubSection(String subSectionId) {
        try {
            // Validate request
            if (subSectionId == null || subSectionId.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("success", false, "message", "SubSection ID is required"));
            }

            // Find the subsection
            ObjectId subSectionObjectId = new ObjectId(subSectionId);
            SubSection subSection = subSectionRepository.findById(subSectionObjectId).orElse(null);

            if (subSection == null) {
                return ResponseEntity.status(404).body(Map.of("success", false, "message", "SubSection not found"));
            }

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", subSection
            ));

        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                    "success", false,
                    "message", "Failed to retrieve subsection: " + e.getMessage()
            ));
        }
    }
}
