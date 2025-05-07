package com.webflux.utils;

import com.webflux.exception.dto.UserDTO;
import com.webflux.model.UserAuth;
import com.webflux.model.UserProfile;

public class UserUtils {

    public static UserProfile getUserProfile(UserDTO userDTO) {
        UserProfile userProfile = new UserProfile();
        userProfile.setUserId(userDTO.getUserId());
        userProfile.setFirstName(userDTO.getFirstName());
        userProfile.setLastName(userDTO.getLastName());
        userProfile.setRole(userDTO.getRole());
        userProfile.setPicture(userDTO.getPicture());
        userProfile.setPhone(userDTO.getPhone());
        userProfile.setAlternatePhone(userDTO.getAlternatePhone());
        userProfile.setExp(userDTO.getExp());
        return userProfile;
    }

    public static UserAuth getUserAuth(UserDTO userDTO) {
        UserAuth userAuth = new UserAuth();
        userAuth.setUserId(userDTO.getUserId());
        userAuth.setEmail(userDTO.getEmail());
        userAuth.setPasswordHash(userDTO.getPasswordHash());
        userAuth.setEmailVerified(userDTO.isEmailVerified());
        userAuth.setCreatedAt(userDTO.getCreatedAt());
        return userAuth;
    }

    public static void userAuthExtracted(UserDTO userDTO, UserAuth existingAuth) {
        existingAuth.setEmail(userDTO.getEmail());
        existingAuth.setPasswordHash(userDTO.getPasswordHash());
        existingAuth.setEmailVerified(userDTO.isEmailVerified());
        existingAuth.setCreatedAt(userDTO.getCreatedAt());
    }


    public static void userProfileExtracted(UserDTO userDTO, UserProfile existingProfile) {
        existingProfile.setFirstName(userDTO.getFirstName());
        existingProfile.setLastName(userDTO.getLastName());
        existingProfile.setRole(userDTO.getRole());
        existingProfile.setPicture(userDTO.getPicture());
        existingProfile.setPhone(userDTO.getPhone());
        existingProfile.setAlternatePhone(userDTO.getAlternatePhone());
        existingProfile.setExp(userDTO.getExp());
    }

    // Helper method to convert to DTO
    public static UserDTO toUserDTO(UserAuth userAuth, UserProfile userProfile) {
        UserDTO userDTO = new UserDTO();
        userDTO.setUserId(userAuth.getUserId());
        userDTO.setEmail(userAuth.getEmail());
        userDTO.setPasswordHash(userAuth.getPasswordHash());
        userDTO.setEmailVerified(userAuth.isEmailVerified());
        userDTO.setCreatedAt(userAuth.getCreatedAt());

        if (userProfile != null) {
            userDTO.setFirstName(userProfile.getFirstName());
            userDTO.setLastName(userProfile.getLastName());
            userDTO.setRole(userProfile.getRole());
            userDTO.setPicture(userProfile.getPicture());
            userDTO.setPhone(userProfile.getPhone());
            userDTO.setAlternatePhone(userProfile.getAlternatePhone());
            userDTO.setExp(userProfile.getExp());
        }

        return userDTO;
    }
}
