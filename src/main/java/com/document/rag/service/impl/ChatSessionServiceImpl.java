package com.document.rag.service.impl;

import com.document.rag.dto.request.ChatSessionResponse;
import com.document.rag.dto.request.CreateChatSessionRequest;
import com.document.rag.exception.ChatSessionNotFoundException;
import com.document.rag.models.ChatSession;
import com.document.rag.repository.ChatSessionRepository;
import com.document.rag.service.ChatSessionService;
import com.document.rag.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatSessionServiceImpl
        implements ChatSessionService {

    private final ChatSessionRepository chatSessionRepository;
    private final UserService userService;

    @Override
    @Transactional
    public ChatSessionResponse create(
            CreateChatSessionRequest request) {

        UUID userId = userService.getProfile().getId();

        LocalDateTime now = LocalDateTime.now();

        ChatSession session = ChatSession.builder()
                .userId(userId)
                .topic(request != null
                        ? request.topic()
                        : null)
                .createdAt(now)
                .updatedAt(now)
                .build();

        session = chatSessionRepository.save(session);

        return toResponse(session);
    }

    @Override
    public Page<ChatSessionResponse> getSessions(
            Pageable pageable) {

        UUID userId = userService.getProfile().getId();

        return chatSessionRepository
                .findAllByUserIdOrderByUpdatedAtDesc(
                        userId,
                        pageable)
                .map(this::toResponse);
    }

    @Override
    public ChatSessionResponse getSession(UUID id) {

        UUID userId = userService.getProfile().getId();

        ChatSession session =
                chatSessionRepository
                        .findByIdAndUserId(id, userId)
                        .orElseThrow(ChatSessionNotFoundException::new);

        return toResponse(session);
    }

    private ChatSessionResponse toResponse(
            ChatSession session) {

        return new ChatSessionResponse(
                session.getId(),
                session.getTopic(),
                session.getCreatedAt(),
                session.getUpdatedAt()
        );
    }
}
