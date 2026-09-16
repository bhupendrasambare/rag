/*
 * Copyright (c) 2026 Bhupendra Sambare
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied.
 *
 * See the License for the specific language governing permissions
 * and limitations under the License.
 */
package com.document.rag.config;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class ChatClientConfig {

  @Bean
  public ChatClient chatClient(
      ChatModel chatModel, VectorStore vectorStore, RagSearchProperties searchProperties) {

    QuestionAnswerAdvisor questionAnswerAdvisor =
            QuestionAnswerAdvisor.builder(vectorStore)
                    .searchRequest(
                            SearchRequest.builder()
                                    .topK(searchProperties.getTopK())
                                    .similarityThreshold(
                                            searchProperties
                                                    .getSimilarityThreshold())
                                    .build()
                    )
                    .build();

    return ChatClient.builder(chatModel)
            .defaultSystem("""
                You are a document question-answering assistant.

                Answer questions using only the information provided
                in the retrieved document context.

                If the retrieved context does not contain enough
                information to answer the question, say that you
                could not find the answer in the uploaded documents.

                Do not invent facts or use unsupported information.
                """)
            .defaultAdvisors(questionAnswerAdvisor)
            .build();
  }
}
