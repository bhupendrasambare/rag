/**
 * author @bhupendrasambare
 * Date   :11/07/26
 * Time   :10:33 pm
 * Project:rag
 **/
package com.example.demo.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "document_chat_reference")
public class DocumentChatReference {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "chat_message_id")
    private UUID chatMessageId;

    @Column(name = "document_id")
    private UUID documentId;

    @Column(name = "chunk_id")
    private UUID chunkId;

    @Column(name = "similarity_score")
    private Float similarityScore;

    @Version
    private Long version;
}
