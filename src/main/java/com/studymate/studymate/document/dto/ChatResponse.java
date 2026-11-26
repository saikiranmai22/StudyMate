package com.studymate.studymate.document.dto;

import java.util.List;

public record ChatResponse(
        String answer,
        List<ChatSourceResponse> sources
) {
}
