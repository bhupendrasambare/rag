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
package com.document.rag.controller;

import com.document.rag.chat.RagChatService;
import com.document.rag.chat.dto.ChatRequest;
import com.document.rag.chat.dto.ChatResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat")
public class ChatController {

  private final RagChatService ragChatService;

  @PostMapping
  public ChatResponse chat(@Valid @RequestBody ChatRequest request) {

    String answer = ragChatService.chat(request.question());

    return new ChatResponse(answer);
  }
}
