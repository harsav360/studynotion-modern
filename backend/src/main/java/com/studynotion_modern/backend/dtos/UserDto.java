package com.studynotion_modern.backend.dtos;

import lombok.Data;


@Data
public class UserDto {
    private String firstName;
    private String lastName;
    private String dateOfBirth;
    private String about;
    private Long contactNumber;
    private String gender;
}
