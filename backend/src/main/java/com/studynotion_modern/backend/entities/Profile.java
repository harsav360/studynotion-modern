package com.studynotion_modern.backend.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "profile")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Profile {
    @Id
    private String id;

    private String gender;
    private String dateOfBirth;
    private String about;
    private Long contactNumber;
}
