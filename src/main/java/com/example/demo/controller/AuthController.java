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
package com.example.demo.controller;

import com.example.demo.constants.Constants;
import com.example.demo.dto.request.LoginRequest;
import com.example.demo.dto.request.RefreshTokenRequest;
import com.example.demo.dto.request.SignupRequest;
import com.example.demo.dto.response.ApiResponse;
import com.example.demo.dto.response.ApiResponses;
import com.example.demo.dto.response.LoginResponse;
import com.example.demo.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {

  public final AuthService authService;

  public ResponseEntity<ApiResponse<LoginResponse>> signUp(
      @Valid @RequestBody SignupRequest request) {
    return ResponseEntity.ok(
        ApiResponses.success(Constants.SETUP_SUCCESSFULLY, authService.signUp(request)));
  }

  @PostMapping("/login")
  public ResponseEntity<ApiResponse<LoginResponse>> login(
      @Valid @RequestBody LoginRequest request) {

    return ResponseEntity.ok(
        ApiResponses.success(Constants.LOGIN_SUCCESSFULLY, authService.login(request)));
  }

  @PostMapping("/refresh")
  public ResponseEntity<ApiResponse<LoginResponse>> refreshToken(
      @Valid @RequestBody RefreshTokenRequest request) {

    return ResponseEntity.ok(
        ApiResponses.success(
            Constants.REFRESH_TOKEN_SUCCESSFULLY, authService.refreshToken(request)));
  }

  @PostMapping("/logout")
  public ResponseEntity<ApiResponse<?>> logout(@Valid @RequestBody RefreshTokenRequest request) {

    authService.logout(request.getRefreshToken());

    return ResponseEntity.ok(ApiResponses.success(Constants.LOGOUT_SUCCESSFULLY));
  }
}
