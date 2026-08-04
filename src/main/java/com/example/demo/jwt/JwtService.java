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
package com.example.demo.jwt;

import com.example.demo.dto.response.CustomUserDetails;
import com.example.demo.properties.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.MacAlgorithm;
import jakarta.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.SecretKey;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JwtService {

  private final JwtProperties properties;
  private SecretKey secretKey;
  private final MacAlgorithm algorithm = Jwts.SIG.HS256;

  @PostConstruct
  public void initialize() {
    this.secretKey = Keys.hmacShaKeyFor(properties.getSecret().getBytes(StandardCharsets.UTF_8));
  }

  public String generateAccessToken(CustomUserDetails userDetails) {
    Map<String, Object> claims = new HashMap<>();
    claims.put("uid", userDetails.getId());
    claims.put("role", userDetails.getAuthorities().iterator().next().getAuthority());

    return buildToken(claims, userDetails.getUsername(), properties.getAccessTokenExpiration());
  }

  public String generateRefreshToken(CustomUserDetails userDetails) {
    Map<String, Object> claims = new HashMap<>();
    claims.put("type", "REFRESH");

    return buildToken(claims, userDetails.getUsername(), properties.getRefreshTokenExpiration());
  }

  private String buildToken(Map<String, Object> claims, String userName, long expiration) {
    Instant now = Instant.now();

    return Jwts.builder()
        .claims(claims)
        .subject(userName)
        .issuer(properties.getIssuer())
        .expiration(Date.from(now.plusMillis(expiration)))
        .signWith(secretKey, algorithm)
        .compact();
  }

  public boolean isTokenValid(String token, UserDetails userDetails) {
    try {
      String username = extractUsername(token);
      return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    } catch (JwtException ex) {
      return false;
    }
  }

  public boolean isTokenExpired(String token) {
    return extractExpiration(token).before(new Date());
  }

  public String extractUsername(String token) {
    return extractClaims(token).getSubject();
  }

  public Date extractExpiration(String token) {

    return extractClaims(token).getExpiration();
  }

  public String extractRole(String token) {

    return extractClaims(token).get("role", String.class);
  }

  public String extractTokenType(String token) {

    return extractClaims(token).get("type", String.class);
  }

  public Claims extractClaims(String token) {

    return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload();
  }

  public String extractUserId(String token) {

    return extractClaims(token).get("uid", String.class);
  }

  public long getAccessTokenExpiration() {
    return this.properties.getAccessTokenExpiration();
  }

  public long getRefreshTokenExpiration() {
    return this.properties.getRefreshTokenExpiration();
  }
}
