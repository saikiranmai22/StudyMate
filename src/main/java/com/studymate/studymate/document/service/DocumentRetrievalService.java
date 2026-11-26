package com.studymate.studymate.document.service;

import com.studymate.studymate.document.dto.RetrievedChunkResponse;
import com.studymate.studymate.document.entity.DocumentChunk;
import com.studymate.studymate.document.repository.DocumentChunkRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class DocumentRetrievalService {
    private final DocumentChunkRepository documentChunkRepository;
    private final GeminiEmbeddingClient geminiEmbeddingClient;
    private final SimilarityCalculator similarityCalculator;

    public DocumentRetrievalService(DocumentChunkRepository documentChunkRepository, GeminiEmbeddingClient geminiEmbeddingClient, SimilarityCalculator similarityCalculator) {
        this.documentChunkRepository = documentChunkRepository;
        this.geminiEmbeddingClient = geminiEmbeddingClient;
        this.similarityCalculator = similarityCalculator;
    }

    public List<RetrievedChunkResponse> retrievedChunkResponseList(Long documentId, String question, int topK){
        if(question==null|| question.isBlank()){
            throw new IllegalStateException("Question cannot be empty");
        }
        if(topK<=0){
            throw new IllegalStateException("topK must be greater than zero");
        }
        //Generate embedding for the student's question
        List<Double> questionEmbedding= geminiEmbeddingClient.generateDocumentEmbedding("Student question", question);

        //Fetch all chunks belonging to this document
        List<DocumentChunk> chunks= documentChunkRepository.findAllByDocument_IdOrderByChunkIndexAsc(documentId);

        for (DocumentChunk chunk : chunks) {
            if (chunk.getEmbedding() == null) {
                continue;
            }
        }

        return chunks.stream().filter(chunk-> chunk.getEmbedding() !=null).map(chunk->{
            //Calculate similarity for each chunk
            double similarity= similarityCalculator.calculateCosineSimilarity(questionEmbedding, chunk.getEmbedding());
            return new RetrievedChunkResponse(chunk.getId(),chunk.getContent(),chunk.getPageStart(),chunk.getPageEnd(),similarity);

        }).sorted( //Sort from highest similarity to lowest
                Comparator.comparing(RetrievedChunkResponse::similarityScore).reversed()
        ).limit(topK).toList(); //Return only the top K chunks.

    }
}