package com.studymate.studymate.note.entity;

import com.studymate.studymate.conversation.entity.Message;
import com.studymate.studymate.workspace.entity.Workspace;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "notes")
public class Note {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "workspace_id", nullable = false)
    private Workspace workspace;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, columnDefinition = "LONGTEXT")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY) //Optional message relationship
    @JoinColumn(name = "source_message_id")
    private Message sourceMessage;
//A student manually creates a note. There is no original chatbot message, so sourceMessage is null.
//A student saves an AI response. We associate the note with the original Message.

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    protected Note() {
        // Required by JPA
    }

    public void update(String title, String content) {
        this.title = title;
        this.content = content;
    }

}
