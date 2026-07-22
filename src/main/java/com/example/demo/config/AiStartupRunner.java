/**
 * author @bhupendrasambare
 * Date   :22/07/26
 * Time   :8:17 pm
 * Project:rag
 **/
package com.example.demo.config;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class AiStartupRunner implements CommandLineRunner {

    private final ChatModel chatModel;
    private final EmbeddingModel embeddingModel;
    private final VectorStore vectorStore;

    @Override
    public void run(String... args){
        System.out.println(chatModel.getClass());

        System.out.println(embeddingModel.getClass());

        System.out.println(vectorStore.getClass());
    }

}
