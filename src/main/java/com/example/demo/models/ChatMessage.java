/**
 * author @bhupendrasambare
 * Date   :11/07/26
 * Time   :10:33 pm
 * Project:rag
 **/
package com.example.demo.models;

import com.example.demo.constants.ChatRole;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Table(name = "chat_message")
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "chat_session_id")
    private String chatSessionId;

    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private ChatRole role;

    @Column(name = "message")
    private String message;

    @Column(name = "token_usage")
    private Long tokenUsage;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
