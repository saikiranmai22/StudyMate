package com.studymate.studymate.document.repository;

import com.studymate.studymate.document.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DocumentRepository extends JpaRepository<Document, Long> {
    List<Document> findAllByWorkspace_IdOrderByCreatedAtDesc(Long workspaceId);
    //retrieves all documents belonging to a workspace, newest first.

    Optional<Document> findByIdAndWorkspace_Id(Long documentId, Long workspaceId);
    //retrieves a document only when it belongs to the specified workspace.
    //Later, the service layer must additionally verify that the workspace belongs to the authenticated user.
}
