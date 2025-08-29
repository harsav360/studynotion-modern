package com.studynotion_modern.backend.dtos;

import lombok.Data;

@Data
public class CourseRequestDto {

    private String courseName;
    private String courseDescription;
    private String whatYouWillLearn;
    private Double price;
    private String tag; // JSON string, will be parsed
    private String category;
    private String status;
    private String instructions; // JSON string
}
