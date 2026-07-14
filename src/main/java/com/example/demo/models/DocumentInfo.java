/**
 * author @bhupendrasambare
 * Date   :10/07/26
 * Time   :11:49 pm
 * Project:rag
 **/
package com.example.demo.models;

import com.example.demo.constants.DocumentStatus;
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

    @Column(name = "checksum")
    private String checksum;

    @Column(name = "status")
    private DocumentStatus status;

    @Column(name = "total_pages")
    private String totalPages;

    @Column(name = "chunk_count")
    private Long chunkCount;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;
}
