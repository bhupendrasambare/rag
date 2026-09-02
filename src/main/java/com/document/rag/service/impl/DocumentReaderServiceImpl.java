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

import com.document.rag.service.DocumentReaderService;
import java.io.IOException;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.document.DocumentReader;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class DocumentReaderServiceImpl implements DocumentReaderService {

  @Override
  public List<Document> read(Resource resource) throws IOException {

    if (resource == null || !resource.exists()) {

      throw new IOException("Document resource does not exist.");
    }

    if (!resource.isReadable()) {

      throw new IOException("Document resource is not readable.");
    }

    try {

      log.info("Reading PDF document. filename={}", resource.getFilename());

      DocumentReader reader = new PagePdfDocumentReader(resource);

      List<Document> documents = reader.get();

      log.info(
          "PDF reading completed. filename={}, pages={}",
          resource.getFilename(),
          documents != null ? documents.size() : 0);

      return documents;

    } catch (Exception exception) {

      log.error(
          "Failed to read PDF document. filename={}, exceptionType={}, message={}",
          resource.getFilename(),
          exception.getClass().getName(),
          exception.getMessage(),
          exception);

      throw exception;
    }
  }
}
