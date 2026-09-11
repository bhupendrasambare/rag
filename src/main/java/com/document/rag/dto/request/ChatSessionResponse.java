package com.document.rag.dto.request;

import java.time.LocalDateTime;
import java.util.UUID;

public record ChatSessionResponse(
        UUID id,
        String topic,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
