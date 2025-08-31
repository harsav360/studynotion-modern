package com.studynotion_modern.backend.service;

import java.util.*;
import java.util.stream.Collectors;

import com.studynotion_modern.backend.dtos.CategoryRequestDto;
import com.studynotion_modern.backend.repository.CourseRepository;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import com.studynotion_modern.backend.dtos.CategoryPageResponseDto;
import com.studynotion_modern.backend.entities.Category;
import com.studynotion_modern.backend.entities.Course;
import com.studynotion_modern.backend.repository.CategoryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryService {

        private final CategoryRepository categoryRepository;
        private final CourseRepository courseRepository;

        public void createCategory(CategoryRequestDto categoryRequest) {
                Category category = new Category();
                category.setName(categoryRequest.getName());
                category.setDescription(categoryRequest.getDescription());
                List<Course> courses = new ArrayList<>();
                if (!categoryRequest.getCourseIds().isEmpty()) {
                        // Get the corresponding course for each course id
                        courses = categoryRequest.getCourseIds().stream()
                                .map(courseId -> courseRepository.findById(new ObjectId(courseId))
                                        .orElseThrow(() -> new NoSuchElementException("Course not found with ID: " + courseId)))
                                .collect(Collectors.toList());
                }
                category.setCourses(courses);
                categoryRepository.save(category);
        }

        public List<Category> getAllCategories() {
                return categoryRepository.findAll();
        }

        public CategoryPageResponseDto getCategoryPageDetails(String categoryId) {

                Category selectedCategory = categoryRepository.findById(new ObjectId(categoryId))
                                .orElseThrow(() -> new NoSuchElementException("Category not found"));

                List<Course> publishedCourses = selectedCategory.getCourses().stream()
                                .filter(course -> "Published".equals(course.getStatus()))
                                .collect(Collectors.toList());

                if (publishedCourses.isEmpty()) {
                        throw new NoSuchElementException("No courses found for the selected category.");
                }

                // Get a different random category
                List<Category> otherCategories = categoryRepository.findByIdNot(new ObjectId(categoryId));
                Category differentCategory = null;
                if (!otherCategories.isEmpty()) {
                        Random rand = new Random();
                        differentCategory = otherCategories.get(rand.nextInt(otherCategories.size()));
                }

                // Get top-selling courses across all categories
                List<Category> allCategories = categoryRepository.findAll();
                List<Course> allCourses = allCategories.stream()
                                .flatMap(cat -> cat.getCourses().stream())
                                .filter(course -> "Published".equals(course.getStatus()))
                                .collect(Collectors.toList());

                List<Course> mostSellingCourses = allCourses.stream()
                                .filter((Course course) -> "Published".equalsIgnoreCase(course.getStatus()))
                                .sorted(Comparator.comparingInt(
                                                (Course course) -> course.getStudentsEnrolled() != null
                                                                ? course.getStudentsEnrolled().size()
                                                                : 0)
                                                .reversed())
                                .limit(10)
                                .collect(Collectors.toList());

                CategoryPageResponseDto response = new CategoryPageResponseDto();
                response.setSelectedCategory(selectedCategory);
                response.setDifferentCategory(differentCategory);
                response.setMostSellingCourses(mostSellingCourses);
                return response;
        }

}
