package com.studynotion_modern.backend.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.studynotion_modern.backend.entities.CourseProgress;

public interface CourseProgressRepository extends MongoRepository<CourseProgress, String> {
}
