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
package com.example.demo.service.impl;

import com.example.demo.constants.UserRole;
import com.example.demo.dto.request.LoginRequest;
import com.example.demo.dto.request.RefreshTokenRequest;
import com.example.demo.dto.request.SignupRequest;
import com.example.demo.dto.response.CustomUserDetails;
import com.example.demo.dto.response.LoginResponse;
import com.example.demo.exception.custom.BusinessException;
import com.example.demo.jwt.JwtService;
import com.example.demo.mapper.UserMapper;
import com.example.demo.models.RefreshToken;
import com.example.demo.models.UserInfo;
import com.example.demo.repository.RefreshTokenRepository;
import com.example.demo.repository.UserInfoRepository;
import com.example.demo.service.AuthService;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

  private final UserInfoRepository userRepository;
  private final RefreshTokenRepository refreshTokenRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;
  private final AuthenticationManager authenticationManager;
  private final UserMapper userMapper;

  @Override
  public LoginResponse signUp(SignupRequest request) {

    if (this.userRepository.existsByEmail(request.getEmail())) {
      throw new BusinessException("Email already registered.");
    }

    UserInfo user = new UserInfo();

    user.setFirstName(request.getFirstName());
    user.setLastName(request.getLastName());
    user.setEmail(request.getEmail());
    user.setPasswordHash(this.passwordEncoder.encode(request.getPassword()));
    user.setRole(UserRole.USER);
    user.setActive(true);

    user = this.userRepository.save(user);
    CustomUserDetails customUserDetails = new CustomUserDetails(user);
    String accessToken = this.jwtService.generateAccessToken(customUserDetails);
    String refreshToken = this.jwtService.generateRefreshToken(customUserDetails);

    saveRefreshToken(user, refreshToken);

    return buildLoginResponse(user, accessToken, refreshToken);
  }

  @Override
  public LoginResponse login(LoginRequest request) {

    this.authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

    UserInfo user =
        this.userRepository
            .findByEmail(request.getEmail())
            .orElseThrow(() -> new BusinessException("Invalid credentials."));

    this.refreshTokenRepository.deleteByUserId(user.getId());
    CustomUserDetails customUserDetails = new CustomUserDetails(user);
    String accessToken = this.jwtService.generateAccessToken(customUserDetails);
    String refreshToken = this.jwtService.generateRefreshToken(customUserDetails);

    saveRefreshToken(user, refreshToken);

    return buildLoginResponse(user, accessToken, refreshToken);
  }

  @Override
  public LoginResponse refreshToken(RefreshTokenRequest request) {

    RefreshToken token =
        this.refreshTokenRepository
            .findByToken(request.getRefreshToken())
            .orElseThrow(() -> new BusinessException("Refresh token not found."));

    if (Boolean.TRUE.equals(token.getRevoked())) {
      throw new BusinessException("Refresh token revoked.");
    }

    if (token.getExpiredAt().isBefore(LocalDateTime.now())) {
      throw new BusinessException("Refresh token expired.");
    }

    UserInfo user =
        this.userRepository
            .findById(token.getUserId())
            .orElseThrow(() -> new BusinessException("User not found."));

    this.refreshTokenRepository.deleteByToken(token.getToken());
    CustomUserDetails customUserDetails = new CustomUserDetails(user);
    String accessToken = this.jwtService.generateAccessToken(customUserDetails);
    String refreshToken = this.jwtService.generateRefreshToken(customUserDetails);

    saveRefreshToken(user, refreshToken);

    return buildLoginResponse(user, accessToken, refreshToken);
  }

  @Override
  public void logout(String refreshToken) {

    this.refreshTokenRepository
        .findByToken(refreshToken)
        .ifPresent(
            token -> {
              token.setRevoked(true);
              this.refreshTokenRepository.save(token);
            });
  }

  private void saveRefreshToken(UserInfo user, String token) {

    RefreshToken refreshToken = new RefreshToken();

    refreshToken.setUserId(user.getId());
    refreshToken.setToken(token);
    refreshToken.setRevoked(false);
    refreshToken.setCreatedAt(LocalDateTime.now());
    refreshToken.setExpiredAt(LocalDateTime.now().plusDays(7));

    this.refreshTokenRepository.save(refreshToken);
  }

  private LoginResponse buildLoginResponse(UserInfo user, String accessToken, String refreshToken) {

    return LoginResponse.builder()
        .accessToken(accessToken)
        .refreshToken(refreshToken)
        .expiresIn(this.jwtService.getAccessTokenExpiration())
        .user(this.userMapper.toUserResponse(user))
        .build();
  }
}
