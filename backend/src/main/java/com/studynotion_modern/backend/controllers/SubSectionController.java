package com.studynotion_modern.backend.controllers;

@RestController
@RequestMapping("/api/v1/subsection")
@RequiredArgsConstructor
public class SubSectionController {

    private final SubSectionService subSectionService;

    @PostMapping("/create")
    public ResponseEntity<?> createSubSection(@ModelAttribute SubSectionRequest request) {
        return subSectionService.createSubSection(request);
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateSubSection(@ModelAttribute SubSectionRequest request) {
        return subSectionService.updateSubSection(request);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteSubSection(@RequestBody SubSectionRequest request) {
        return subSectionService.deleteSubSection(request);
    }
}