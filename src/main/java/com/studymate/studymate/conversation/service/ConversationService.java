package com.studymate.studymate.conversation.service;

import com.studymate.studymate.conversation.dto.ConversationResponse;
import com.studymate.studymate.conversation.dto.CreateConversationRequest;
import com.studymate.studymate.conversation.dto.MessageResponse;
import com.studymate.studymate.conversation.entity.Conversation;
import com.studymate.studymate.conversation.repository.ConversationRepository;
import com.studymate.studymate.conversation.repository.MessageRepository;
import com.studymate.studymate.user.entity.User;
import com.studymate.studymate.workspace.entity.Workspace;
import com.studymate.studymate.workspace.repository.WorkspaceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

@Service
public class ConversationService {
    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final WorkspaceRepository workspaceRepository;
    private final ObjectMapper objectMapper;

    public ConversationService(ConversationRepository conversationRepository, MessageRepository messageRepository, WorkspaceRepository workspaceRepository, ObjectMapper objectMapper) {
        this.conversationRepository = conversationRepository;
        this.messageRepository = messageRepository;
        this.workspaceRepository = workspaceRepository;
        this.objectMapper=objectMapper;
    }

    private ConversationResponse toConversationResponse(Conversation conversation){
        long count= messageRepository.countByConversation_Id(conversation.getId());
        return new ConversationResponse(conversation.getId(),conversation.getTitle(),conversation.getCreatedAt(),conversation.getUpdatedAt(),count);
    }

    public ConversationResponse createConversation(Long workspaceId, User user, CreateConversationRequest request){
        Workspace workspace= workspaceRepository.findByIdAndUser_Id(workspaceId,user.getId()).orElseThrow(()-> new IllegalStateException("Workspace not found"));
        String title= request==null? null:request.title();
        Conversation conversation= new Conversation(workspace,title);
        conversation=conversationRepository.save(conversation);
        return toConversationResponse(conversation);
    }

    //GET ALL CONVERSATIONS IN A WORKSPACE
    @Transactional(readOnly = true)
    public List<ConversationResponse> getConversations(Long workspaceId, User user){
        workspaceRepository.findByIdAndUser_Id(workspaceId,user.getId()).orElseThrow(()-> new IllegalStateException("Workspace not found"));
        return conversationRepository.findAllByWorkspace_IdOrderByUpdatedAtDesc(workspaceId).stream().map(this::toConversationResponse).toList();
    }

    private Conversation getOwnedConversation(Long conversationId, User user){
        return conversationRepository.findByIdAndWorkspace_User_Id(conversationId,user.getId()).orElseThrow(()-> new IllegalStateException("Conversation not found"));
    }

    //get messages of the conversation
    @Transactional(readOnly = true)
    public List<MessageResponse> getMessages(Long conversationId, User user){
        Conversation conversation=getOwnedConversation(conversationId,user);
        return messageRepository.findByConversation_IdOrderByCreatedAtAsc(conversation.getId())
                .stream()
                .map(message -> new MessageResponse(
                        message.getId(),
                        message.getRole(),
                        message.getContent(),
                        parseSources(message.getSources()),
                        message.getCreatedAt())).toList();
    }

    @Transactional
    public void deleteConversation(Long conversationId, User user){
        Conversation conversation= getOwnedConversation(conversationId,user);
        messageRepository.deleteAll(messageRepository.findByConversation_IdOrderByCreatedAtAsc(conversationId));
        conversationRepository.delete(conversation);
    }

    private JsonNode parseSources(String sources) {
        if (sources == null || sources.isBlank()) {
            return null;
        }

        return objectMapper.readTree(sources);
    }
}
