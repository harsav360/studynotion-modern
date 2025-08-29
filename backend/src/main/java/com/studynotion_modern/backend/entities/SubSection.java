package com.studynotion_modern.backend.entities;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "subSection")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubSection {
    @Id
    private ObjectId id;

    private String title;
    private String timeDuration;
    private String description;
    private String videoUrl;
}
