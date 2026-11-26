package com.studymate.studymate.document.dto;

public record RetrievedChunkResponse(
        Long chunkId,
        String content,
        Integer pageStart,
        Integer pageEnd,
        Double similarityScore
) {
}
