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
package com.document.rag.dto.response;

import com.document.rag.exception.ErrorCode;
import java.time.LocalDateTime;
import java.util.Map;

public class ApiResponses {

  private ApiResponses() {}

  public static <T> ApiResponse<T> success(String message, T data) {
    return ApiResponse.<T>builder()
        .success(true)
        .message(message)
        .data(data)
        .timestamp(LocalDateTime.now())
        .build();
  }

  public static ApiResponse<Object> success(String message) {
    return ApiResponse.builder()
        .success(true)
        .message(message)
        .timestamp(LocalDateTime.now())
        .build();
  }

  public static ApiResponse<Object> failure(ErrorCode errorCode, String message) {
    return ApiResponse.builder()
        .success(false)
        .errorCode(errorCode)
        .message(message)
        .timestamp(LocalDateTime.now())
        .build();
  }

  public static ApiResponse<Object> validation(
      ErrorCode errorCode, String message, Map<String, String> errors) {

    return ApiResponse.builder()
        .success(false)
        .errorCode(errorCode)
        .message(message)
        .errors(errors)
        .timestamp(LocalDateTime.now())
        .build();
  }
}
