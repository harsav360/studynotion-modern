package com.studynotion_modern.backend.entities;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "courseProgress")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseProgress {
    @Id
    private String id;

    @DBRef
    private Course courseID;

    @DBRef
    private User userId;

    @DBRef
    private List<SubSection> completedVideos;
}
