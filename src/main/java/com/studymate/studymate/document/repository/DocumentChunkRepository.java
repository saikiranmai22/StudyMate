package com.studymate.studymate.document.repository;

import com.studymate.studymate.document.entity.DocumentChunk;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DocumentChunkRepository extends JpaRepository<DocumentChunk, Long> {

    List<DocumentChunk> findAllByDocument_IdOrderByChunkIndexAsc(Long documentId);
    //retrieves all chunks of a document in their original order.

    long countByDocument_Id(Long documentId);
    //tells us how many chunks were generated for a document.
}
