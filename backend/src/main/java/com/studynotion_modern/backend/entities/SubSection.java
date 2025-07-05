package com.studynotion_modern.backend.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "subSection")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubSection {
    @Id
    private String id;

    private String title;
    private String timeDuration;
    private String description;
    private String videoUrl;
}
