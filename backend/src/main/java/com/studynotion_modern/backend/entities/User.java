package com.studynotion_modern.backend.entities;

import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "user")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    private ObjectId id;

    private String firstName;
    private String lastName;
    private String email;
    private String password;

    private String accountType; // Admin, Student, Instructor

    private Boolean active = true;
    private Boolean approved = true;

    @DBRef
    private Profile additionalDetails;

    @DBRef
    private List<Course> courses;

    private String token;
    private Date resetPasswordExpires;
    private String image;

    @DBRef
    private List<CourseProgress> courseProgress;

    @CreatedDate
    private Date createdAt;

    @LastModifiedDate
    private Date updatedAt;
}
