-- ============================================================
-- Migration : V6__create_document_chunks.sql
-- Description:
--     Creates the document_chunks table.
--
-- Author : Bhupendra Sambare
-- ============================================================

CREATE TABLE IF NOT EXISTS document_chunks
(
    id UUID PRIMARY KEY,

    document_id UUID NOT NULL,

    chunk_index INTEGER NOT NULL,

    content TEXT NOT NULL,

    token_count INTEGER,

    page_number INTEGER,

    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    version BIGINT NOT NULL DEFAULT 0,

    CONSTRAINT fk_document_chunks_document
        FOREIGN KEY (document_id)
        REFERENCES document_info(id)
        ON DELETE CASCADE,

    CONSTRAINT uk_document_chunk
        UNIQUE (document_id, chunk_index)
);

-- ============================================================
-- Indexes
-- ============================================================

CREATE INDEX IF NOT EXISTS idx_document_chunks_document
    ON document_chunks(document_id);

CREATE INDEX IF NOT EXISTS idx_document_chunks_page
    ON document_chunks(page_number);

CREATE INDEX IF NOT EXISTS idx_document_chunks_created_at
    ON document_chunks(created_at);

CREATE INDEX IF NOT EXISTS idx_document_chunks_token_count
    ON document_chunks(token_count);