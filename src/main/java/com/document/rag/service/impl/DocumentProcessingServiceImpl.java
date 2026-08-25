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
import org.springframework.ai.document.Document;
import org.springframework.ai.document.DocumentReader;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class DocumentProcessingServiceImpl implements DocumentProcessingService {

  private final DocumentInfoRepository documentRepository;

  private final VectorStore vectorStore;

  @Override
  @Transactional
  public void process(DocumentInfo documentInfo, MultipartFile file) throws IOException {

    try {
      documentInfo.setStatus(DocumentStatus.PROCESSING);
      documentInfo = documentRepository.save(documentInfo);

      /*
       * --------------------------------------------------
       * 2. Extract document text
       * --------------------------------------------------
       */
      List<Document> documents = extractDocuments(file);

      if (documents != null && documents.isEmpty()) {

        TokenTextSplitter splitter =
                TokenTextSplitter.builder()
                        .withChunkSize(800)
                        .withMinChunkSizeChars(350)
                        .withMinChunkLengthToEmbed(5)
                        .build();

        List<Document> chunks = splitter.apply(documents);

        if (chunks.isEmpty()) {
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

        vectorStore.add(chunks);
        documentInfo.setStatus(DocumentStatus.COMPLETED);
      }else{
        throw new DocumentNotReadableException();
      }

    } catch (Exception exception) {
      documentInfo.setStatus(DocumentStatus.FAILED);
      if (exception instanceof IOException ioException) {
        throw ioException;
      }

      throw new DocumentProcessingFailedException();
    } finally {
      documentInfo.setUpdatedAt(LocalDateTime.now());
      documentRepository.save(documentInfo);
    }
  }

  @Override
  public void deleteVectors(UUID documentId) {

    if (documentId == null) {
      return;
    }
    String filterExpression = "documentId == '" + documentId + "'";

    vectorStore.delete(filterExpression);
  }

  private List<Document> extractDocuments(MultipartFile file) throws IOException {

    if (file == null || file.isEmpty()) {

      throw new IllegalArgumentException("Document file is empty.");
    }

    String contentType = file.getContentType();
    if ("application/pdf".equalsIgnoreCase(contentType)) {

      return readPdf(file);
    }

    throw new IllegalArgumentException("Unsupported document type: " + contentType);
  }

  private List<Document> readPdf(MultipartFile file) throws IOException {
    DocumentReader reader = new PagePdfDocumentReader(file.getResource());

    return reader.get();
  }
}
