package com.studynotion_modern.backend.dtos;

import lombok.Data;

import java.util.List;

@Data
public class CategoryRequestDto {
    private String name;
    private String description;
    private List<String> courseIds;
}
