/**
 * author @bhupendrasambare
 * Date   :11/07/26
 * Time   :10:34 pm
 * Project:rag
 **/
package com.example.demo.models;

import com.example.demo.constants.EventStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Table(name = "processing_event")
public class ProcessingEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "document_id")
    private String documentId;

    @Column(name = "event_type")
    private String eventType;

    @Column(name = "status")
    private EventStatus status;

    @Column(name = "error_message")
    private String errorMessage;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;
}
