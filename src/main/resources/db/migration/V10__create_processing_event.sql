-- ============================================================
-- Migration : V10__create_processing_event.sql
-- Description:
--     Creates the processing_event table.
--
--     Tracks the lifecycle of document ingestion:
--
--     Upload
--        ↓
--     Parsing
--        ↓
--     Chunking
--        ↓
--     Embedding
--        ↓
--     Vector Store
--        ↓
--     Completed / Failed
--
-- Author : Bhupendra Sambare
-- ============================================================

CREATE TABLE IF NOT EXISTS processing_event
(
    id UUID PRIMARY KEY,

    document_id UUID NOT NULL,

    event_type VARCHAR(30) NOT NULL,

    status VARCHAR(20) NOT NULL,

    error_message TEXT,

    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    completed_at TIMESTAMPTZ,

    version BIGINT NOT NULL DEFAULT 0,

    CONSTRAINT fk_processing_event_document
        FOREIGN KEY (document_id)
        REFERENCES document_info(id)
        ON DELETE CASCADE
);

-- ============================================================
-- Indexes
-- ============================================================

CREATE INDEX IF NOT EXISTS idx_processing_event_document
    ON processing_event(document_id);

CREATE INDEX IF NOT EXISTS idx_processing_event_event_type
    ON processing_event(event_type);

CREATE INDEX IF NOT EXISTS idx_processing_event_status
    ON processing_event(status);

CREATE INDEX IF NOT EXISTS idx_processing_event_created_at
    ON processing_event(created_at);

CREATE INDEX IF NOT EXISTS idx_processing_event_completed_at
    ON processing_event(completed_at);

-- Optimized for retrieving all processing events
-- for a document in chronological order.
CREATE INDEX IF NOT EXISTS idx_processing_event_document_created
    ON processing_event(document_id, created_at ASC);