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
import com.document.rag.dto.request.UpdateProfileRequest;
import com.document.rag.dto.response.ApiResponse;
import com.document.rag.dto.response.ApiResponses;
import com.document.rag.dto.response.UserProfileResponse;
import com.document.rag.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/user")
public class UserController {

  private final UserService userService;

  @GetMapping("/profile")
  public ResponseEntity<ApiResponse<UserProfileResponse>> getProfile() {

    return ResponseEntity.ok(
        ApiResponses.success(Constants.FETCH_USER_PROFILE_SUCCESSFULLY, userService.getProfile()));
  }

  @PutMapping("/profile")
  public ResponseEntity<ApiResponse<UserProfileResponse>> updateProfile(
      @Valid @RequestBody UpdateProfileRequest request) {

    return ResponseEntity.ok(
        ApiResponses.success(
            Constants.UPDATE_USER_PROFILE_SUCCESSFULLY, userService.updateProfile(request)));
  }
}
