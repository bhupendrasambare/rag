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
package com.document.rag.service;

import com.document.rag.dto.request.UploadDocumentRequest;
import com.document.rag.dto.response.DocumentResponse;
import com.document.rag.dto.response.DocumentStatusResponse;
import java.util.UUID;

import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DocumentService {

  public void uploadDocument(UploadDocumentRequest request);

  DocumentResponse uploadFile(UploadDocumentRequest request);

  Page<DocumentResponse> getDocuments(Pageable pageable);

  DocumentResponse getDocument(UUID id);

  DocumentStatusResponse getDocumentStatus(UUID id);

  Resource downloadDocument(UUID id);

  void deleteDocument(UUID id);
}
