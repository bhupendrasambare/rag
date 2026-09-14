package com.document.rag.dto.request;

import jakarta.validation.constraints.NotBlank;

public record SendChatMessageRequest(

        @NotBlank(message = "Question cannot be empty.")
        String message

) {
}
