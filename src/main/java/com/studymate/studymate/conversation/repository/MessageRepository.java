package com.studymate.studymate.conversation.repository;

import com.studymate.studymate.conversation.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByConversation_IdOrderByCreatedAtAsc(Long conversationId);
    //We need to display messages in chronological order

    //counts messages without loading all of them
    long countByConversation_Id(Long conversationId);

    Optional<Message> findByIdAndConversation_Workspace_User_Id(
            Long messageId,
            Long userId
    );
}
