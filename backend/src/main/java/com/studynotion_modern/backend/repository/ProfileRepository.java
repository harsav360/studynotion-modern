package com.studynotion_modern.backend.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.studynotion_modern.backend.entities.Profile;

public interface ProfileRepository extends MongoRepository<Profile, String> {
}
