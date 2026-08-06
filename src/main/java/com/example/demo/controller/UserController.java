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
import com.example.demo.dto.response.ApiResponse;
import com.example.demo.dto.response.ApiResponses;
import com.example.demo.dto.response.UserProfileResponse;
import com.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {

  private final UserService userService;

  public ResponseEntity<ApiResponse<UserProfileResponse>> getProfile(){
      return ResponseEntity.ok(ApiResponses.success(Constants.FETCH_USER_PROFILE_SUCCESSFULLY,this.userService.getProfile()));
  }


}
