-- ============================================================
-- Migration : V4__create_refresh_token.sql
-- Description:
--     Creates the refresh_token table.
--
-- Author : Bhupendra Sambare
-- ============================================================

CREATE TABLE IF NOT EXISTS refresh_token
(
    id UUID PRIMARY KEY,

    user_id UUID NOT NULL,

    token TEXT NOT NULL,

    revoked BOOLEAN NOT NULL DEFAULT FALSE,

    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    expires_at TIMESTAMPTZ NOT NULL,

    version BIGINT NOT NULL DEFAULT 0,

    CONSTRAINT uk_refresh_token_token
        UNIQUE (token),

    CONSTRAINT fk_refresh_token_user
        FOREIGN KEY (user_id)
        REFERENCES user_info(id)
        ON DELETE CASCADE
);

-- ============================================================
-- Indexes
-- ============================================================

CREATE INDEX IF NOT EXISTS idx_refresh_token_user
    ON refresh_token(user_id);

CREATE INDEX IF NOT EXISTS idx_refresh_token_revoked
    ON refresh_token(revoked);

CREATE INDEX IF NOT EXISTS idx_refresh_token_expires_at
    ON refresh_token(expires_at);