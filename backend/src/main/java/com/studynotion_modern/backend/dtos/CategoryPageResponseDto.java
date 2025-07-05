package com.studynotion_modern.backend.dtos;

import java.util.List;

import org.springframework.stereotype.Component;

import com.studynotion_modern.backend.entities.Category;
import com.studynotion_modern.backend.entities.Course;

import lombok.AllArgsConstructor;
import lombok.Data;

@Component
@AllArgsConstructor
@Data
public class CategoryPageResponseDto {

    Category selectedCategory;
    Category differentCategory;
    List<Course> mostSellingCourses;
}
