package com.webflux.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table("user_auth")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserAuth {

    @Id
    @Column("user_id")
    private Long userId;

    @Column("email")
    private String email;

    @Column("password_hash")
    private String passwordHash;

    @Column("email_verified")
    private boolean emailVerified = false;

    @Column("created_at")
    private LocalDateTime createdAt;
}