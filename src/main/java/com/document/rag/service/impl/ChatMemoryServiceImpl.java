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

import com.document.rag.models.ChatMessage;
import com.document.rag.repository.ChatMessageRepository;
import com.document.rag.service.ChatMemoryService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatMemoryServiceImpl implements ChatMemoryService {

  private final ChatMessageRepository chatMessageRepository;

  public List<Message> getPreviousMessages(UUID sessionId, int maxMessages) {

    List<ChatMessage> messages =
        chatMessageRepository.findAllByChatSessionIdOrderByCreatedAtAsc(sessionId);

    if (messages.isEmpty()) {
      return List.of();
    }

    int endIndex = messages.size() - 1;

    int fromIndex = Math.max(0, endIndex - maxMessages);

    return messages.subList(fromIndex, endIndex).stream().map(this::toSpringAiMessage).toList();
  }

  private Message toSpringAiMessage(ChatMessage message) {

    return switch (message.getRole()) {
      case USER -> new UserMessage(message.getMessage());

      case ASSISTANT -> new AssistantMessage(message.getMessage());

      case SYSTEM -> new SystemMessage(message.getMessage());
    };
  }
}
