package com.studymate.studymate.note.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateNoteRequest(
        @NotBlank
        @Size(max=200)
        String title,

        @NotBlank
        String content
) {
}
