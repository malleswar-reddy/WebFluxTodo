package com.webflux.controller;

import com.webflux.exception.dto.UserDTO;
import com.webflux.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private WebTestClient webTestClient;
    private UserDTO userDTO;
    private UserDTO userDTO2;

    @BeforeEach
    void setUp() {
        // Initialize WebTestClient
        webTestClient = WebTestClient.bindToController(userController).build();

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
        when(userService.createUser(any(UserDTO.class))).thenReturn(Mono.just(userDTO));

        webTestClient.post()
                .uri("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(userDTO)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(UserDTO.class)
                .isEqualTo(userDTO);

        verify(userService).createUser(any(UserDTO.class));
    }

    @Test
    void getUserById_success() {
        when(userService.getUserById(1L)).thenReturn(Mono.just(userDTO));

        webTestClient.get()
                .uri("/api/users/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody(UserDTO.class)
                .isEqualTo(userDTO);

        verify(userService).getUserById(1L);
    }

    @Test
    void getUserById_notFound() {
        when(userService.getUserById(1L)).thenReturn(Mono.empty());

        webTestClient.get()
                .uri("/api/users/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody().isEmpty();

        verify(userService).getUserById(1L);
    }

    @Test
    void getAllUsers_success() {
        when(userService.getAllUsers()).thenReturn(Flux.just(userDTO, userDTO2));

        webTestClient.get()
                .uri("/api/users")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(UserDTO.class)
                .hasSize(2)
                .contains(userDTO, userDTO2);

        verify(userService).getAllUsers();
    }

    @Test
    void getAllUsers_empty() {
        when(userService.getAllUsers()).thenReturn(Flux.empty());

        webTestClient.get()
                .uri("/api/users")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(UserDTO.class)
                .hasSize(0);

        verify(userService).getAllUsers();
    }

    @Test
    void updateUser_success() {
        when(userService.updateUser(eq(1L), any(UserDTO.class))).thenReturn(Mono.just(userDTO));

        webTestClient.put()
                .uri("/api/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(userDTO)
                .exchange()
                .expectStatus().isOk()
                .expectBody(UserDTO.class)
                .isEqualTo(userDTO);

        verify(userService).updateUser(eq(1L), any(UserDTO.class));
    }

    @Test
    void updateUser_notFound() {
        when(userService.updateUser(eq(1L), any(UserDTO.class))).thenReturn(Mono.empty());

        webTestClient.put()
                .uri("/api/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(userDTO)
                .exchange()
                .expectStatus().isOk()
                .expectBody().isEmpty();

        verify(userService).updateUser(eq(1L), any(UserDTO.class));
    }

    @Test
    void deleteUser_success() {
        when(userService.deleteUser(1L)).thenReturn(Mono.empty());

        webTestClient.delete()
                .uri("/api/users/1")
                .exchange()
                .expectStatus().isNoContent()
                .expectBody().isEmpty();

        verify(userService).deleteUser(1L);
    }
}