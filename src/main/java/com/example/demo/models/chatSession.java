/**
 * author @bhupendrasambare
 * Date   :11/07/26
 * Time   :10:33 pm
 * Project:rag
 **/
package com.example.demo.models;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Table(name = "chat_session")
public class chatSession {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "topic")
    private String topic;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
