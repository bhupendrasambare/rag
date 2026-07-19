-- ============================================================
-- Migration : V5__create_document_info.sql
-- Description:
--     Creates the document_info table.
--
-- Author : Bhupendra Sambare
-- ============================================================

CREATE TABLE IF NOT EXISTS document_info
(
    id UUID PRIMARY KEY,

    user_id UUID NOT NULL,

    embedding_model VARCHAR(100) NOT NULL,

    chat_model VARCHAR(100) NOT NULL,

    file_name VARCHAR(255) NOT NULL,

    original_file_name VARCHAR(255) NOT NULL,

    file_type VARCHAR(30) NOT NULL,

    mime_type VARCHAR(100) NOT NULL,

    file_size BIGINT NOT NULL,

    storage_path TEXT NOT NULL,

    checksum VARCHAR(128) NOT NULL,

    status VARCHAR(30) NOT NULL,

    total_pages INTEGER,

    chunk_count BIGINT DEFAULT 0,

    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    processed_at TIMESTAMPTZ,

    version BIGINT NOT NULL DEFAULT 0,

    CONSTRAINT uk_document_checksum
        UNIQUE (checksum),

    CONSTRAINT fk_document_user
        FOREIGN KEY (user_id)
        REFERENCES user_info(id)
        ON DELETE CASCADE
);

-- ============================================================
-- Indexes
-- ============================================================

CREATE INDEX IF NOT EXISTS idx_document_user
    ON document_info(user_id);

CREATE INDEX IF NOT EXISTS idx_document_status
    ON document_info(status);

CREATE INDEX IF NOT EXISTS idx_document_file_name
    ON document_info(file_name);

CREATE INDEX IF NOT EXISTS idx_document_created_at
    ON document_info(created_at);

CREATE INDEX IF NOT EXISTS idx_document_file_type
    ON document_info(file_type);

CREATE INDEX IF NOT EXISTS idx_document_checksum
    ON document_info(checksum);