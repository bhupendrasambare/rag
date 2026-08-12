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
package com.example.demo.exception;

import com.example.demo.dto.response.ApiResponse;
import com.example.demo.dto.response.ApiResponses;
import com.example.demo.exception.custom.BaseException;
import jakarta.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(BaseException.class)
  public ResponseEntity<ApiResponse<Object>> handleBaseException(BaseException ex) {

    HttpStatus status = resolveHttpStatus(ex.getErrorCode());

    return ResponseEntity.status(status)
            .body(
                    ApiResponses.failure(
                            ex.getErrorCode(),
                            ex.getMessage()));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiResponse<Object>> handleMethodValidation(
          MethodArgumentNotValidException ex) {

    Map<String, String> errors = new HashMap<>();

    for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
      errors.put(fieldError.getField(), fieldError.getDefaultMessage());
    }

    return ResponseEntity.badRequest()
            .body(
                    ApiResponses.validation(
                            ErrorCode.VALIDATION_ERROR,
                            "Validation failed",
                            errors));
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<ApiResponse<Object>> handleConstraint(
          ConstraintViolationException ex) {

    Map<String, String> errors = new HashMap<>();

    ex.getConstraintViolations()
            .forEach(
                    violation ->
                            errors.put(
                                    violation.getPropertyPath().toString(),
                                    violation.getMessage()));

    return ResponseEntity.badRequest()
            .body(
                    ApiResponses.validation(
                            ErrorCode.VALIDATION_ERROR,
                            "Validation failed",
                            errors));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiResponse<Object>> handleException(Exception ex) {

    ex.printStackTrace();

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(
                    ApiResponses.failure(
                            ErrorCode.INTERNAL_SERVER_ERROR,
                            "Something went wrong."));
  }

  private HttpStatus resolveHttpStatus(ErrorCode errorCode) {

    return switch (errorCode) {

      case INVALID_CREDENTIALS,
              REFRESH_TOKEN_INVALID,
              INVALID_TOKEN,
              TOKEN_EXPIRED ->
              HttpStatus.UNAUTHORIZED;

      case UNAUTHORIZED ->
              HttpStatus.UNAUTHORIZED;

      case ACCESS_DENIED ->
              HttpStatus.FORBIDDEN;

      case USER_NOT_FOUND,
              DOCUMENT_NOT_FOUND ->
              HttpStatus.NOT_FOUND;

      case USER_ALREADY_EXISTS ->
              HttpStatus.CONFLICT;

      case VALIDATION_ERROR ->
              HttpStatus.BAD_REQUEST;

      default ->
              HttpStatus.BAD_REQUEST;
    };
  }
}
