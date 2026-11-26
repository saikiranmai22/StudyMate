package com.studymate.studymate.document.dto;

public record ChatSourceResponse(
        Long chunkId,
        Integer pageStart,
        Integer pageEnd,
        Double similarityScore
) {
}
