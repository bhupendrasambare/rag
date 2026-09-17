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
package com.document.rag.service;

import com.document.rag.constants.ChatRole;
import com.document.rag.models.ChatMessage;
import com.document.rag.models.ChatSession;
import com.document.rag.repository.ChatMessageRepository;
import com.document.rag.repository.ChatSessionRepository;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChatMessagePersistenceService {

  private final ChatMessageRepository chatMessageRepository;
  private final ChatSessionRepository chatSessionRepository;

  @Transactional
  public ChatMessage saveUserMessage(UUID sessionId, String message) {

    return saveMessage(sessionId, ChatRole.USER, message);
  }

  @Transactional
  public ChatMessage saveAssistantMessage(UUID sessionId, String message) {

    return saveMessage(sessionId, ChatRole.ASSISTANT, message);
  }

  private ChatMessage saveMessage(UUID sessionId, ChatRole role, String message) {

    LocalDateTime now = LocalDateTime.now();

    ChatMessage chatMessage =
        ChatMessage.builder()
            .id(UUID.randomUUID())
            .chatSessionId(sessionId)
            .role(role)
            .message(message)
            .createdAt(now)
            .build();

    ChatMessage saved = chatMessageRepository.save(chatMessage);

    ChatSession session = chatSessionRepository.findById(sessionId).orElseThrow();

    session.setUpdatedAt(now);

    return saved;
  }
}
