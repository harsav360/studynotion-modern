package com.studynotion_modern.backend.entities;

import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "course")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Course {
    @Id
    private String id;

    private String courseName;
    private String courseDescription;

    @DBRef
    private User instructor;

    private String whatYouWillLearn;

    @DBRef
    private List<Section> courseContent;

    @DBRef
    private List<RatingAndReview> ratingAndReviews;

    private Double price;
    private String thumbnail;

    private List<String> tag;

    @DBRef
    private Category category;

    @DBRef
    private List<User> studentsEnrolled;

    private List<String> instructions;

    private String status; // Draft or Published

    private Date createdAt = new Date();
}
