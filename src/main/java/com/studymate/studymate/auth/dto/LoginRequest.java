package com.studymate.studymate.auth.dto;

import jakarta.persistence.Entity;
import jakarta.validation.constraints.*;

public record LoginRequest(
        @NotBlank
        @Email
        String email,
        @NotBlank
        String password
) {
}
