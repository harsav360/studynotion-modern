package com.studynotion_modern.backend.controllers;

import com.studynotion_modern.backend.dtos.SubSectionRequestDto;
import com.studynotion_modern.backend.service.SubSectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/subsection")
@RequiredArgsConstructor
public class SubSectionController {

    private final SubSectionService subSectionService;

    @PostMapping("/create")
    public ResponseEntity<?> createSubSection(@ModelAttribute SubSectionRequestDto request) {
        return subSectionService.createSubSection(request);
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateSubSection(@ModelAttribute SubSectionRequestDto request) {
        return subSectionService.updateSubSection(request);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteSubSection(@RequestBody SubSectionRequestDto request) {
        return subSectionService.deleteSubSection(request);
    }
}