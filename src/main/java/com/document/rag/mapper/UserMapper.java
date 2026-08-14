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
package com.document.rag.mapper;

import com.document.rag.dto.response.UserProfileResponse;
import com.document.rag.dto.response.UserResponse;
import com.document.rag.models.UserInfo;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

  public UserResponse toUserResponse(UserInfo user) {

    if (user == null) {
      return null;
    }

    return UserResponse.builder()
        .id(user.getId())
        .firstName(user.getFirstName())
        .lastName(user.getLastName())
        .email(user.getEmail())
        .profileImage(user.getProfileImage())
        .role(user.getRole())
        .build();
  }

  public UserProfileResponse toProfileResponse(UserInfo user) {

    if (user == null) {
      return null;
    }

    return UserProfileResponse.builder()
        .id(user.getId())
        .firstName(user.getFirstName())
        .lastName(user.getLastName())
        .email(user.getEmail())
        .profileImage(user.getProfileImage())
        .role(user.getRole())
        .active(user.getActive())
        .createdAt(user.getCreatedAt())
        .build();
  }
}
