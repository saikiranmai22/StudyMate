package com.studymate.studymate.document.entity;

import com.studymate.studymate.workspace.entity.Workspace;
import jakarta.persistence.*;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "documents")
public class Document {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "workspace_id", nullable = false)
    private Workspace workspace;

    @Column(name="original_filename", nullable = false, length = 255)
    private String originalFilename;

    @Column(name = "storage_key", nullable = false, unique = true, length = 500)
    private String storageKey; //It identifies the PDF in object storage.
    //Remember our storage decision: the original PDF belongs in persistent object storage, not directly in MySQL. MySQL stores its metadata and the storage key.

    @Column(name = "file_size_bytes", nullable = false)
    private Long fileSizeBytes;

    @Column(name = "page_count")
    private Integer pageCount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private DocumentStatus status = DocumentStatus.PROCESSING;

    @Column(name = "embedding_model", length = 100)
    private String embeddingModel;

    @Column(name = "embedding_dimensions")
    private Integer embeddingDimensions;

    @Column(name = "failure_reason", length = 1000)
    private String failureReason;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    protected Document() {
        // Required by JPA
    }

    public Document(Workspace workspace, String originalFilename, String storageKey, Long fileSizeBytes) {
        this.workspace = workspace;
        this.originalFilename = originalFilename;
        this.storageKey = storageKey;
        this.fileSizeBytes = fileSizeBytes;
    }

    //These methods make status transitions explicit.
    //so page count, embedding model , embedding dimensions and failure reason can be null
    public void markCompleted(int pageCount, String embeddingModel, int embeddingDimensions){
        this.pageCount=pageCount;
        this.embeddingModel=embeddingModel;
        this.embeddingDimensions=embeddingDimensions;
        this.failureReason=null;
        this.status=DocumentStatus.COMPLETED;
    }

    public void markFailed(String failureReason){
        this.failureReason=failureReason;
        this.status= DocumentStatus.FAILED;
    }

    public Long getId() {
        return id;
    }

    public Workspace getWorkspace() {
        return workspace;
    }

    public String getOriginalFilename() {
        return originalFilename;
    }

    public String getStorageKey() {
        return storageKey;
    }

    public Long getFileSizeBytes() {
        return fileSizeBytes;
    }

    public Integer getPageCount() {
        return pageCount;
    }

    public DocumentStatus getStatus() {
        return status;
    }

    public String getEmbeddingModel() {
        return embeddingModel;
    }

    public Integer getEmbeddingDimensions() {
        return embeddingDimensions;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
