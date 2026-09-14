package com.document.rag.controller;

import java.util.UUID;

import com.document.rag.dto.response.ChatSessionResponse;
import com.document.rag.dto.request.CreateChatSessionRequest;
import com.document.rag.service.ChatSessionService;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat/sessions")
public class ChatSessionController {

    private final ChatSessionService chatSessionService;

    @PostMapping
    public ChatSessionResponse createSession(
            @RequestBody(required = false)
            CreateChatSessionRequest request) {

        return chatSessionService.create(request);
    }

    @GetMapping
    public Page<ChatSessionResponse> getSessions(
            Pageable pageable) {

        return chatSessionService.getSessions(pageable);
    }

    @GetMapping("/{sessionId}")
    public ChatSessionResponse getSession(
            @PathVariable UUID sessionId) {

        return chatSessionService.getSession(sessionId);
    }
}
