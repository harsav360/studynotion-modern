package com.studynotion_modern.backend.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.studynotion_modern.backend.entities.Category;

public interface CategoryRepository extends MongoRepository<Category, ObjectId> {
    List<Category> findByIdNot(ObjectId id);
}
