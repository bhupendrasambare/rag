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
package com.document.rag.service.impl;

import com.document.rag.service.VectorStoreService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class VectorStoreServiceImpl implements VectorStoreService {

  private final VectorStore vectorStore;

  @Override
  public void save(List<Document> documents) {

    if (documents == null || documents.isEmpty()) {

      log.warn("Vector store save skipped because documents are empty.");

      return;
    }

    log.info("Saving documents to vector store. documents={}", documents.size());

    vectorStore.add(documents);

    log.info("Documents successfully saved to vector store. documents={}", documents.size());
  }

  @Override
  public void delete(UUID documentId) {

    if (documentId == null) {

      log.warn("Vector deletion skipped because documentId is null.");

      return;
    }

    String filterExpression = "documentId == '" + documentId + "'";

    try {

      log.info("Deleting vectors. documentId={}", documentId);

      vectorStore.delete(filterExpression);

      log.info("Vectors deleted successfully. documentId={}", documentId);

    } catch (Exception exception) {

      log.error(
          "Failed to delete vectors. documentId={}, message={}",
          documentId,
          exception.getMessage(),
          exception);

      throw exception;
    }
  }

  @Override
  public List<Document> similaritySearch(String query) {
    SearchRequest searchRequest = SearchRequest.builder().query(query).topK(5).build();

    return vectorStore.similaritySearch(searchRequest);
  }
}
