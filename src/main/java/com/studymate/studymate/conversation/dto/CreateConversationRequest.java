package com.studymate.studymate.conversation.dto;

import jakarta.validation.constraints.Size;

public record CreateConversationRequest(
        @Size(max = 200, message = "Title cannot exceed 200 characters")
        String title
) {
}
