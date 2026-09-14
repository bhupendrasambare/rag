package com.document.rag.service.impl;

import com.document.rag.chat.RagChatService;
import com.document.rag.constants.ChatRole;
import com.document.rag.dto.request.SendChatMessageRequest;
import com.document.rag.dto.response.ChatMessageResponse;
import com.document.rag.exception.ChatSessionNotFoundException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.document.rag.models.ChatMessage;
import com.document.rag.models.ChatSession;
import com.document.rag.repository.ChatMessageRepository;
import com.document.rag.repository.ChatSessionRepository;
import com.document.rag.service.ChatMessageService;
import com.document.rag.service.UserService;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChatMessageServiceImpl
        implements ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatSessionRepository chatSessionRepository;
    private final UserService userService;
    private final RagChatService ragChatService;

    @Override
    public ChatMessageResponse sendMessage(
            UUID sessionId,
            SendChatMessageRequest request) {

        UUID userId = userService.getProfile().getId();

        ChatSession session =
                getOwnedSession(sessionId, userId);

        String question = request.message().trim();

        ChatMessage userMessage =
                saveUserMessage(session.getId(), question);

        try {

            String answer =
                    ragChatService.chat(question, userId);

            ChatMessage assistantMessage =
                    saveAssistantMessage(
                            session.getId(),
                            answer
                    );

            return toResponse(assistantMessage);

        } catch (RuntimeException exception) {

            /*
             * The USER message has already been committed.
             *
             * We intentionally do not delete it if the RAG
             * operation fails. It represents what the user sent.
             */

            throw exception;
        }
    }

    @Override
    public List<ChatMessageResponse> getMessages(
            UUID sessionId) {

        UUID userId = userService.getProfile().getId();

        getOwnedSession(sessionId, userId);

        return chatMessageRepository
                .findAllByChatSessionIdOrderByCreatedAtAsc(sessionId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private ChatSession getOwnedSession(
            UUID sessionId,
            UUID userId) {

        return chatSessionRepository
                .findByIdAndUserId(sessionId, userId)
                .orElseThrow(
                        ChatSessionNotFoundException::new
                );
    }

    @Transactional
    protected ChatMessage saveUserMessage(
            UUID sessionId,
            String message) {

        LocalDateTime now = LocalDateTime.now();

        ChatMessage chatMessage = ChatMessage.builder()
                .id(UUID.randomUUID())
                .chatSessionId(sessionId)
                .role(ChatRole.USER)
                .message(message)
                .createdAt(now)
                .build();

        ChatMessage saved =
                chatMessageRepository.save(chatMessage);

        updateSessionTimestamp(sessionId, now);

        return saved;
    }

    @Transactional
    protected ChatMessage saveAssistantMessage(
            UUID sessionId,
            String message) {

        LocalDateTime now = LocalDateTime.now();

        ChatMessage chatMessage = ChatMessage.builder()
                .id(UUID.randomUUID())
                .chatSessionId(sessionId)
                .role(ChatRole.ASSISTANT)
                .message(message)
                .createdAt(now)
                .build();

        ChatMessage saved =
                chatMessageRepository.save(chatMessage);

        updateSessionTimestamp(sessionId, now);

        return saved;
    }

    private void updateSessionTimestamp(
            UUID sessionId,
            LocalDateTime updatedAt) {

        chatSessionRepository.findById(sessionId)
                .ifPresent(session -> {
                    session.setUpdatedAt(updatedAt);
                    chatSessionRepository.save(session);
                });
    }

    private ChatMessageResponse toResponse(
            ChatMessage message) {

        return new ChatMessageResponse(
                message.getId(),
                message.getRole(),
                message.getMessage(),
                message.getCreatedAt()
        );
    }
}
