package com.studynotion_modern.backend.dtos;

import lombok.Data;

@Data
public class SectionRequestDto {
    private String sectionId;
    private String sectionName;
    private String courseId;
}
