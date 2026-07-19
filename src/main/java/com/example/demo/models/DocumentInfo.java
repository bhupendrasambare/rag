/**
 * author @bhupendrasambare
 * Date   :10/07/26
 * Time   :11:49 pm
 * Project:rag
 **/
package com.example.demo.models;

import com.example.demo.constants.DocumentStatus;
import com.example.demo.constants.FileType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(
        name = "document_info",
        indexes = {
                @Index(name="idx_document_user", columnList="user_id"),
                @Index(name="idx_document_status", columnList="status")
        }
)
public class DocumentInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "embedding_model", nullable = false, length = 100)
    private String embeddingModel;

    @Column(name = "chat_model", nullable = false, length = 100)
    private String chatModel;

    @Column(name = "file_name", nullable = false, length = 225)
    private String fileName;

    @Column(name = "original_file_name", nullable = false, length = 225)
    private String originalFileName;

    @Column(name = "file_type", nullable = false, length = 30)
    @Enumerated(EnumType.STRING)
    private FileType fileType;

    @Column(name = "mime_type", nullable = false, length = 100)
    private String mimeType;

    @Column(name = "file_size", nullable = false)
    private Long fileSize;

    @Column(name = "storage_path", nullable = false, columnDefinition = "TEXT")
    private String storagePath;

    @Column(name = "checksum", nullable = false, unique = true, length = 128)
    private String checksum;

    @Column(name = "status", nullable = false, length = 30)
    @Enumerated(EnumType.STRING)
    private DocumentStatus status;

    @Column(name = "total_pages")
    private Integer totalPages;

    @Column(name = "chunk_count")
    private Long chunkCount;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    @Version
    private Long version;
}
