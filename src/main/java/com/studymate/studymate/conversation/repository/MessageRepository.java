package com.studymate.studymate.conversation.repository;

import com.studymate.studymate.conversation.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByConversation_IdOrderByCreatedAtAsc(Long conversationId);
    //We need to display messages in chronological order

    long countByConversation_Id(Long conversationId);

}
