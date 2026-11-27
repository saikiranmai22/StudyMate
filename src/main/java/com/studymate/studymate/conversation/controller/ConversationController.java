package com.studymate.studymate.conversation.controller;

import com.studymate.studymate.auth.security.CustomUserDetails;
import com.studymate.studymate.conversation.dto.ConversationChatRequest;
import com.studymate.studymate.conversation.dto.ConversationResponse;
import com.studymate.studymate.conversation.dto.CreateConversationRequest;
import com.studymate.studymate.conversation.dto.MessageResponse;
import com.studymate.studymate.conversation.service.ConversationChatService;
import com.studymate.studymate.conversation.service.ConversationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ConversationController {
    private final ConversationService conversationService;
    private final ConversationChatService conversationChatService;

    public ConversationController(ConversationService conversationService, ConversationChatService conversationChatService) {
        this.conversationService = conversationService;
        this.conversationChatService = conversationChatService;
    }

    @PostMapping("/workspaces/{workspaceId}/conversations")
    public ResponseEntity<ConversationResponse> createConversation(
            @PathVariable Long workspaceId,
            @RequestBody(required = false) @Valid CreateConversationRequest request, //This means the request body is optional.
            //RequestBody: Take the JSON data sent by the frontend and convert it into a Java object.
            //Valid: Before passing this Java object to the controller method, check whether it satisfies the validation rules defined in the DTO.
            @AuthenticationPrincipal CustomUserDetails principal
            ){
        ConversationResponse response= conversationService.createConversation(workspaceId,principal.getUser(),request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response); //201	Resource created
    }

    @GetMapping("/workspaces/{workspaceId}/conversations")
    public ResponseEntity<List<ConversationResponse>> getConversations(
            @PathVariable Long workspaceId,
            @AuthenticationPrincipal CustomUserDetails principal
    ){
        List<ConversationResponse> conversations= conversationService.getConversations(workspaceId,principal.getUser());
        return ResponseEntity.ok(conversations);
        //ResponseEntity is a Spring class that lets you control the HTTP response sent back to the frontend.
        //HTTP status: 200 OK : Request successful
        //Response body: conversations
    }

    @DeleteMapping("/conversations/{conversationId}")
    public ResponseEntity<Void> deleteConversation(
            @PathVariable Long conversationId,
            @AuthenticationPrincipal CustomUserDetails principal
    ){
        conversationService.deleteConversation(conversationId,principal.getUser());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/conversations/{conversationId}/messages")
    public ResponseEntity<List<MessageResponse>> getMessage(
            @PathVariable Long conversationId,
            @AuthenticationPrincipal CustomUserDetails principal
    ){
        List<MessageResponse> messages=conversationService.getMessages(conversationId,principal.getUser());
        return ResponseEntity.ok(messages);
    }

    @PostMapping("/conversations/{conversationId}/messages")
    public ResponseEntity<MessageResponse> sendMessage(
            @PathVariable Long conversationId,
            @Valid @RequestBody ConversationChatRequest request,
            @AuthenticationPrincipal CustomUserDetails principal
            ){
        MessageResponse response= conversationChatService.askQuestion(conversationId,principal.getUser(),request);
        return ResponseEntity.ok(response);
    }

}