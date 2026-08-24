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
package com.document.rag.controller;

import com.document.rag.constants.Constants;
import com.document.rag.dto.request.UploadDocumentRequest;
import com.document.rag.dto.response.ApiResponse;
import com.document.rag.dto.response.ApiResponses;
import com.document.rag.dto.response.DocumentResponse;
import com.document.rag.dto.response.DocumentStatusResponse;
import com.document.rag.service.DocumentService;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/document")
public class DocumentController {

  private final DocumentService documentService;

  @PostMapping
  public ResponseEntity<ApiResponse<DocumentResponse>> uploadFile(
      @Valid @ModelAttribute UploadDocumentRequest request) {

    return ResponseEntity.ok(
        ApiResponses.success(
            Constants.DOCUMENT_UPLOADED_SUCCESSFULLY, documentService.uploadFile(request)));
  }

  @GetMapping
  public ResponseEntity<ApiResponse<Page<DocumentResponse>>> getDocuments(Pageable pageable) {

    return ResponseEntity.ok(
        ApiResponses.success(
            Constants.FETCH_DOCUMENTS_SUCCESSFULLY, documentService.getDocuments(pageable)));
  }

  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<DocumentResponse>> getDocument(@PathVariable UUID id) {

    return ResponseEntity.ok(
        ApiResponses.success(
            Constants.FETCH_DOCUMENT_SUCCESSFULLY, documentService.getDocument(id)));
  }

  @GetMapping("/{id}/status")
  public ResponseEntity<ApiResponse<DocumentStatusResponse>> getDocumentStatus(
      @PathVariable UUID id) {

    return ResponseEntity.ok(
        ApiResponses.success(
            Constants.FETCH_DOCUMENT_STATUS_SUCCESSFULLY, documentService.getDocumentStatus(id)));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<ApiResponse<?>> deleteDocument(@PathVariable UUID id) {

    documentService.deleteDocument(id);

    return ResponseEntity.ok(ApiResponses.success(Constants.DELETE_DOCUMENT_SUCCESSFULLY));
  }

  // TODO : implement download api
}
