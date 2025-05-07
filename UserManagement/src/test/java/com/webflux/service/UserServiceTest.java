package com.webflux.service;

import com.webflux.exception.dto.UserDTO;
import com.webflux.model.UserAuth;
import com.webflux.model.UserProfile;
import com.webflux.repository.UserAuthRepository;
import com.webflux.repository.UserProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserAuthRepository userAuthRepository;

    @Mock
    private UserProfileRepository userProfileRepository;

    @InjectMocks
    private UserService userService;

    private UserDTO userDTO;
    private UserAuth userAuth;
    private UserProfile userProfile;
    private UserDTO userDTO2;

    @BeforeEach
    void setUp() {
        // Initialize test data
        userDTO = new UserDTO();
        userDTO.setUserId(1L);
        userDTO.setEmail("user@example.com");
        userDTO.setPasswordHash("hashedPassword");
        userDTO.setEmailVerified(false);
        userDTO.setCreatedAt(LocalDateTime.now());
        userDTO.setFirstName("John");
        userDTO.setLastName("Doe");
        userDTO.setRole("customer");
        userDTO.setPicture("http://example.com/picture.jpg");
        userDTO.setPhone(1234567890L);
        userDTO.setAlternatePhone(9876543210L);
        userDTO.setExp("some_exp");

        userAuth = new UserAuth();
        userAuth.setUserId(1L);
        userAuth.setEmail("user@example.com");
        userAuth.setPasswordHash("hashedPassword");
        userAuth.setEmailVerified(false);
        userAuth.setCreatedAt(LocalDateTime.now());

        userProfile = new UserProfile();
        userProfile.setUserId(1L);
        userProfile.setFirstName("John");
        userProfile.setLastName("Doe");
        userProfile.setRole("customer");
        userProfile.setPicture("http://example.com/picture.jpg");
        userProfile.setPhone(1234567890L);
        userProfile.setAlternatePhone(9876543210L);
        userProfile.setExp("some_exp");

        userDTO2 = new UserDTO();
        userDTO2.setUserId(2L);
        userDTO2.setEmail("user2@example.com");
        userDTO2.setPasswordHash("hash2");
        userDTO2.setEmailVerified(true);
        userDTO2.setCreatedAt(LocalDateTime.now());
        userDTO2.setFirstName("Jane");
        userDTO2.setLastName("Doe");
        userDTO2.setRole("admin");
        userDTO2.setPicture(null);
        userDTO2.setPhone(null);
        userDTO2.setAlternatePhone(null);
        userDTO2.setExp(null);
    }

    @Test
    void createUser_success() {
        when(userAuthRepository.save(any(UserAuth.class))).thenReturn(Mono.just(userAuth));
        when(userProfileRepository.save(any(UserProfile.class))).thenReturn(Mono.just(userProfile));

        StepVerifier.create(userService.createUser(userDTO))
                .expectNext(userDTO)
                .verifyComplete();

        verify(userAuthRepository).save(any(UserAuth.class));
        verify(userProfileRepository).save(any(UserProfile.class));
    }

    @Test
    void getUserById_success_withProfile() {
        when(userAuthRepository.findById(1L)).thenReturn(Mono.just(userAuth));
        when(userProfileRepository.findById(1L)).thenReturn(Mono.just(userProfile));

        StepVerifier.create(userService.getUserById(1L))
                .expectNextMatches(dto ->
                        dto.getUserId().equals(1L) &&
                                dto.getEmail().equals("user@example.com") &&
                                dto.getFirstName().equals("John") &&
                                dto.getLastName().equals("Doe"))
                .verifyComplete();

        verify(userAuthRepository).findById(1L);
        verify(userProfileRepository).findById(1L);
    }

    @Test
    void getUserById_success_withoutProfile() {
        when(userAuthRepository.findById(1L)).thenReturn(Mono.just(userAuth));
        when(userProfileRepository.findById(1L)).thenReturn(Mono.empty());

        StepVerifier.create(userService.getUserById(1L))
                .expectNextMatches(dto ->
                        dto.getUserId().equals(1L) &&
                                dto.getEmail().equals("user@example.com") &&
                                dto.getFirstName() == null &&
                                dto.getLastName() == null)
                .verifyComplete();

        verify(userAuthRepository).findById(1L);
        verify(userProfileRepository).findById(1L);
    }

    @Test
    void getUserById_notFound() {
        when(userAuthRepository.findById(1L)).thenReturn(Mono.empty());

        StepVerifier.create(userService.getUserById(1L))
                .expectNextCount(0)
                .verifyComplete();

        verify(userAuthRepository).findById(1L);
        verify(userProfileRepository, never()).findById(anyLong());
    }

    @Test
    void getAllUsers_success() {
        when(userAuthRepository.findAllWithProfile()).thenReturn(Flux.just(userDTO, userDTO2));

        Flux<UserDTO> result = userService.getAllUsers();
        assertNotNull(result, "getAllUsers should not return null");

        StepVerifier.create(result)
                .expectNextMatches(dto -> dto.getUserId().equals(1L))
                .expectNextMatches(dto -> dto.getUserId().equals(2L))
                .verifyComplete();

        verify(userAuthRepository).findAllWithProfile();
        verify(userProfileRepository, never()).findById(anyLong());
    }

    @Test
    void getAllUsers_withMissingProfile() {
        UserDTO userDTOWithoutProfile = new UserDTO();
        userDTOWithoutProfile.setUserId(1L);
        userDTOWithoutProfile.setEmail("user@example.com");
        userDTOWithoutProfile.setPasswordHash("hashedPassword");
        userDTOWithoutProfile.setEmailVerified(false);
        userDTOWithoutProfile.setCreatedAt(LocalDateTime.now());

        when(userAuthRepository.findAllWithProfile()).thenReturn(Flux.just(userDTOWithoutProfile));

        Flux<UserDTO> result = userService.getAllUsers();
        assertNotNull(result, "getAllUsers should not return null");

        StepVerifier.create(result)
                .expectNextMatches(dto ->
                        dto.getUserId().equals(1L) &&
                                dto.getFirstName() == null)
                .verifyComplete();

        verify(userAuthRepository).findAllWithProfile();
        verify(userProfileRepository, never()).findById(anyLong());
    }

    @Test
    void getAllUsers_empty() {
        when(userAuthRepository.findAllWithProfile()).thenReturn(Flux.empty());

        Flux<UserDTO> result = userService.getAllUsers();
        assertNotNull(result, "getAllUsers should not return null");

        StepVerifier.create(result)
                .expectNextCount(0)
                .verifyComplete();

        verify(userAuthRepository).findAllWithProfile();
        verify(userProfileRepository, never()).findById(anyLong());
    }

    @Test
    void updateUser_success() {
        when(userAuthRepository.findById(1L)).thenReturn(Mono.just(userAuth));
        when(userAuthRepository.save(any(UserAuth.class))).thenReturn(Mono.just(userAuth));
        when(userProfileRepository.findById(1L)).thenReturn(Mono.just(userProfile));
        when(userProfileRepository.save(any(UserProfile.class))).thenReturn(Mono.just(userProfile));

        UserDTO updatedDTO = new UserDTO();
        updatedDTO.setUserId(1L);
        updatedDTO.setEmail("updated@example.com");
        updatedDTO.setPasswordHash("newHash");
        updatedDTO.setEmailVerified(true);
        updatedDTO.setCreatedAt(LocalDateTime.now());
        updatedDTO.setFirstName("Jane");
        updatedDTO.setLastName("Smith");
        updatedDTO.setRole("admin");

        StepVerifier.create(userService.updateUser(1L, updatedDTO))
                .expectNext(updatedDTO)
                .verifyComplete();

        verify(userAuthRepository).findById(1L);
        verify(userAuthRepository).save(any(UserAuth.class));
        verify(userProfileRepository).findById(1L);
        verify(userProfileRepository).save(any(UserProfile.class));
    }

    @Test
    void updateUser_notFound() {
        when(userAuthRepository.findById(1L)).thenReturn(Mono.empty());

        StepVerifier.create(userService.updateUser(1L, userDTO))
                .expectNextCount(0)
                .verifyComplete();

        verify(userAuthRepository).findById(1L);
        verify(userAuthRepository, never()).save(any());
        verify(userProfileRepository, never()).findById(anyLong());
    }

    @Test
    void deleteUser_success() {
        when(userProfileRepository.deleteById(1L)).thenReturn(Mono.empty());
        when(userAuthRepository.deleteById(1L)).thenReturn(Mono.empty());

        StepVerifier.create(userService.deleteUser(1L))
                .verifyComplete();

        verify(userProfileRepository).deleteById(1L);
        verify(userAuthRepository).deleteById(1L);
    }

    @Test
    void verifyServiceInitialization() {
        assertNotNull(userService, "UserService should be initialized");
        assertNotNull(userAuthRepository, "UserAuthRepository should be mocked");
        assertNotNull(userProfileRepository, "UserProfileRepository should be mocked");
    }
}