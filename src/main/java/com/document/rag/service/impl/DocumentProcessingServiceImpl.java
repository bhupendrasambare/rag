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
import com.document.rag.exception.custom.BaseException;
import com.document.rag.exception.custom.DocumentNotEnoughTextException;
import com.document.rag.exception.custom.DocumentNotReadableException;
import com.document.rag.exception.custom.DocumentProcessingFailedException;
import com.document.rag.models.DocumentInfo;
import com.document.rag.repository.DocumentInfoRepository;
import com.document.rag.service.DocumentProcessingService;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.document.DocumentReader;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentProcessingServiceImpl implements DocumentProcessingService {

  private final DocumentInfoRepository documentRepository;

  private final VectorStore vectorStore;

  @Override
  @Transactional
  public void process(DocumentInfo documentInfo, MultipartFile file) throws IOException {

    try {

      log.info(
          "Starting document processing. documentId={}, fileName={}",
          documentInfo.getId(),
          documentInfo.getFileName());

      documentInfo.setStatus(DocumentStatus.PROCESSING);
      documentInfo = documentRepository.save(documentInfo);

      List<Document> documents = extractDocuments(file);

      if (documents != null && !documents.isEmpty()) {
        log.info(
            "Document extracted successfully. documentId={}, pages={}",
            documentInfo.getId(),
            documents.size());

        TokenTextSplitter splitter =
            TokenTextSplitter.builder()
                .withChunkSize(800)
                .withMinChunkSizeChars(350)
                .withMinChunkLengthToEmbed(5)
                .build();

        List<Document> chunks = splitter.apply(documents);

        log.info(
            "Document split completed. documentId={}, chunks={}",
            documentInfo.getId(),
            chunks.size());

        if (chunks.isEmpty()) {

          log.error(
              "Document does not contain enough text to create chunks. "
                  + "documentId={}, fileName={}",
              documentInfo.getId(),
              documentInfo.getFileName());

          throw new DocumentNotEnoughTextException();
        }

        final String documentId = documentInfo.getId().toString();
        final String userId = documentInfo.getUserId().toString();
        final String fileName = documentInfo.getFileName();

        chunks.forEach(
            chunk -> {
              chunk.getMetadata().put("documentId", documentId);
              chunk.getMetadata().put("userId", userId);
              chunk.getMetadata().put("fileName", fileName);
            });

        log.info(
            "Adding document chunks to vector store. " + "documentId={}, chunks={}",
            documentInfo.getId(),
            chunks.size());

        vectorStore.add(chunks);

        documentInfo.setStatus(DocumentStatus.COMPLETED);

        log.info(
            "Document processing completed successfully. " + "documentId={}, fileName={}",
            documentInfo.getId(),
            documentInfo.getFileName());

      } else {
        log.error(
            "Document extraction returned no content. documentId={}, fileName={}",
            documentInfo.getId(),
            documentInfo.getFileName());
        throw new DocumentNotReadableException();
      }

    } catch (BaseException exception) {
      log.error(
          "Document processing failed. "
              + "documentId={}, fileName={}, "
              + "errorCode={}, message={}",
          documentInfo.getId(),
          documentInfo.getFileName(),
          exception.getErrorCode(),
          exception.getMessage(),
          exception);

      documentInfo.setStatus(DocumentStatus.FAILED);

      throw exception;

    } catch (IOException exception) {
      log.error(
          "IO error while processing document. " + "documentId={}, fileName={}, message={}",
          documentInfo.getId(),
          documentInfo.getFileName(),
          exception.getMessage(),
          exception);

      documentInfo.setStatus(DocumentStatus.FAILED);

      throw exception;

    } catch (Exception exception) {

      log.error(
          "Unexpected error while processing document. "
              + "documentId={}, fileName={}, exceptionType={}, message={}",
          documentInfo.getId(),
          documentInfo.getFileName(),
          exception.getClass().getName(),
          exception.getMessage(),
          exception);

      documentInfo.setStatus(DocumentStatus.FAILED);

      throw new DocumentProcessingFailedException();

    } finally {

      documentInfo.setUpdatedAt(LocalDateTime.now());

      documentRepository.save(documentInfo);

      log.info(
          "Document processing status persisted. " + "documentId={}, status={}",
          documentInfo.getId(),
          documentInfo.getStatus());
    }
  }

  @Override
  public void deleteVectors(UUID documentId) {

    if (documentId == null) {

      log.warn("Skipping vector deletion because documentId is null.");
      return;
    }

    try {

      String filterExpression = "documentId == '" + documentId + "'";
      log.info("Deleting document vectors. documentId={}", documentId);
      vectorStore.delete(filterExpression);
      log.info("Document vectors deleted successfully. documentId={}", documentId);

    } catch (Exception exception) {
      log.error(
          "Failed to delete document vectors. " + "documentId={}, message={}",
          documentId,
          exception.getMessage(),
          exception);

      throw exception;
    }
  }

  private List<Document> extractDocuments(MultipartFile file) throws IOException {

    if (file == null || file.isEmpty()) {
      log.error("Document file is null or empty.");
      throw new IllegalArgumentException("Document file is empty.");
    }

    String contentType = file.getContentType();

    log.info(
        "Extracting document. fileName={}, contentType={}, size={}",
        file.getOriginalFilename(),
        contentType,
        file.getSize());

    if ("application/pdf".equalsIgnoreCase(contentType)) {

      return readPdf(file);
    }

    log.error(
        "Unsupported document type. fileName={}, contentType={}",
        file.getOriginalFilename(),
        contentType);

    throw new IllegalArgumentException("Unsupported document type: " + contentType);
  }

  private List<Document> readPdf(MultipartFile file) throws IOException {

    try {

      log.info("Reading PDF document. fileName={}", file.getOriginalFilename());

      DocumentReader reader = new PagePdfDocumentReader(file.getResource());

      List<Document> documents = reader.get();

      log.info(
          "PDF reading completed. fileName={}, documents={}",
          file.getOriginalFilename(),
          documents != null ? documents.size() : 0);

      return documents;

    } catch (Exception exception) {

      log.error(
          "Unexpected exception while reading PDF. " + "fileName={}, exceptionType={}, message={}",
          file.getOriginalFilename(),
          exception.getClass().getName(),
          exception.getMessage(),
          exception);

      throw exception;
    }
  }
}
