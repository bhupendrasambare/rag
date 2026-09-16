package com.document.rag.service.impl;

import com.document.rag.models.ChatMessage;
import com.document.rag.repository.ChatMessageRepository;
import com.document.rag.service.ChatMemoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.stereotype.Service;
import org.aspectj.bridge.Message;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChatMemoryServiceImpl implements ChatMemoryService {

    private final ChatMessageRepository chatMessageRepository;

    public List<Message> getHistory(
            UUID sessionId,
            int maxMessages) {

        List<ChatMessage> messages =
                chatMessageRepository
                        .findAllByChatSessionIdOrderByCreatedAtAsc(
                                sessionId);

        int fromIndex =
                Math.max(0, messages.size() - maxMessages);

        return messages.subList(fromIndex, messages.size())
                .stream()
                .map(this::toSpringAiMessage)
                .toList();
    }

    private Message toSpringAiMessage(ChatMessage message) {

        return switch (message.getRole()) {

            case USER ->
                    new UserMessage(message.getMessage());

            case ASSISTANT ->
                    new AssistantMessage(message.getMessage());
        };
    }
}
