package com.studymate.studymate.document.dto;

import com.studymate.studymate.document.entity.DocumentStatus;

import java.time.LocalDateTime;

public record DocumentResponse(
        Long id,
        String originalFilename,
        Long fileSizeBytes,
        Integer pageCount,
        DocumentStatus status,
        LocalDateTime createdAt
) {
}
//Our Document entity contains internal information such as the storage key.
// The frontend doesn't need to know where the PDF is stored on our server.