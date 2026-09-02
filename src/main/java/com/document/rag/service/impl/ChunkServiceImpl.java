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

import com.document.rag.exception.custom.DocumentNotEnoughTextException;
import com.document.rag.service.ChunkService;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ChunkServiceImpl implements ChunkService {

  private final TokenTextSplitter splitter;

  public ChunkServiceImpl() {

    this.splitter =
        TokenTextSplitter.builder()
            .withChunkSize(800)
            .withMinChunkSizeChars(350)
            .withMinChunkLengthToEmbed(5)
            .build();
  }

  @Override
  public List<Document> chunk(List<Document> documents) {

    if (documents == null || documents.isEmpty()) {

      log.error("Cannot create chunks because extracted documents are empty.");

      throw new DocumentNotEnoughTextException();
    }

    log.info("Starting document chunking. documents={}", documents.size());

    List<Document> chunks = splitter.apply(documents);

    if (chunks == null || chunks.isEmpty()) {

      log.error("Chunking produced no chunks. documents={}", documents.size());

      throw new DocumentNotEnoughTextException();
    }

    log.info(
        "Document chunking completed. documents={}, chunks={}", documents.size(), chunks.size());

    return chunks;
  }
}
