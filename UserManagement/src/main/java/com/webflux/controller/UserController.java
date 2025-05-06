package com.webflux.controller;

import com.webflux.exception.dto.UserDTO;
import com.webflux.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Create
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<UserDTO> createUser(@RequestBody UserDTO userDTO) {
        return userService.createUser(userDTO);
    }

    // Read (Get by ID)
    @GetMapping("/{id}")
    public Mono<UserDTO> getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    // Read (Get all)
    @GetMapping
    public Flux<UserDTO> getAllUsers() {
        return userService.getAllUsers();
    }

    // Update
    @PutMapping("/{id}")
    public Mono<UserDTO> updateUser(@PathVariable Long id,@RequestBody UserDTO userDTO) {
        return userService.updateUser(id, userDTO);
    }

    // Delete
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteUser(@PathVariable Long id) {
        return userService.deleteUser(id);
    }
}
