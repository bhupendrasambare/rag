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
package com.document.rag.chat;

import com.document.rag.config.ChatMemoryProperties;
import com.document.rag.service.ChatMemoryService;
import com.document.rag.service.UserService;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.aspectj.bridge.Message;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RagChatService {

  private final ChatClient chatClient;
  private final ChatMemoryService chatMemoryService;
  private final ChatMemoryProperties memoryProperties;

  public String chat(
          UUID sessionId,
          String question,
          UUID userId) {

    if (question == null || question.isBlank()) {
      throw new IllegalArgumentException(
              "Question cannot be empty.");
    }

    String filterExpression =
            "userId == '" + userId + "'";

    List<Message> history =
            chatMemoryService.getHistory(
                    sessionId,
                    memoryProperties.getMaxMessages());

    return chatClient
            .prompt()
            .messages(history)
            .advisors(advisor -> advisor
                    .param(
                            QuestionAnswerAdvisor.FILTER_EXPRESSION,
                            filterExpression
                    )
            )
            .user(question)
            .call()
            .content();
  }
}
