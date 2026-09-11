package com.document.rag.service;

import com.document.rag.dto.request.ChatSessionResponse;
import com.document.rag.dto.request.CreateChatSessionRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ChatSessionService {

    ChatSessionResponse create(
            CreateChatSessionRequest request);

    Page<ChatSessionResponse> getSessions(
            Pageable pageable);

    ChatSessionResponse getSession(UUID id);
}
