package com.studynotion_modern.backend.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.studynotion_modern.backend.entities.Course;

public interface CourseRepository extends MongoRepository<Course, String> {
}
