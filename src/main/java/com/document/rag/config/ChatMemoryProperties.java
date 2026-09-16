package com.document.rag.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "app.rag.chat.memory")
public class ChatMemoryProperties {

    private int maxMessages = 10;
}
