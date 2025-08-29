package com.studynotion_modern.backend.service;

import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.stream.Collectors;

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

        public void createCategory(Category category) {
                categoryRepository.save(category);
        }

        public List<Category> getAllCategories() {
                return categoryRepository.findAll();
        }

        public CategoryPageResponseDto getCategoryPageDetails(Long categoryId) {

                /*
                 * Future Steps - Create a categorydto and then build it using the below
                 * category,and
                 * Right now I am using Category field in CategoryPageResponseDto, also update
                 * it
                 */
                Category selectedCategory = categoryRepository.findById(categoryId)
                                .orElseThrow(() -> new NoSuchElementException("Category not found"));

                List<Course> publishedCourses = selectedCategory.getCourses().stream()
                                .filter(course -> "Published".equals(course.getStatus()))
                                .collect(Collectors.toList());

                if (publishedCourses.isEmpty()) {
                        throw new NoSuchElementException("No courses found for the selected category.");
                }

                // Get a different random category
                List<Category> otherCategories = categoryRepository.findByIdNot(categoryId);
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
