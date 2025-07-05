package com.studynotion_modern.backend.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.studynotion_modern.backend.entities.Category;

public interface CategoryRepository extends MongoRepository<Category, String> {
}
