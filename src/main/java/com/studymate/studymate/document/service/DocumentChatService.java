package com.studymate.studymate.document.service;

import com.studymate.studymate.document.dto.ChatRequest;
import com.studymate.studymate.document.dto.ChatResponse;
import com.studymate.studymate.document.dto.ChatSourceResponse;
import com.studymate.studymate.document.dto.RetrievedChunkResponse;
import com.studymate.studymate.document.entity.Document;
import com.studymate.studymate.document.repository.DocumentRepository;
import com.studymate.studymate.user.entity.User;
import com.studymate.studymate.workspace.repository.WorkspaceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DocumentChatService {
    private final DocumentRepository documentRepository;
    private final WorkspaceRepository workspaceRepository;
    private final DocumentRetrievalService retrievalService;
    private final DocumentPromptBuilder promptBuilder;
    private final GeminiChatClient geminiChatClient;

    public DocumentChatService(DocumentRepository documentRepository, WorkspaceRepository workspaceRepository, DocumentRetrievalService retrievalService, DocumentPromptBuilder promptBuilder, GeminiChatClient geminiChatClient) {
        this.documentRepository = documentRepository;
        this.workspaceRepository = workspaceRepository;
        this.retrievalService = retrievalService;
        this.promptBuilder = promptBuilder;
        this.geminiChatClient = geminiChatClient;
    }

    public ChatResponse askQuestion(Long workspaceId, Long documentId, User user, ChatRequest request){
        //Verify workspace ownership
        workspaceRepository.findByIdAndUser_Id(workspaceId,user.getId()).orElseThrow(()-> new IllegalStateException("Workspace not found"));
        //Verify document belongs to this workspace
        Document document= documentRepository.findByIdAndWorkspace_Id(documentId,workspaceId).orElseThrow(()-> new IllegalStateException("Document not found"));
        int topK= request.topk()==null?5:request.topk();

        //Retrieve relevant chunks
        List<RetrievedChunkResponse> chunks= retrievalService.retrievedChunkResponseList(document.getId(),request.question(),topK);

        //Handle empty retrieval results
        if (chunks.isEmpty()){
            return new ChatResponse("I couldn't find relevant information " +
                    "in this document to answer your question.",
                    List.of());
        }
        //Build the grounded prompt
        String prompt= promptBuilder.builPRompt(request.question(), chunks);
        //Generate Gemini answer
        String answer= geminiChatClient.generateAnswer(prompt);

        //Convert retrieved chunks into source DTOs
        List<ChatSourceResponse> sourceResponses= chunks.stream().map(chunk-> new ChatSourceResponse(
                chunk.chunkId(),
                chunk.pageStart(),
                chunk.pageEnd(),
                chunk.similarityScore()
        )).toList();

        //Return answer and sources
        return new ChatResponse(answer, sourceResponses);
    }
}
