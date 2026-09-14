package com.document.rag.repository;


import java.util.List;
import java.util.UUID;

import com.document.rag.models.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatMessageRepository
        extends JpaRepository<ChatMessage, UUID> {

    List<ChatMessage> findAllByChatSessionIdOrderByCreatedAtAsc(
            UUID chatSessionId
    );
}
