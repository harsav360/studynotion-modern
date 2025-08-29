package com.studynotion_modern.backend.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.studynotion_modern.backend.entities.RatingAndReview;

public interface RatingAndReviewRepository extends MongoRepository<RatingAndReview, ObjectId> {
}
