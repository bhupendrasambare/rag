-- ============================================================
-- Migration : V7__create_chat_session.sql
-- Description:
--     Creates the chat_session table.
--
-- Author : Bhupendra Sambare
-- ============================================================

CREATE TABLE IF NOT EXISTS chat_session
(
    id UUID PRIMARY KEY,

    user_id UUID NOT NULL,

    topic VARCHAR(255),

    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    updated_at TIMESTAMPTZ,

    version BIGINT NOT NULL DEFAULT 0,

    CONSTRAINT fk_chat_session_user
        FOREIGN KEY (user_id)
        REFERENCES user_info(id)
        ON DELETE CASCADE
);

-- ============================================================
-- Indexes
-- ============================================================

CREATE INDEX IF NOT EXISTS idx_chat_session_user
    ON chat_session(user_id);

CREATE INDEX IF NOT EXISTS idx_chat_session_created_at
    ON chat_session(created_at);

CREATE INDEX IF NOT EXISTS idx_chat_session_updated_at
    ON chat_session(updated_at);

CREATE INDEX IF NOT EXISTS idx_chat_session_topic
    ON chat_session(topic);