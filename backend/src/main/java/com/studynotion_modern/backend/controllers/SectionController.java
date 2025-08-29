package com.studynotion_modern.backend.controllers;

@RestController
@RequestMapping("/api/v1/section")
@RequiredArgsConstructor
public class SectionController {

    private final SectionService sectionService;

    @PostMapping("/create")
    public ResponseEntity<?> createSection(@RequestBody SectionRequest request) {
        return sectionService.createSection(request);
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateSection(@RequestBody SectionRequest request) {
        return sectionService.updateSection(request);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteSection(@RequestBody SectionRequest request) {
        return sectionService.deleteSection(request);
    }
}