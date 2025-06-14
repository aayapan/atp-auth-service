package com.antrip.auth_service.models;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String displayName;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false, name = "bcrypt_password")
    private String password;

    @Column(nullable = false)
    private LocalDateTime dateCreated;

    @Column(nullable = false)
    private LocalDateTime dateUpdated;
}
