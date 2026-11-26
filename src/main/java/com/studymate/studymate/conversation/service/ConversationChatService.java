package com.studymate.studymate.conversation.service;

import com.studymate.studymate.conversation.dto.ConversationChatRequest;
import com.studymate.studymate.conversation.dto.MessageResponse;
import com.studymate.studymate.conversation.entity.Conversation;
import com.studymate.studymate.conversation.entity.Message;
import com.studymate.studymate.conversation.entity.MessageRole;
import com.studymate.studymate.conversation.repository.ConversationRepository;
import com.studymate.studymate.conversation.repository.MessageRepository;
import com.studymate.studymate.document.dto.ChatRequest;
import com.studymate.studymate.document.dto.ChatResponse;
import com.studymate.studymate.document.entity.Document;
import com.studymate.studymate.document.repository.DocumentRepository;
import com.studymate.studymate.document.service.DocumentChatService;
import com.studymate.studymate.user.entity.User;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@Service
public class ConversationChatService {
    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final DocumentRepository documentRepository;
    private final DocumentChatService documentChatService;
    private final ObjectMapper objectMapper;

    public ConversationChatService(ConversationRepository conversationRepository, MessageRepository messageRepository, DocumentRepository documentRepository, DocumentChatService documentChatService, ObjectMapper objectMapper) {
        this.conversationRepository = conversationRepository;
        this.messageRepository = messageRepository;
        this.documentRepository = documentRepository;
        this.documentChatService = documentChatService;
        this.objectMapper = objectMapper;
    }

    public MessageResponse askQuestion(Long conversationId, User user, ConversationChatRequest request){
        //Verify conversation ownership by checking whether the workspace it is in belongs to the user
        Conversation conversation= conversationRepository.findByIdAndWorkspace_User_Id(conversationId, user.getId()).orElseThrow(()->new IllegalStateException("Conversation not found"));
        //Verify document belongs to this conversation's workspace
        Document document= documentRepository.findByIdAndWorkspace_Id(request.documentId(),conversation.getWorkspace().getId()).orElseThrow(()->new IllegalStateException("Document not found in this workspace"));

        //save the question asked by the user
        Message userMessage= new Message(conversation, MessageRole.USER,request.question(),null); //sources is null
        messageRepository.save(userMessage);

        //generate the answer for the users question
        ChatResponse chatResponse= documentChatService.askQuestion(conversation.getWorkspace().getId(), document.getId(), user, new ChatRequest(request.question(),request.topK()));

        //convert the sources into JSON
//        JsonNode sources= objectMapper.valueToTree(chatResponse.sources());

        String sourcesJson = objectMapper.writeValueAsString(
                chatResponse.sources()
        );

        //save the response and the assisstants response in the db
        Message assitantMessage= new Message(conversation, MessageRole.ASSISTANT, chatResponse.answer(), sourcesJson);
        assitantMessage= messageRepository.save(assitantMessage);

        conversationRepository.updateActivity(conversation.getId());

        if("New Conversation".equals(conversation.getTitle())){ //generate its title
            String title= request.question();
            if(title.length()>190){
                title= title.substring(0,190)+"...";
            }
            conversation.updateTitle(title);
            conversationRepository.save(conversation);
        }

        return new MessageResponse(
                assitantMessage.getId(),
                assitantMessage.getRole(),
                assitantMessage.getContent(),
                parseSources(assitantMessage.getSources()),
                assitantMessage.getCreatedAt()
        );
    }
    private JsonNode parseSources(String sources) {
        if (sources == null || sources.isBlank()) {
            return null;
        }

        return objectMapper.readTree(sources);
    }
}
