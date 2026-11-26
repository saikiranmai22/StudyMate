package com.studymate.studymate.conversation.repository;

import com.studymate.studymate.conversation.entity.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

public interface ConversationRepository extends JpaRepository<Conversation, Long> {
    List<Conversation> findAllByWorkspace_IdOrderByUpdatedAtDesc(Long workspaceId);
    //Retrieve all conversations in a workspace, most recently active first
    //Workspace → follow the workspace field in Conversation.
    //_ → separate the nested property.
    //Id → check the id field inside Workspace.

    Optional<Conversation> findByIdAndWorkspace_Id(Long conversationId, Long workspaceId);
    //Retrieve one conversation only if it belongs to the specified workspace.

    //Find this conversation only if its workspace belongs to the specified user.
    Optional<Conversation> findByIdAndWorkspace_User_Id(Long conversationId, Long userId);

    //This allows you to display recently active conversations first.
    @Modifying //This query changes data. Don't treat it as a normal SELECT query. It is used with UPDATE and DELETE queries.
    @Transactional
    //JPQL (Java Persistence Query Language)
    @Query(""" 
    UPDATE Conversation c
    SET c.updatedAt = CURRENT_TIMESTAMP
    WHERE c.id = :conversationId
""")
    //@Param → Connects the Java parameter to the query
    void updateActivity(@Param("conversationId") Long conversationId);
}
