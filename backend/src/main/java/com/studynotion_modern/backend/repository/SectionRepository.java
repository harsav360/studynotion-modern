package com.studynotion_modern.backend.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.studynotion_modern.backend.entities.Section;

public interface SectionRepository extends MongoRepository<Section, String> {
}