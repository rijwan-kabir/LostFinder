package com.diu.lostfinder.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String fullName;

    private String studentId; // DIU Student ID
    private String department; // CSE, EEE, etc.
    private String phone;

    @Enumerated(EnumType.STRING)
    private Role role; // USER or ADMIN

    private LocalDateTime createdAt;

    public enum Role {
        USER, ADMIN
    }
}