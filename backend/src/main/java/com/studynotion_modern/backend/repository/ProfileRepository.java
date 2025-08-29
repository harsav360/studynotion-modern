package com.studynotion_modern.backend.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.studynotion_modern.backend.entities.Profile;

public interface ProfileRepository extends MongoRepository<Profile, ObjectId> {
}
