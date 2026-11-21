package com.studymate.studymate.auth.dto;

public record AuthResponse(
        String accessToken,
        String tokenType //The tokenType tells the frontend what kind of token it received and how to send it in subsequent requests.
) {
}
