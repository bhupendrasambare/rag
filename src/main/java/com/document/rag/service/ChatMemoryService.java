package com.document.rag.service;


import org.aspectj.bridge.Message;

import java.util.List;
import java.util.UUID;

public interface ChatMemoryService {

    List<Message> getHistory(
            UUID sessionId,
            int maxMessages);


}
