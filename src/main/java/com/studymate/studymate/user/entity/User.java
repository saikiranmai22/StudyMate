package com.studymate.studymate.user.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name="users",
uniqueConstraints = {
        @UniqueConstraint(columnNames = "username"),
        @UniqueConstraint(columnNames = "email")
        //@Column(unique = true) → individual column uniqueness.
})
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length=50)
    private String username;

    @Column(nullable = false, length=255)
    private String email;

    @Column(name="password_hash", nullable = false, length = 255)
    private String passwordHash;
    //Java commonly uses camelCase, whereas SQL databases commonly use snake_case
    //@Column(name = "...") explicitly tells JPA which database column corresponds to the Java field.

    @Column(length=20)
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false,length = 20)
    private UserRole role= UserRole.STUDENT;

    @CreationTimestamp
    @Column(name="created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name="updated_at", nullable = false)
    private LocalDateTime updatedAt;

    protected User(){ //is the no-argument constructor JPA requires to instantiate entities.
    //Same package and subclasses
    }

    public User(String username, String email, String passwordHash, String phone, UserRole role) {
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.phone = phone;
        this.role = role;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getPhone() {
        return phone;
    }

    public UserRole getRole() {
        return role;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
