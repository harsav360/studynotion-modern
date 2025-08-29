package com.studynotion_modern.backend.entities;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "section")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Section {
    @Id
    private ObjectId id;

    private String sectionName;

    @DBRef
    private List<SubSection> subSection;
}
