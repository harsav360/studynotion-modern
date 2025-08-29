package com.studynotion_modern.backend.dtos;

import java.util.List;

import com.studynotion_modern.backend.entities.Category;
import com.studynotion_modern.backend.entities.Course;

import lombok.Data;

@Data
public class CategoryPageResponseDto {

    Category selectedCategory;
    Category differentCategory;
    List<Course> mostSellingCourses;
}
