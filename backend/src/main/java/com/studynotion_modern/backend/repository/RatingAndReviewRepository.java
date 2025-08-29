package com.studynotion_modern.backend.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.studynotion_modern.backend.entities.RatingAndReview;
import org.springframework.stereotype.Repository;

@Repository
public interface RatingAndReviewRepository extends MongoRepository<RatingAndReview, ObjectId> {
}
