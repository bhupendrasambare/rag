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

import com.document.rag.dto.request.DocumentResponse;
import com.document.rag.dto.request.DocumentStatusResponse;
import com.document.rag.dto.request.UploadDocumentRequest;
import com.document.rag.service.DocumentService;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class DocumentServiceImpl implements DocumentService {
  @Override
  public void uploadDocument(UploadDocumentRequest request) {}

  @Override
  public DocumentResponse uploadFile(UploadDocumentRequest request) {
    return null;
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
}
