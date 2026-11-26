package com.studymate.studymate.document.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record ChatRequest(
        @NotBlank(message = "Question cannot be empty")
        String question,
        @Min(value=1, message="topK must be at least 1")
        @Max(value = 20, message = "topK cannot exceed 20")
        Integer topk
) {
}
//Java records are convenient for DTOs. Java automatically provides a constructor, getters (accessors), equals(), and hashCode().