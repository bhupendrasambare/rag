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
package com.document.rag.mapper;

import com.document.rag.dto.response.DocumentResponse;
import com.document.rag.models.DocumentInfo;
import org.springframework.stereotype.Component;

@Component
public class DocumentMapper {

  public DocumentResponse toResponse(DocumentInfo document) {

    return DocumentResponse.builder()
        .id(document.getId())
        .fileName(document.getFileName())
        .originalFileName(document.getOriginalFileName())
        .fileSize(document.getFileSize())
        .status(document.getStatus())
        .createdAt(document.getCreatedAt())
        .updatedAt(document.getUpdatedAt())
        .build();
  }
}
