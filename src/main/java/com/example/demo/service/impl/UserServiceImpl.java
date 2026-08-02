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

import com.example.demo.dto.request.UpdateProfileRequest;
import com.example.demo.dto.response.UserProfileResponse;
import com.example.demo.exception.custom.NotFoundException;
import com.example.demo.mapper.UserMapper;
import com.example.demo.models.UserInfo;
import com.example.demo.repository.UserInfoRepository;
import com.example.demo.service.UserService;
import java.util.UUID;
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

    return userMapper.toProfileResponse(user);
  }

  @Override
  public UserProfileResponse updateProfile(UpdateProfileRequest request) {

    UserInfo user = getCurrentUser();

    user.setFirstName(request.getFirstName());
    user.setLastName(request.getLastName());
    user.setProfileImage(request.getProfileImage());

    user = userRepository.save(user);

    return userMapper.toProfileResponse(user);
  }

  private UserInfo getCurrentUser() {

    Authentication authentication =
            SecurityContextHolder.getContext().getAuthentication();

    String email = authentication.getName();

    return userRepository.findByEmail(email)
            .orElseThrow(() ->
                    new NotFoundException("Authenticated user not found."));
  }
}
