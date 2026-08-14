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

import com.document.rag.dto.request.UpdateProfileRequest;
import com.document.rag.dto.response.UserProfileResponse;
import com.document.rag.exception.custom.UserNotFoundException;
import com.document.rag.mapper.UserMapper;
import com.document.rag.models.UserInfo;
import com.document.rag.repository.UserInfoRepository;
import com.document.rag.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

  private final UserInfoRepository userRepository;
  private final UserMapper userMapper;

  @Override
  public UserProfileResponse getProfile() {

    UserInfo user = getCurrentUser();

    return this.userMapper.toProfileResponse(user);
  }

  @Override
  public UserProfileResponse updateProfile(UpdateProfileRequest request) {

    UserInfo user = getCurrentUser();

    user.setFirstName(request.getFirstName());
    user.setLastName(request.getLastName());
    user.setProfileImage(request.getProfileImage());

    user = this.userRepository.save(user);

    return this.userMapper.toProfileResponse(user);
  }

  private UserInfo getCurrentUser() {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    String email = authentication.getName();

    return this.userRepository.findByEmail(email).orElseThrow(UserNotFoundException::new);
  }
}
