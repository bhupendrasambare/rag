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
import com.document.rag.dto.request.UploadDocumentRequest;
import com.document.rag.dto.response.DocumentResponse;
import com.document.rag.dto.response.DocumentStatusResponse;
import com.document.rag.dto.response.UserProfileResponse;
import com.document.rag.exception.custom.DocumentFileRequiredException;
import com.document.rag.exception.custom.DocumentInfoNotFoundException;
import com.document.rag.exception.custom.DocumentUploadException;
import com.document.rag.exception.custom.EmptyDocumentFileException;
import com.document.rag.exception.custom.EmptyIdException;
import com.document.rag.mapper.DocumentMapper;
import com.document.rag.models.DocumentInfo;
import com.document.rag.repository.DocumentInfoRepository;
import com.document.rag.service.DocumentProcessingService;
import com.document.rag.service.DocumentService;
import com.document.rag.service.UserService;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class DocumentServiceImpl implements DocumentService {

  private final DocumentInfoRepository documentInfoRepository;
  private final DocumentMapper documentMapper;
  private final UserService userService;
  private final DocumentProcessingService documentProcessingService;

  @Override
  @Transactional
  public void uploadDocument(UploadDocumentRequest request) {

    this.uploadFile(request);
  }

  @Override
  @Transactional
  public DocumentResponse uploadFile(UploadDocumentRequest request) {

    validateUploadRequest(request);

    UserProfileResponse userInfo = this.userService.getProfile();

    MultipartFile file = request.getFile();

    try {

      DocumentInfo documentInfo = new DocumentInfo();

      documentInfo.setUserId(userInfo.getId());

      /*
       * If request.getFileName() is supplied by frontend,
       * use it. Otherwise use the actual uploaded filename.
       */
      String fileName = request.getFileName();

      if (fileName == null || fileName.isBlank()) {
        fileName = file.getOriginalFilename();
      }

      documentInfo.setFileName(fileName);

      documentInfo.setOriginalFileName(file.getOriginalFilename());

      documentInfo.setFileSize(file.getSize());

      documentInfo.setStatus(DocumentStatus.UPLOADING);

      documentInfo.setDeleted(false);

      documentInfo.setCreatedAt(LocalDateTime.now());

      /*
       * Save metadata first.
       *
       * This gives us the document UUID which will be used
       * as metadata in PGVector.
       */
      documentInfo = this.documentInfoRepository.save(documentInfo);

      /*
       * Process:
       *
       * PDF
       *   ↓
       * extract text
       *   ↓
       * split chunks
       *   ↓
       * Ollama embeddings
       *   ↓
       * PGVector
       */
      this.documentProcessingService.process(documentInfo, file);

      /*
       * Re-fetch because processing may have changed
       * the status.
       */
      documentInfo =
          this.documentInfoRepository
              .findById(documentInfo.getId())
              .orElseThrow(DocumentInfoNotFoundException::new);

      return this.documentMapper.toResponse(documentInfo);

    } catch (DocumentInfoNotFoundException exception) {

      throw exception;

    } catch (Exception exception) {

      throw new DocumentUploadException();
    }
  }

  @Override
  @Transactional(readOnly = true)
  public Page<DocumentResponse> getDocuments(Pageable pageable) {

    UserProfileResponse userInfo = this.userService.getProfile();

    Page<DocumentInfo> documentList =
        this.documentInfoRepository.findAllByUserId(userInfo.getId(), pageable);

    return documentList.map(this.documentMapper::toResponse);
  }

  @Override
  @Transactional(readOnly = true)
  public DocumentResponse getDocument(UUID id) {

    DocumentInfo documentInfo = this.getDocumentInfo(id, false);

    return this.documentMapper.toResponse(documentInfo);
  }

  @Override
  @Transactional(readOnly = true)
  public DocumentStatusResponse getDocumentStatus(UUID id) {

    DocumentInfo documentInfo = this.getDocumentInfo(id, false);

    return new DocumentStatusResponse(documentInfo.getStatus());
  }

  @Override
  @Transactional
  public void deleteDocument(UUID id) {

    DocumentInfo documentInfo = this.getDocumentInfo(id, false);

    /*
     * Delete vectors first.
     *
     * If vector deletion fails, we don't mark the
     * document as deleted.
     */
    this.documentProcessingService.deleteVectors(documentInfo.getId());

    /*
     * Soft delete.
     */
    documentInfo.setDeleted(true);

    documentInfo.setUpdatedAt(LocalDateTime.now());

    this.documentInfoRepository.save(documentInfo);
  }

  private void validateUploadRequest(UploadDocumentRequest request) {

    if (request == null || request.getFile() == null) {

      throw new DocumentFileRequiredException();
    }

    if (request.getFile().isEmpty()) {

      throw new EmptyDocumentFileException();
    }
  }

  private DocumentInfo getDocumentInfo(UUID id, boolean showDeleted) {

    if (id == null) {
      throw new EmptyIdException();
    }

    UUID userId = this.userService.getProfile().getId();

    Optional<DocumentInfo> info = this.documentInfoRepository.findByIdAndUserId(id, userId);

    if (info.isEmpty()) {

      throw new DocumentInfoNotFoundException();
    }

    DocumentInfo documentInfo = info.get();

    if (!showDeleted && Boolean.TRUE.equals(documentInfo.getDeleted())) {

      throw new DocumentInfoNotFoundException();
    }

    return documentInfo;
  }
}
