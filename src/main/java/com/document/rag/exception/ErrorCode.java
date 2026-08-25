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
package com.document.rag.exception;

public enum ErrorCode {

  // Authentication
  INVALID_CREDENTIALS,
  UNAUTHORIZED,
  ACCESS_DENIED,
  INVALID_TOKEN,
  TOKEN_EXPIRED,
  REFRESH_TOKEN_INVALID,

  // User
  USER_NOT_FOUND,
  USER_ALREADY_EXISTS,

  // Validation
  VALIDATION_ERROR,

  // Documents
  DOCUMENT_NOT_FOUND,
  DOCUMENT_UPLOAD_FAILED,
  DOCUMENT_PROCESSING_FAILED,

  // Generic
  BUSINESS_ERROR,
  DUPLICATE_EMAIL,
  REFRESH_TOKEN_EXPIRED,
  REFRESH_TOKEN_NOT_FOUND,
  REFRESH_TOKEN_REVOKED,
  EMPTY_PASSWORD,
  CONFIRTM_PASSWORD_NOT_MATCHED,
  EMPTY_CONFIRM_PASSWORD,
  INVALID_REFRESH_TOKEN,
  EMPTY_DOCUMENT_FILE,
  DOCUMENT_FILE_REQUIRED,
  DOCUMENT_INFO_NOT_FOUND,
  EMPTY_ID,
    DOCUMENT_NOT_ENOUGH_TEXT, DOCUMENT_NOT_READABLE, INTERNAL_SERVER_ERROR
}
