/**
 * author @bhupendrasambare
 * Date   :24/07/26
 * Time   :8:53 am
 * Project:rag
 **/
package com.example.demo.jwt;

import com.example.demo.dto.response.CustomUserDetails;
import com.example.demo.properties.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.MacAlgorithm;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final JwtProperties properties;
    private SecretKey secretKey;
    private final MacAlgorithm algorithm = Jwts.SIG.HS256;

    @PostConstruct
    public void initialize(){
        this.secretKey = Keys.hmacShaKeyFor(
                properties.getSecret().getBytes(StandardCharsets.UTF_8)
        );
    }

    public String generateAccessToken(CustomUserDetails userDetails){
        Map<String, Object> claims = new HashMap<>();
        claims.put("uid", userDetails.getId());
        claims.put("role", userDetails.getAuthorities().iterator().next().getAuthority());

        return buildToken(claims,
                userDetails.getUsername(),
                properties.getAccessTokenExpiration());
    }

    public String generateRefreshToken(CustomUserDetails userDetails){
        Map<String, Object> claims = new HashMap<>();
        claims.put("type", "REFRESH");

        return buildToken(
                claims,
                userDetails.getUsername(),
                properties.getRefreshTokenExpiration()
        );
    }

    private String buildToken(Map<String, Object> claims,
                              String userName,
                              long expiration){
        Instant now = Instant.now();

        return Jwts.builder()
                .claims(claims)
                .subject(userName)
                .issuer(properties.getIssuer())
                .expiration(Date.from(now.plusMillis(expiration)))
                .signWith(secretKey, algorithm)
                .compact();
    }

    public boolean isTokenValid(String token, UserDetails userDetails){
        try {
            String username = extractUsername(token);
            return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
        }catch (JwtException ex){
            return false;
        }
    }

    public boolean isTokenExpired(String token){
        return extractExpiration(token).before(new Date());
    }

    public String extractUsername(String token){
        return extractClaims(token).getSubject();
    }

    public Date extractExpiration(String token) {

        return extractClaims(token)
                .getExpiration();
    }

    public String extractRole(String token) {

        return extractClaims(token)
                .get("role", String.class);
    }

    public String extractTokenType(String token) {

        return extractClaims(token)
                .get("type", String.class);
    }

    public Claims extractClaims(String token) {

        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String extractUserId(String token) {

        return extractClaims(token)
                .get("uid", String.class);
    }




}
