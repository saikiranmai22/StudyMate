package com.studymate.studymate.conversation.entity;

import com.studymate.studymate.workspace.entity.Workspace;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name="conversations")
public class Conversation {
    @Id@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name ="workspace_id", nullable = false )
    private Workspace workspace;

    @Column(nullable = false, length = 200)
    private String title="New conversation";

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    protected Conversation() {
        // Required by JPA
    }

    public Conversation(Workspace workspace, String title) {
        this.workspace = workspace;
        this.title = (title==null || title.isBlank()) ? "New conversation":title;
    }
    //Once the user sends their first message, we can later implement automatic title generation to replace "New conversation" with something meaningful.

    public Long getId() {
        return id;
    }

    public Workspace getWorkspace() {
        return workspace;
    }

    public String getTitle() {
        return title;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void updateTitle(String title){
        if(title!=null && !title.isBlank()){
            this.title=title;
        }
    }
}
