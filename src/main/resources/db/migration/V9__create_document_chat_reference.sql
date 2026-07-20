-- ============================================================
-- Migration : V9__create_document_chat_reference.sql
-- Description:
--     Creates the document_chat_reference table.
--
--     Stores the relationship between an AI-generated chat
--     message and the document chunks that were retrieved
--     during RAG search.
--
-- Author : Bhupendra Sambare
-- ============================================================

CREATE TABLE IF NOT EXISTS document_chat_reference
(
    id UUID PRIMARY KEY,

    chat_message_id UUID NOT NULL,

    document_id UUID NOT NULL,

    chunk_id UUID NOT NULL,

    similarity_score REAL NOT NULL,

    version BIGINT NOT NULL DEFAULT 0,

    CONSTRAINT fk_dcr_chat_message
        FOREIGN KEY (chat_message_id)
        REFERENCES chat_message(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_dcr_document
        FOREIGN KEY (document_id)
        REFERENCES document_info(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_dcr_chunk
        FOREIGN KEY (chunk_id)
        REFERENCES document_chunks(id)
        ON DELETE CASCADE,

    CONSTRAINT uk_dcr_message_chunk
        UNIQUE (chat_message_id, chunk_id)
);

-- ============================================================
-- Indexes
-- ============================================================

CREATE INDEX IF NOT EXISTS idx_dcr_chat_message
    ON document_chat_reference(chat_message_id);

CREATE INDEX IF NOT EXISTS idx_dcr_document
    ON document_chat_reference(document_id);

CREATE INDEX IF NOT EXISTS idx_dcr_chunk
    ON document_chat_reference(chunk_id);

CREATE INDEX IF NOT EXISTS idx_dcr_similarity
    ON document_chat_reference(similarity_score);

-- Optimized for retrieving citations for a response
CREATE INDEX IF NOT EXISTS idx_dcr_message_document
    ON document_chat_reference(chat_message_id, document_id);