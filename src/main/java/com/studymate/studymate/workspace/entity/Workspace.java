package com.studymate.studymate.workspace.entity;

import com.studymate.studymate.user.entity.User;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
        name="workspaces",
        uniqueConstraints = {
                @UniqueConstraint(columnNames ={"user_id", "name"} ) //table level - for a combination of columns to be unique
        }
)
public class Workspace {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    //FetchType.LAZY: JPA doesn't automatically load the complete user whenever you retrieve a workspace. It loads the related user when needed.
    //optional = false: A workspace must have an owner.
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    //unidirectional
    //For example, suppose you return a User object through a REST API.
    //- JSON serializer finds the user's workspaces.
    //- It finds the user inside each workspace.
    //- It finds that user's workspaces again.
    //- This can repeat indefinitely.

    @Column(nullable = false, length = 150)
    private String name;

    @Column(length = 500)
    private String description;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    protected Workspace(){

    }

    public Workspace(User user, String name, String description) {
        this.user = user;
        this.name = name;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
