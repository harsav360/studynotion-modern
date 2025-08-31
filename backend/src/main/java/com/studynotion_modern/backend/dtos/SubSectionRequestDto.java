package com.studynotion_modern.backend.dtos;

import lombok.Data;

@Data
public class SubSectionRequestDto {
    private String subSectionId;
    private String sectionId;
    private String title;
    private String timeDuration;
    private String description;
    private String videoUrl;
}
