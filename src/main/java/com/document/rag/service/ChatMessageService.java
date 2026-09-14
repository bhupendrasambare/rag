package com.document.rag.service;

import com.document.rag.dto.request.SendChatMessageRequest;
import com.document.rag.dto.response.ChatMessageResponse;

import java.util.List;
import java.util.UUID;

public interface ChatMessageService {

    ChatMessageResponse sendMessage(
            UUID sessionId,
            SendChatMessageRequest request
    );

    List<ChatMessageResponse> getMessages(
            UUID sessionId
    );
}
