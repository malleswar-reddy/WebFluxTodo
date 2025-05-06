package com.webflux.service;

import com.webflux.exception.dto.UserDTO;
import com.webflux.model.UserAuth;
import com.webflux.model.UserProfile;
import com.webflux.repository.UserAuthRepository;
import com.webflux.repository.UserProfileRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class UserService {

    private final UserAuthRepository userAuthRepository;
    private final UserProfileRepository userProfileRepository;

    public UserService(UserAuthRepository userAuthRepository, UserProfileRepository userProfileRepository) {
        this.userAuthRepository = userAuthRepository;
        this.userProfileRepository = userProfileRepository;
    }

    // Create
    public Mono<UserDTO> createUser(UserDTO userDTO) {
        UserAuth userAuth = new UserAuth();
        userAuth.setUserId(userDTO.getUserId());
        userAuth.setEmail(userDTO.getEmail());
        userAuth.setPasswordHash(userDTO.getPasswordHash());
        userAuth.setEmailVerified(userDTO.isEmailVerified());
        userAuth.setCreatedAt(userDTO.getCreatedAt());

        UserProfile userProfile = new UserProfile();
        userProfile.setUserId(userDTO.getUserId());
        userProfile.setFirstName(userDTO.getFirstName());
        userProfile.setLastName(userDTO.getLastName());
        userProfile.setRole(userDTO.getRole());
        userProfile.setPicture(userDTO.getPicture());
        userProfile.setPhone(userDTO.getPhone());
        userProfile.setAlternatePhone(userDTO.getAlternatePhone());
        userProfile.setExp(userDTO.getExp());

        return userAuthRepository.save(userAuth)
                .flatMap(savedAuth -> userProfileRepository.save(userProfile)
                        .thenReturn(userDTO));
    }

    // Read (Get by ID, unchanged for simplicity)
    public Mono<UserDTO> getUserById(Long id) {
        return userAuthRepository.findById(id)
                .flatMap(userAuth -> userProfileRepository.findById(id)
                        .map(userProfile -> toUserDTO(userAuth, userProfile))
                        .switchIfEmpty(Mono.just(toUserDTO(userAuth, null))));
    }

    // Read (Get all, updated to use custom query)
    public Flux<UserDTO> getAllUsers() {
        return userAuthRepository.findAllWithProfile();
    }

    // Update
    public Mono<UserDTO> updateUser(Long id, UserDTO userDTO) {
        return userAuthRepository.findById(id)
                .flatMap(existingAuth -> {
                    existingAuth.setEmail(userDTO.getEmail());
                    existingAuth.setPasswordHash(userDTO.getPasswordHash());
                    existingAuth.setEmailVerified(userDTO.isEmailVerified());
                    existingAuth.setCreatedAt(userDTO.getCreatedAt());

                    return userAuthRepository.save(existingAuth)
                            .then(userProfileRepository.findById(id))
                            .flatMap(existingProfile -> {
                                existingProfile.setFirstName(userDTO.getFirstName());
                                existingProfile.setLastName(userDTO.getLastName());
                                existingProfile.setRole(userDTO.getRole());
                                existingProfile.setPicture(userDTO.getPicture());
                                existingProfile.setPhone(userDTO.getPhone());
                                existingProfile.setAlternatePhone(userDTO.getAlternatePhone());
                                existingProfile.setExp(userDTO.getExp());

                                return userProfileRepository.save(existingProfile)
                                        .thenReturn(userDTO);
                            });
                });
    }

    // Delete
    public Mono<Void> deleteUser(Long id) {
        return userProfileRepository.deleteById(id)
                .then(userAuthRepository.deleteById(id));
    }

    // Helper method to convert to DTO
    private UserDTO toUserDTO(UserAuth userAuth, UserProfile userProfile) {
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