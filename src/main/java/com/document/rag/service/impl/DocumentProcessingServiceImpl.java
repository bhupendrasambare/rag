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

import com.document.rag.constants.DocumentStatus;
import com.document.rag.exception.custom.DocumentInfoNotFoundException;
import com.document.rag.exception.custom.DocumentNotReadableException;
import com.document.rag.exception.custom.DocumentProcessingFailedException;
import com.document.rag.models.DocumentInfo;
import com.document.rag.repository.DocumentInfoRepository;
import com.document.rag.service.ChunkService;
import com.document.rag.service.DocumentProcessingService;
import com.document.rag.service.DocumentReaderService;
import com.document.rag.service.EmbeddingService;
import com.document.rag.service.VectorStoreService;
import com.document.rag.storage.FileStorageService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentProcessingServiceImpl implements DocumentProcessingService {

  private final DocumentInfoRepository documentRepository;
  private final FileStorageService fileStorageService;
  private final DocumentReaderService documentReaderService;
  private final ChunkService chunkService;
  private final EmbeddingService embeddingService;
  private final VectorStoreService vectorStoreService;

  @Override
  public void process(UUID documentId) {

    log.info("Starting document processing pipeline. documentId={}", documentId);

    try {

      DocumentInfo documentInfo = getDocument(documentId);

      updateStatus(documentId, DocumentStatus.PROCESSING);

      Resource resource =
          fileStorageService.getResource(documentInfo.getId(), documentInfo.getOriginalFileName());

      if (resource == null || !resource.exists()) {
        throw new DocumentNotReadableException();
      }

      log.info(
          "Reading document. documentId={}, fileName={}",
          documentId,
          documentInfo.getOriginalFileName());

      List<Document> documents = documentReaderService.read(resource);

      if (documents == null || documents.isEmpty()) {
        throw new DocumentNotReadableException();
      }

      log.info(
          "Document reading completed. documentId={}, documents={}", documentId, documents.size());

      List<Document> chunks = chunkService.chunk(documents);

      if (chunks == null || chunks.isEmpty()) {
        throw new DocumentProcessingFailedException();
      }

      log.info("Document chunking completed. documentId={}, chunks={}", documentId, chunks.size());

      enrichMetadata(chunks, documentInfo);

      List<Document> embeddedChunks = embeddingService.embed(chunks);

      if (embeddedChunks == null || embeddedChunks.isEmpty()) {
        throw new DocumentProcessingFailedException();
      }

      log.info(
          "Document embedding completed. documentId={}, embeddings={}",
          documentId,
          embeddedChunks.size());

      vectorStoreService.save(embeddedChunks);

      log.info(
          "Document vectors stored successfully. documentId={}, vectors={}",
          documentId,
          embeddedChunks.size());

      markCompleted(documentId, embeddedChunks.size());

      log.info("Document processing completed successfully. documentId={}", documentId);

    } catch (DocumentProcessingFailedException exception) {

      log.error(
          "Document processing failed. documentId={}, message={}",
          documentId,
          exception.getMessage(),
          exception);

      markFailed(documentId);

      throw exception;

    } catch (Exception exception) {

      log.error(
          "Unexpected document processing error. documentId={}, exceptionType={}, message={}",
          documentId,
          exception.getClass().getName(),
          exception.getMessage(),
          exception);

      markFailed(documentId);

      throw new DocumentProcessingFailedException();
    }
  }

  private DocumentInfo getDocument(UUID documentId) {
    return documentRepository.findById(documentId).orElseThrow(DocumentInfoNotFoundException::new);
  }

  @Transactional
  protected void updateStatus(UUID documentId, DocumentStatus status) {

    DocumentInfo documentInfo = getDocument(documentId);

    documentInfo.setStatus(status);
    documentInfo.setUpdatedAt(LocalDateTime.now());

    documentRepository.save(documentInfo);
  }

  @Transactional
  protected void markCompleted(UUID documentId, long chunkCount) {

    DocumentInfo documentInfo = getDocument(documentId);

    documentInfo.setStatus(DocumentStatus.COMPLETED);
    documentInfo.setChunkCount(chunkCount);
    documentInfo.setProcessedAt(LocalDateTime.now());
    documentInfo.setUpdatedAt(LocalDateTime.now());

    documentRepository.save(documentInfo);
  }

  @Transactional
  protected void markFailed(UUID documentId) {

    try {

      DocumentInfo documentInfo = getDocument(documentId);

      documentInfo.setStatus(DocumentStatus.FAILED);
      documentInfo.setUpdatedAt(LocalDateTime.now());

      documentRepository.save(documentInfo);

    } catch (Exception exception) {

      log.error(
          "Failed to persist FAILED document status. documentId={}, message={}",
          documentId,
          exception.getMessage(),
          exception);
    }
  }

  @Override
  public void deleteVectors(UUID documentId) {

    if (documentId == null) {
      log.warn("Skipping vector deletion because documentId is null.");
      return;
    }

    vectorStoreService.delete(documentId);
  }

  private void enrichMetadata(List<Document> chunks, DocumentInfo documentInfo) {

    for (Document chunk : chunks) {

      chunk.getMetadata().put("documentId", documentInfo.getId().toString());

      chunk.getMetadata().put("userId", documentInfo.getUserId().toString());

      chunk.getMetadata().put("fileName", documentInfo.getOriginalFileName());
    }
  }
}
