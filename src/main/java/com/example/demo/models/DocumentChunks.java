/**
 * author @bhupendrasambare
 * Date   :10/07/26
 * Time   :11:50 pm
 * Project:rag
 **/
package com.example.demo.models;

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
@Table(name = "document_chunks")
public class DocumentChunks {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "document_id")
    private UUID documentId;

    @Column(name = "chunk_index")
    private Integer chunkIndex;

    @Column(name = "content")
    private String content;

    @Column(name = "token_count")
    private Integer tokenCount;

    @Column(name = "page_number")
    private Integer pageNumber;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
