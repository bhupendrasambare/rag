-- ============================================================
-- Migration : V8__create_chat_message.sql
-- Description:
--     Creates the chat_message table.
--
-- Author : Bhupendra Sambare
-- ============================================================

CREATE TABLE IF NOT EXISTS chat_message
(
    id UUID PRIMARY KEY,

    chat_session_id UUID NOT NULL,

    role VARCHAR(20) NOT NULL,

    message TEXT NOT NULL,

    token_usage BIGINT,

    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    version BIGINT NOT NULL DEFAULT 0,

    CONSTRAINT fk_chat_message_session
        FOREIGN KEY (chat_session_id)
        REFERENCES chat_session(id)
        ON DELETE CASCADE
);

-- ============================================================
-- Indexes
-- ============================================================

CREATE INDEX IF NOT EXISTS idx_chat_message_session
    ON chat_message(chat_session_id);

CREATE INDEX IF NOT EXISTS idx_chat_message_role
    ON chat_message(role);

CREATE INDEX IF NOT EXISTS idx_chat_message_created_at
    ON chat_message(created_at);

-- Optimized for loading an entire conversation in order
CREATE INDEX IF NOT EXISTS idx_chat_message_session_created
    ON chat_message(chat_session_id, created_at ASC);