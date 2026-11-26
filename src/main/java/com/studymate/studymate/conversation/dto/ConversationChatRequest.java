package com.studymate.studymate.conversation.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ConversationChatRequest(
        @NotNull(message = "Document ID is required")
        Long documentId,

        @NotBlank(message = "Question cannot be empty")
        String question,

        @Min(1)
        @Max(20)
        Integer topK
) {
}
