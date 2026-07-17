/**
 * author @bhupendrasambare
 * Date   :11/07/26
 * Time   :10:33 pm
 * Project:rag
 **/
package com.example.demo.models;

import jakarta.persistence.*;

@Table(name = "document_char_reference")
public class DocumentChatReference {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "chat_message_id")
    private String chatMessageId;

    @Column(name = "document_id")
    private String documentId;

    @Column(name = "chunk_id")
    private String chunkId;

    @Column(name = "similarity_score")
    private Float similarityScore;
}
