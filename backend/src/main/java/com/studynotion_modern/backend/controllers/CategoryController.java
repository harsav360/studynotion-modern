package com.studynotion_modern.backend.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.studynotion_modern.backend.dtos.ApiResponseDto;
import com.studynotion_modern.backend.dtos.CategoryPageRequestDto;
import com.studynotion_modern.backend.dtos.CategoryPageResponseDto;
import com.studynotion_modern.backend.entities.Category;
import com.studynotion_modern.backend.service.CategoryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<ApiResponseDto> createCategory(@RequestBody Category category) {
        if (category.getName() == null || category.getName().isEmpty()) {
            return ResponseEntity.badRequest().body(new ApiResponseDto(false, "All fields are required"));
        }
        categoryService.createCategory(category);
        return ResponseEntity.ok(new ApiResponseDto(true, "Category Created Successfully"));
    }

    @GetMapping
    public ResponseEntity<ApiResponseDto> showAllCategories() {
        List<Category> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(new ApiResponseDto(true, categories));
    }

    @PostMapping("/page-details")
    public ResponseEntity<ApiResponseDto> categoryPageDetails(
            @RequestBody CategoryPageRequestDto categoryPageRequestDto) {
        CategoryPageResponseDto response = categoryService
                .getCategoryPageDetails(categoryPageRequestDto.getCategoryId());
        return ResponseEntity.ok(new ApiResponseDto(true, response));
    }

}
