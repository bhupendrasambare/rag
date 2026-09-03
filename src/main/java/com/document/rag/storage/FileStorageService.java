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
import com.document.rag.exception.custom.InvalidFileException;
import com.document.rag.exception.custom.InvalidFileExtensionException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
public class FileStorageService {

  @Value("${app.storage.document-path:storage/documents}")
  private String documentStoragePath;

  public String save(UUID documentId, MultipartFile file) {

    validateDocumentId(documentId);
    validateFile(file);

    try {

      Path storageDirectory = getStorageDirectory();

      Files.createDirectories(storageDirectory);

      String extension = getExtension(file.getOriginalFilename());

      String physicalFileName = documentId + extension;

      Path targetPath = storageDirectory.resolve(physicalFileName).normalize();

      validatePath(targetPath, storageDirectory);

      try (InputStream inputStream = file.getInputStream()) {

        Files.copy(inputStream, targetPath, StandardCopyOption.REPLACE_EXISTING);
      }

      log.info("Document file stored successfully. documentId={}, path={}", documentId, targetPath);

      return targetPath.toString();

    } catch (IOException exception) {

      log.error(
          "Failed to store document file. documentId={}, fileName={}, message={}",
          documentId,
          file.getOriginalFilename(),
          exception.getMessage(),
          exception);

      throw new FileStorageException("Unable to store the uploaded file.", exception);
    }
  }

  public Path getFile(UUID documentId, String originalFileName) {

    validateDocumentId(documentId);

    Path storageDirectory = getStorageDirectory();

    String extension = getExtension(originalFileName);

    Path filePath = storageDirectory.resolve(documentId + extension).normalize();

    validatePath(filePath, storageDirectory);

    if (!Files.exists(filePath) || !Files.isRegularFile(filePath)) {

      log.warn("Stored document file not found. documentId={}, path={}", documentId, filePath);

      throw new FileNotFoundException();
    }

    return filePath;
  }

  public Resource getResource(UUID documentId, String originalFileName) {

    Path filePath = getFile(documentId, originalFileName);

    return new FileSystemResource(filePath);
  }

  public InputStream getInputStream(UUID documentId, String originalFileName) {

    Path filePath = getFile(documentId, originalFileName);

    try {

      return Files.newInputStream(filePath);

    } catch (IOException exception) {

      log.error(
          "Unable to open stored document. documentId={}, path={}, message={}",
          documentId,
          filePath,
          exception.getMessage(),
          exception);

      throw new FileStorageException("Unable to read the stored file.", exception);
    }
  }

  public void delete(UUID documentId, String originalFileName) {

    if (documentId == null) {

      log.warn("Skipping file deletion because documentId is null.");

      return;
    }

    Path storageDirectory = getStorageDirectory();

    String extension = getExtension(originalFileName);

    Path filePath = storageDirectory.resolve(documentId + extension).normalize();

    validatePath(filePath, storageDirectory);

    try {

      boolean deleted = Files.deleteIfExists(filePath);

      if (deleted) {

        log.info(
            "Document file deleted successfully. documentId={}, path={}", documentId, filePath);

      } else {

        log.debug("Document file was already absent. documentId={}, path={}", documentId, filePath);
      }

    } catch (IOException exception) {

      log.error(
          "Failed to delete document file. documentId={}, path={}, message={}",
          documentId,
          filePath,
          exception.getMessage(),
          exception);

      throw new FileDeleteException();
    }
  }

  private Path getStorageDirectory() {

    return Paths.get(documentStoragePath).toAbsolutePath().normalize();
  }

  private String getExtension(String fileName) {

    if (!StringUtils.hasText(fileName)) {
      throw new InvalidFileException();
    }

    String cleanFileName;

    try {

      cleanFileName = Paths.get(fileName).getFileName().toString();

    } catch (Exception exception) {

      log.warn("Invalid file name received. fileName={}", fileName);

      throw new InvalidFileException();
    }

    if (!StringUtils.hasText(cleanFileName)) {
      throw new InvalidFileException();
    }

    int lastDot = cleanFileName.lastIndexOf('.');

    if (lastDot <= 0 || lastDot == cleanFileName.length() - 1) {

      throw new InvalidFileExtensionException();
    }

    String extension = cleanFileName.substring(lastDot).toLowerCase();

    if (!extension.matches("\\.[a-z0-9]{1,10}")) {

      throw new InvalidFileExtensionException();
    }

    return extension;
  }

  private void validatePath(Path targetPath, Path storageDirectory) {

    Path normalizedDirectory = storageDirectory.toAbsolutePath().normalize();

    Path normalizedTarget = targetPath.toAbsolutePath().normalize();

    if (!normalizedTarget.startsWith(normalizedDirectory)) {

      log.error(
          "Invalid file path detected. directory={}, target={}",
          normalizedDirectory,
          normalizedTarget);

      throw new FileStorageException("Invalid file path.");
    }
  }

  private void validateDocumentId(UUID documentId) {

    if (documentId == null) {

      throw new InvalidFileException();
    }
  }

  private void validateFile(MultipartFile file) {

    if (file == null || file.isEmpty()) {

      throw new InvalidFileException();
    }

    if (!StringUtils.hasText(file.getOriginalFilename())) {

      throw new InvalidFileException();
    }
  }
}
