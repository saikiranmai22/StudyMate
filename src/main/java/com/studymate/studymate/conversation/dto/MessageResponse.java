package com.studymate.studymate.conversation.dto;

import com.studymate.studymate.conversation.entity.MessageRole;
import tools.jackson.databind.JsonNode;

import java.time.LocalDateTime;

public record MessageResponse(
        Long id,

        MessageRole role,

        String content,

        JsonNode sources,

        LocalDateTime createdAt
) {
}
