package com.studymate.studymate.note.dto;

import java.time.LocalDateTime;

public record NoteResponse(
        Long id,
        Long workspaceId,
        Long sourceMessageId,
        String title,
        String content,
        LocalDateTime createdAt,
        LocalDateTime updateAt
) {
}
