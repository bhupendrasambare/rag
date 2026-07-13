/**
 * author @bhupendrasambare
 * Date   :10/07/26
 * Time   :10:53 pm
 * Project:rag
 **/
package com.example.demo.models;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Table(name = "user_info")
public class UserInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "email")
    private String email;

    @Column(name = "password_hash")
    private String passwordHash;

    @Column(name = "profile_image")
    private String profileImage;

    @Column(name = "role")
    private String role;

    @Column(name = "active")
    private Boolean active;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "created_by")
    private LocalDateTime createdBy;

    @Column(name = "updated_by")
    private LocalDateTime updatedBy;
}
