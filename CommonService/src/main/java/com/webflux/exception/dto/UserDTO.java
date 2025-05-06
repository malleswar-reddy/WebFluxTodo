package com.webflux.exception.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserDTO {
    private Long userId;
    private String email;
    private String passwordHash;
    private boolean emailVerified;
    private LocalDateTime createdAt;
    private String firstName;
    private String lastName;
    private String role;
    private String picture;
    private Long phone;
    private Long alternatePhone;
    private String exp;
}
