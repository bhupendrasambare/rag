/*
 * Copyright (c) 2026 Bhupendra Sambare
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied.
 *
 * See the License for the specific language governing permissions
 * and limitations under the License.
 */
package com.document.rag.service.impl;

import com.document.rag.constants.ChatRole;
import com.document.rag.dto.request.SendChatMessageRequest;
import com.document.rag.dto.response.ChatMessageResponse;
import com.document.rag.exception.ChatSessionNotFoundException;
import com.document.rag.models.ChatMessage;
import com.document.rag.models.ChatSession;
import com.document.rag.repository.ChatMessageRepository;
import com.document.rag.repository.ChatSessionRepository;
import com.document.rag.service.ChatMessagePersistenceService;
import com.document.rag.service.ChatMessageService;
import com.document.rag.service.RagChatService;
import com.document.rag.service.UserService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChatMessageServiceImpl implements ChatMessageService {

  private final ChatMessageRepository chatMessageRepository;
  private final ChatSessionRepository chatSessionRepository;
  private final UserService userService;
  private final RagChatService ragChatService;
  private final ChatMessagePersistenceService persistenceService;

  @Override
  public ChatMessageResponse sendMessage(UUID sessionId, SendChatMessageRequest request) {

    UUID userId = userService.getProfile().getId();

    getOwnedSession(sessionId, userId);

    String question = request.message().trim();

    persistenceService.saveUserMessage(sessionId, question);

    String answer = ragChatService.chat(sessionId, question, userId);

    ChatMessage assistantMessage = persistenceService.saveAssistantMessage(sessionId, answer);

    return toResponse(assistantMessage);
  }

  @Override
  public List<ChatMessageResponse> getMessages(UUID sessionId) {

    UUID userId = userService.getProfile().getId();

    getOwnedSession(sessionId, userId);

    return chatMessageRepository.findAllByChatSessionIdOrderByCreatedAtAsc(sessionId).stream()
        .map(this::toResponse)
        .toList();
  }

  private ChatSession getOwnedSession(UUID sessionId, UUID userId) {

    return chatSessionRepository
        .findByIdAndUserId(sessionId, userId)
        .orElseThrow(ChatSessionNotFoundException::new);
  }

  @Transactional
  protected ChatMessage saveUserMessage(UUID sessionId, String message) {

    LocalDateTime now = LocalDateTime.now();

    ChatMessage chatMessage =
        ChatMessage.builder()
            .id(UUID.randomUUID())
            .chatSessionId(sessionId)
            .role(ChatRole.USER)
            .message(message)
            .createdAt(now)
            .build();

    ChatMessage saved = chatMessageRepository.save(chatMessage);

    updateSessionTimestamp(sessionId, now);

    return saved;
  }

  @Transactional
  protected ChatMessage saveAssistantMessage(UUID sessionId, String message) {

    LocalDateTime now = LocalDateTime.now();

    ChatMessage chatMessage =
        ChatMessage.builder()
            .id(UUID.randomUUID())
            .chatSessionId(sessionId)
            .role(ChatRole.ASSISTANT)
            .message(message)
            .createdAt(now)
            .build();

    ChatMessage saved = chatMessageRepository.save(chatMessage);

    updateSessionTimestamp(sessionId, now);

    return saved;
  }

  private void updateSessionTimestamp(UUID sessionId, LocalDateTime updatedAt) {

    chatSessionRepository
        .findById(sessionId)
        .ifPresent(
            session -> {
              session.setUpdatedAt(updatedAt);
              chatSessionRepository.save(session);
            });
  }

  private ChatMessageResponse toResponse(ChatMessage message) {

    return new ChatMessageResponse(
        message.getId(), message.getRole(), message.getMessage(), message.getCreatedAt());
  }
}
