package com.webflux.service;

import com.webflux.exception.dto.UserDTO;
import com.webflux.model.UserAuth;
import com.webflux.model.UserProfile;
import com.webflux.repository.UserAuthRepository;
import com.webflux.repository.UserProfileRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static com.webflux.utils.UserUtils.getUserAuth;
import static com.webflux.utils.UserUtils.getUserProfile;
import static com.webflux.utils.UserUtils.toUserDTO;
import static com.webflux.utils.UserUtils.userAuthExtracted;
import static com.webflux.utils.UserUtils.userProfileExtracted;

@Service
@Slf4j
public class UserService {

    private final UserAuthRepository userAuthRepository;
    private final UserProfileRepository userProfileRepository;

    public UserService(UserAuthRepository userAuthRepository, UserProfileRepository userProfileRepository) {
        this.userAuthRepository = userAuthRepository;
        this.userProfileRepository = userProfileRepository;
    }

    // Create
    public Mono<UserDTO> createUser(UserDTO userDTO) {
        UserAuth userAuth = getUserAuth(userDTO);
        UserProfile userProfile = getUserProfile(userDTO);

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
                    userAuthExtracted(userDTO, existingAuth);

                    return userAuthRepository.save(existingAuth)
                            .then(userProfileRepository.findById(id))
                            .flatMap(existingProfile -> {
                                userProfileExtracted(userDTO, existingProfile);
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
}