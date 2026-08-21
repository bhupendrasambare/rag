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
import com.document.rag.dto.request.DocumentResponse;
import com.document.rag.dto.request.DocumentStatusResponse;
import com.document.rag.dto.request.UploadDocumentRequest;
import com.document.rag.dto.response.UserProfileResponse;
import com.document.rag.exception.custom.DocumentFileRequiredException;
import com.document.rag.exception.custom.DocumentUploadException;
import com.document.rag.exception.custom.EmptyDocumentFileException;
import com.document.rag.mapper.DocumentMapper;
import com.document.rag.models.DocumentInfo;
import com.document.rag.repository.DocumentInfoRepository;
import com.document.rag.service.DocumentProcessingService;
import com.document.rag.service.DocumentService;
import com.document.rag.service.UserService;
import io.jsonwebtoken.io.IOException;
import java.time.LocalDateTime;
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
    this.validateUploadRequest(request);

    UserProfileResponse userInfo = this.userService.getProfile();
    try {
      MultipartFile file = request.getFile();

      DocumentInfo documentInfo = new DocumentInfo();
      documentInfo.setUserId(userInfo.getId());
      documentInfo.setFileName(request.getFileName());
      documentInfo.setOriginalFileName(file.getOriginalFilename());
      documentInfo.setFileSize(file.getSize());
      documentInfo.setStatus(DocumentStatus.UPLOADING);
      documentInfo.setCreatedAt(LocalDateTime.now());

      documentInfo = this.documentInfoRepository.save(documentInfo);

      return documentMapper.toResponse(documentInfo);

    } catch (IOException exception) {
      throw new DocumentUploadException();
    }
  }

  @Override
  public Page<DocumentResponse> getDocuments(Pageable pageable) {
    return null;
  }

  @Override
  public DocumentResponse getDocument(UUID id) {
    return null;
  }

  @Override
  public DocumentStatusResponse getDocumentStatus(UUID id) {
    return null;
  }

  @Override
  public void deleteDocument(UUID id) {}

  private void validateUploadRequest(UploadDocumentRequest request) {

    if (request == null || request.getFile() == null) {

      throw new DocumentFileRequiredException();
    }

    if (request.getFile().isEmpty()) {

      throw new EmptyDocumentFileException();
    }
  }
}
