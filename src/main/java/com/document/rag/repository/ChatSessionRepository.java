package com.document.rag.repository;

import com.document.rag.models.ChatSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ChatSessionRepository
        extends JpaRepository<ChatSession, UUID> {

    Page<ChatSession> findAllByUserIdOrderByUpdatedAtDesc(
            UUID userId,
            Pageable pageable);

    Optional<ChatSession> findByIdAndUserId(
            UUID id,
            UUID userId);
}
