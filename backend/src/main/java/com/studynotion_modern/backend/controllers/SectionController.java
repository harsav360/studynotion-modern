package com.studynotion_modern.backend.controllers;

import com.studynotion_modern.backend.dtos.SectionRequestDto;
import com.studynotion_modern.backend.service.SectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/section")
@RequiredArgsConstructor
public class SectionController {

    private final SectionService sectionService;

    @PostMapping("/create")
    public ResponseEntity<?> createSection(@RequestBody SectionRequestDto request) {
        return sectionService.createSection(request);
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateSection(@RequestBody SectionRequestDto request) {
        return sectionService.updateSection(request);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteSection(@RequestBody SectionRequestDto request) {
        return sectionService.deleteSection(request);
    }
}