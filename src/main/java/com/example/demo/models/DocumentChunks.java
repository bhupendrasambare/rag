/**
 * author @bhupendrasambare
 * Date   :10/07/26
 * Time   :11:50 pm
 * Project:rag
 **/
package com.example.demo.models;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Table(name = "document_chunks")
public class DocumentChunks {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "document_id")
    private String documentId;

    @Column(name = "chunk_index")
    private String chunkIndex;

    @Column(name = "content")
    private String content;

    @Column(name = "token_count")
    private String tokenCount;

    @Column(name = "page_number")
    private String pageNumber;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
