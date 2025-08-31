package com.studynotion_modern.backend.service;

import org.springframework.stereotype.Service;
import com.studynotion_modern.backend.dtos.SignupRequestDto;
import com.studynotion_modern.backend.entities.Profile;
import com.studynotion_modern.backend.entities.User;
import com.studynotion_modern.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    // Create User during initial sign-up request
    public User createUser(SignupRequestDto signupRequestDto, String hashedPassword, boolean approved) {
        Profile profile = new Profile();

        User user = new User();
        user.setFirstName(signupRequestDto.getFirstName());
        user.setLastName(signupRequestDto.getLastName());
        user.setEmail(signupRequestDto.getEmail());
        user.setAdditionalDetails(profile);
        user.setPassword(hashedPassword);
        user.setAccountType(signupRequestDto.getAccountType());
        user.setApproved(approved);
        user.setImage("");

        return userRepository.save(user);
    }
}