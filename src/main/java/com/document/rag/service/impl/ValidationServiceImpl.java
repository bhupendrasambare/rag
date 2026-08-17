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

import com.document.rag.exception.custom.*;
import com.document.rag.models.RefreshToken;
import com.document.rag.repository.RefreshTokenRepository;
import com.document.rag.repository.UserInfoRepository;
import com.document.rag.service.ValidationService;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class ValidationServiceImpl implements ValidationService {

  private final UserInfoRepository userInfoRepository;
  private final RefreshTokenRepository refreshTokenRepository;

  @Override
  public void validateUniqueEmail(String email, UUID userId) {

    if (!StringUtils.hasText(email)) {
      return;
    }

    boolean emailExists;

    if (userId == null) {
      emailExists = this.userInfoRepository.existsByEmail(email);
    } else {
      emailExists = this.userInfoRepository.existsByEmailAndIdNot(email, userId);
    }

    if (emailExists) {
      throw new DuplicateEmailException();
    }
  }

  @Override
  public void validatePassword(String password, String confirmPassword) {

    if (!StringUtils.hasText(password)) {
      throw new EmptyPasswordException();
    }

    if (!StringUtils.hasText(confirmPassword)) {
      throw new EmptyConfirmPasswordException();
    }

    if (!password.equals(confirmPassword)) {
      throw new ConfirmPasswordNotMatchedException();
    }
  }

  @Override
  public void validateRefreshToken(String refreshToken, UUID userId) {

    if (!StringUtils.hasText(refreshToken)) {
      throw new RefreshTokenNotFoundException();
    }

    RefreshToken token =
        this.refreshTokenRepository
            .findByToken(refreshToken)
            .orElseThrow(InvalidRefreshTokenException::new);

    if (Boolean.TRUE.equals(token.getRevoked())) {
      throw new InvalidRefreshTokenException();
    }

    if (token.getExpiredAt().isBefore(LocalDateTime.now())) {
      throw new InvalidRefreshTokenException();
    }

    if (userId != null && !token.getUserId().equals(userId)) {
      throw new InvalidRefreshTokenException();
    }
  }
}
