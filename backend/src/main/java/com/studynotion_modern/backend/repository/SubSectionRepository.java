package com.studynotion_modern.backend.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.studynotion_modern.backend.entities.SubSection;

public interface SubSectionRepository extends MongoRepository<SubSection, String> {
}