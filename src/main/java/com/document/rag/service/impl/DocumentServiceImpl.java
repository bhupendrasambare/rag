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
import com.document.rag.constants.FileType;
import com.document.rag.dto.request.UploadDocumentRequest;
import com.document.rag.dto.response.DocumentResponse;
import com.document.rag.dto.response.DocumentStatusResponse;
import com.document.rag.dto.response.UserProfileResponse;
import com.document.rag.exception.custom.DocumentFileRequiredException;
import com.document.rag.exception.custom.DocumentInfoNotFoundException;
import com.document.rag.exception.custom.DocumentUploadException;
import com.document.rag.exception.custom.EmptyIdException;
import com.document.rag.mapper.DocumentMapper;
import com.document.rag.models.DocumentInfo;
import com.document.rag.repository.DocumentInfoRepository;
import com.document.rag.service.DocumentProcessingService;
import com.document.rag.service.DocumentService;
import com.document.rag.service.UserService;
import com.document.rag.storage.FileStorageService;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentServiceImpl implements DocumentService {

  private final DocumentInfoRepository documentInfoRepository;
  private final DocumentMapper documentMapper;
  private final UserService userService;
  private final DocumentProcessingService documentProcessingService;
  private final FileStorageService fileStorageService;

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

      String fileName = request.getFileName();

      if (fileName == null || fileName.isBlank()) {
        fileName = file.getOriginalFilename();
      }

      documentInfo.setUserId(userInfo.getId());
      documentInfo.setFileName(fileName);
      documentInfo.setOriginalFileName(file.getOriginalFilename());
      documentInfo.setFileSize(file.getSize());
      documentInfo.setStatus(DocumentStatus.UPLOADING);
      documentInfo.setDeleted(false);
      documentInfo.setCreatedAt(LocalDateTime.now());
      documentInfo.setUpdatedAt(LocalDateTime.now());
      documentInfo.setEmbeddingModel("Internal");
      documentInfo.setFileType(FileType.PDF);

      documentInfo = this.documentInfoRepository.save(documentInfo);

      this.fileStorageService.save(documentInfo.getId(), request.getFile());
      this.documentProcessingService.process(documentInfo, file);

      documentInfo =
          this.documentInfoRepository
              .findById(documentInfo.getId())
              .orElseThrow(DocumentInfoNotFoundException::new);

      return this.documentMapper.toResponse(documentInfo);

    } catch (DocumentInfoNotFoundException exception) {
      log.error(
          "Document upload failed. fileName={}, errorCode={}, message={}",
          file != null ? file.getOriginalFilename() : null,
          exception.getErrorCode(),
          exception.getMessage(),
          exception);

      throw exception;

    } catch (Exception exception) {
      log.error(
          "Unexpected error while uploading document. fileName={}",
          file != null ? file.getOriginalFilename() : null,
          exception);
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
  @Transactional(readOnly = true)
  public Resource downloadDocument(UUID id) {
    DocumentInfo documentInfo = this.getDocumentInfo(id, false);

    try {

      return new FileSystemResource(
          this.fileStorageService.getFile(
              documentInfo.getId(), documentInfo.getOriginalFileName()));

    } catch (RuntimeException exception) {

      throw exception;
    }
  }

  @Override
  @Transactional
  public void deleteDocument(UUID id) {

    DocumentInfo documentInfo = this.getDocumentInfo(id, false);

    this.documentProcessingService.deleteVectors(documentInfo.getId());

    documentInfo.setDeleted(true);
    documentInfo.setUpdatedAt(LocalDateTime.now());

    this.documentInfoRepository.save(documentInfo);
  }

  private void validateUploadRequest(UploadDocumentRequest request) {

    if (request != null && request.getFile() != null && !request.getFile().isEmpty()) {
      return;
    }
    throw new DocumentFileRequiredException();
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
