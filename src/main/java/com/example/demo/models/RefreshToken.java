/**
 * author @bhupendrasambare
 * Date   :10/07/26
 * Time   :10:54 pm
 * Project:rag
 **/
package com.example.demo.models;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Table(name = "refresh_token")
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "token")
    private String token;

    @Column(name = "revoked")
    private Boolean revoked;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "expired_at")
    private LocalDateTime expiredAt;
}
