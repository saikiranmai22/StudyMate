package com.studymate.studymate.document.service;

import com.studymate.studymate.document.entity.Document;
import com.studymate.studymate.document.entity.DocumentChunk;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DocumentEmbeddingService {
    private final GeminiEmbeddingClient geminiEmbeddingClient;

    public DocumentEmbeddingService(GeminiEmbeddingClient geminiEmbeddingClient){
        this.geminiEmbeddingClient=geminiEmbeddingClient;
    }

    public void generateEmbeddings(Document document, List<DocumentChunk> chunks){
        if(chunks==null||chunks.isEmpty()){
            throw new IllegalStateException("No document chunks found");
        }
        for(DocumentChunk chunk:chunks){
            if(chunk.getEmbedding()!=null && !chunk.getEmbedding().isEmpty()){
                continue;
            }
            //generates for each chunk
            List<Double> embedding= geminiEmbeddingClient.generateDocumentEmbedding(document.getOriginalFilename(),chunk.getContent());
            chunk.setEmbedding(embedding); //sets the embedding
        }
        document.markCompleted(document.getPageCount(),geminiEmbeddingClient.getModel(), geminiEmbeddingClient.getDimensions());
    }
}
