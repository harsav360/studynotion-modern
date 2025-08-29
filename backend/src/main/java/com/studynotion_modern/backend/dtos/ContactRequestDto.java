package com.studynotion_modern.backend.dtos;

import lombok.Data;

@Data
public class ContactRequestDto {
    private String email;
    private String firstname;
    private String lastname;
    private String message;
    private String phoneNo;
    private String countrycode;

}
