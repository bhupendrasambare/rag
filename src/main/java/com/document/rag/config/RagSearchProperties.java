package com.document.rag.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "app.rag.search")
public class RagSearchProperties {

    private int topK = 5;

    private double similarityThreshold = 0.70;
}
