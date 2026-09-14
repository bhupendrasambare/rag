package com.document.rag.dto.response;

import com.document.rag.constants.ChatRole;

import java.time.LocalDateTime;
import java.util.UUID;

public record ChatMessageResponse(
        UUID id,
        ChatRole role,
        String message,
        LocalDateTime createdAt
) {
}
