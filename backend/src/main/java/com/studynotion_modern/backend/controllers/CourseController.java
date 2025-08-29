package com.studynotion_modern.backend.controllers;

import java.security.Principal;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.studynotion_modern.backend.dtos.CourseRequestDto;
import com.studynotion_modern.backend.service.CourseService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/course")
public class CourseController {

    private final CourseService courseService;

    @PostMapping("/create")
    public ResponseEntity<?> createCourse(@RequestPart("data") CourseRequestDto courseRequest,
            @RequestPart("thumbnailImage") MultipartFile thumbnail,
            Principal principal) {
        return courseService.createCourse(courseRequest, thumbnail, principal.getName());
    }

    @PutMapping("/edit")
    public ResponseEntity<?> editCourse(@RequestParam("courseId") String courseId,
            @RequestParam Map<String, String> updates,
            @RequestPart(value = "thumbnail", required = false) MultipartFile thumbnail) {
        return courseService.editCourse(courseId, updates, thumbnail);
    }

    @GetMapping("/details/{courseId}")
    public ResponseEntity<?> getCourseDetails(@PathVariable String courseId) {
        return courseService.getCourseDetails(courseId);
    }

    @GetMapping("/instructor")
    public ResponseEntity<?> getInstructorCourses(Principal principal) {
        return courseService.getInstructorCourses(principal.getName());
    }

    @DeleteMapping("/delete/{courseId}")
    public ResponseEntity<?> deleteCourse(@PathVariable String courseId) {
        return courseService.deleteCourse(courseId);
    }

    @PostMapping("/progress/update")
    public ResponseEntity<?> updateProgress(@RequestBody Map<String, String> request, Principal principal) {
        return courseService.updateCourseProgress(
                request.get("courseId"),
                request.get("subsectionId"),
                principal.getName());
    }

}
