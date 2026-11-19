package com.studymate.studymate.conversation.repository;

import com.studymate.studymate.conversation.entity.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.*;

public interface ConversationRepository extends JpaRepository<Conversation, Long> {
    List<Conversation> findAllByWorkspace_IdOrderByUpdatedAtDesc(Long workspaceId);
    //Retrieve all conversations in a workspace, most recently active first

    Optional<Conversation> findByIdAndWorkspace_Id(Long conversationId, Long workspaceId);
    //Retrieve one conversation only if it belongs to the specified workspace.

}
