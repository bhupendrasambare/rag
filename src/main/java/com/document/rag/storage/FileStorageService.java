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
package com.document.rag.storage;

import com.document.rag.exception.custom.FileDeleteException;
import com.document.rag.exception.custom.FileNotFoundException;
import com.document.rag.exception.custom.FileStorageException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class FileStorageService {

  @Value("${app.storage.document-path:storage/documents}")
  private String documentStoragePath;

  /**
   * Saves a document using the document UUID as the physical filename.
   *
   * <p>Example: storage/documents/{documentId}.pdf
   */
  public String save(UUID documentId, MultipartFile file) {

    if (documentId == null) {
      throw new FileStorageException("Document ID cannot be null.");
    }

    if (file == null || file.isEmpty()) {
      throw new FileStorageException("File cannot be empty.");
    }

    try {

      Path storageDirectory = getStorageDirectory();

      Files.createDirectories(storageDirectory);

      String extension = getExtension(file.getOriginalFilename());

      String fileName = documentId + extension;

      Path targetPath = storageDirectory.resolve(fileName).normalize();

      /*
       * Additional protection against path traversal.
       */
      if (!targetPath.getParent().equals(storageDirectory)) {
        throw new FileStorageException("Invalid file path.");
      }

      try (InputStream inputStream = file.getInputStream()) {

        Files.copy(inputStream, targetPath, StandardCopyOption.REPLACE_EXISTING);
      }

      return targetPath.toString();

    } catch (IOException exception) {

      throw new FileStorageException("Unable to store the uploaded file.", exception);
    }
  }

  /** Retrieves the physical path of a document. */
  public Path getFile(UUID documentId, String originalFileName) {

    if (documentId == null) {
      throw new FileNotFoundException();
    }

    String extension = getExtension(originalFileName);

    Path filePath = getStorageDirectory().resolve(documentId + extension).normalize();

    if (!filePath.getParent().equals(getStorageDirectory())) {
      throw new FileNotFoundException();
    }

    if (!Files.exists(filePath) || !Files.isRegularFile(filePath)) {
      throw new FileNotFoundException();
    }

    return filePath;
  }

  /** Reads the file as an InputStream. */
  public InputStream getInputStream(UUID documentId, String originalFileName) {

    Path filePath = getFile(documentId, originalFileName);

    try {

      return Files.newInputStream(filePath);

    } catch (IOException exception) {

      throw new FileStorageException("Unable to read the stored file.", exception);
    }
  }

  /** Deletes the physical document. */
  public void delete(UUID documentId, String originalFileName) {

    if (documentId == null) {
      return;
    }

    Path filePath = getFile(documentId, originalFileName);

    try {

      Files.deleteIfExists(filePath);

    } catch (IOException exception) {

      throw new FileDeleteException();
    }
  }

  private Path getStorageDirectory() {

    return Paths.get(documentStoragePath).toAbsolutePath().normalize();
  }

  private String getExtension(String fileName) {

    if (!StringUtils.hasText(fileName)) {
      return "";
    }

    String cleanFileName = Paths.get(fileName).getFileName().toString();

    int lastDot = cleanFileName.lastIndexOf('.');

    if (lastDot < 0) {
      return "";
    }

    return cleanFileName.substring(lastDot).toLowerCase();
  }
}
