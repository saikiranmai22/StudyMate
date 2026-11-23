package com.studymate.studymate.document.dto;

import java.util.List;

public record PdfExtractionResult(
        int pageCount,
        List<ExtractedPage> pages
) {
    public PdfExtractionResult {
        pages = List.copyOf(pages);
    }
}
