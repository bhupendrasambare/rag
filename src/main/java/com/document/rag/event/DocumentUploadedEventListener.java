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
package com.document.rag.event;

import com.document.rag.dto.DocumentUploadedEvent;
import com.document.rag.service.DocumentProcessingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class DocumentUploadedEventListener {

  private final DocumentProcessingService documentProcessingService;

  @Async
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handle(DocumentUploadedEvent event) {

    log.info("DocumentUploadedEvent received. documentId={}", event.documentId());

    try {

      documentProcessingService.process(event.documentId());

    } catch (Exception exception) {

      log.error(
          "Document processing failed from event listener. " + "documentId={}, message={}",
          event.documentId(),
          exception.getMessage(),
          exception);
    }
  }
}
