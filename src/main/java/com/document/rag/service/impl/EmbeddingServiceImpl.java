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

import com.document.rag.service.EmbeddingService;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EmbeddingServiceImpl implements EmbeddingService {

  @Override
  public List<Document> embed(List<Document> chunks) {

    if (chunks == null || chunks.isEmpty()) {

      log.warn("Embedding skipped because no chunks were provided.");

      return List.of();
    }

    log.info("Embedding stage prepared. chunks={}", chunks.size());

    return chunks;
  }
}
