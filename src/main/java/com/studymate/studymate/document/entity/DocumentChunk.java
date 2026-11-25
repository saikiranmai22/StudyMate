package com.studymate.studymate.document.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.resilience.annotation.ConcurrencyLimit;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "document_chunks",
        uniqueConstraints = {
        @UniqueConstraint(
                columnNames = {"document_id","chunk_index"} //This prevents duplicate chunk positions within the same PDF.
        )
        }
)
public class DocumentChunk {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "document_id", nullable = false)
    private Document document;

    @Column(name = "chunk_index", nullable = false)
    private Integer chunkIndex;

    @Column(name = "page_start", nullable = false)
    private Integer pageStart;

    @Column(name = "page_end", nullable = false)
    private Integer pageEnd;

    @Column(nullable = false, columnDefinition = "LONGTEXT")
    private String content;

    //This tells Hibernate to use JSON handling when reading and writing the embedding.
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(nullable = false, columnDefinition = "JSON")
    private List<Double> embedding;
    //An embedding is essentially a list of numerical values
    //We use a list of doubles to represent that vector in Java. Hibernate's JSON mapping serializes it into a JSON array in MySQL.

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    protected DocumentChunk() {
        // Required by JPA
    }

    public DocumentChunk(Document document, Integer chunkIndex, Integer pageStart, Integer pageEnd, String content,List<Double> embedding) {
        this.document = document;
        this.chunkIndex = chunkIndex;
        this.pageStart = pageStart;
        this.pageEnd = pageEnd;
        this.content = content;
        this.embedding = embedding;
    }

    public Long getId() {
        return id;
    }

    public Document getDocument() {
        return document;
    }

    public Integer getChunkIndex() {
        return chunkIndex;
    }

    public Integer getPageStart() {
        return pageStart;
    }

    public Integer getPageEnd() {
        return pageEnd;
    }

    public String getContent() {
        return content;
    }

    public List<Double> getEmbedding() {
        return embedding;
    }

    public void setEmbedding(List<Double> embedding) {
        this.embedding = embedding;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
