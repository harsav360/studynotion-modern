package com.studynotion_modern.backend.entities;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "ratingAndReview")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RatingAndReview {
    @Id
    private ObjectId id;

    @DBRef
    private User user;

    private Double rating;
    private String review;

    @DBRef
    private Course course;
}
