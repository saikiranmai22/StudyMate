package com.studymate.studymate.conversation.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import tools.jackson.databind.JsonNode;


import java.time.LocalDateTime;

@Entity
@Table(name = "messages")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "conversation_id", nullable = false)
    private Conversation conversation;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MessageRole role;

    @Column(nullable = false, columnDefinition = "LONGTEXT")
    private String content;

//    @JdbcTypeCode(SqlTypes.JSON)
//    @Column(columnDefinition = "JSON")
//    private JsonNode sources;

    @Column(columnDefinition = "LONGTEXT")
    private String sources;

    //For ordinary user messages, sources can be null. the agents message will have a source
    //We are storing source metadata, not the PDF text itself. The actual extracted text is stored in document_chunks

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    protected Message() {
        // Required by JPA
    }

    public Message(Conversation conversation, MessageRole role, String content, String sources) {
        this.conversation = conversation;
        this.role = role;
        this.content = content;
        this.sources = sources;
    }

    public Long getId() {
        return id;
    }

    public Conversation getConversation() {
        return conversation;
    }

    public MessageRole getRole() {
        return role;
    }

    public String getContent() {
        return content;
    }

    public String getSources() {
        return sources;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
