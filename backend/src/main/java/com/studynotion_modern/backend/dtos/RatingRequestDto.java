package com.studynotion_modern.backend.dtos;

import lombok.Data;

@Data
public class RatingRequestDto {
    private String courseId;
    private int rating;
    private String review;
}
