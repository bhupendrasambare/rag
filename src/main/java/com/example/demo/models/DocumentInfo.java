/**
 * author @bhupendrasambare
 * Date   :10/07/26
 * Time   :11:49 pm
 * Project:rag
 **/
package com.example.demo.models;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Table(name = "document_info")
public class DocumentInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "original_file_name")
    private String originalFileName;

    @Column(name = "file_type")
    private String fileType;

    @Column(name = "mime_type")
    private String mimeType;

    @Column(name = "file_size")
    private Double fileSize;

    @Column(name = "storage_path")
    private String storagePath;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
