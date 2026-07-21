-- ============================================================
-- Migration : V3__create_user_info.sql
-- Description:
--     Creates the user_info table.
--
-- Author : Bhupendra Sambare
-- ============================================================

CREATE TABLE IF NOT EXISTS user_info
(
    id UUID PRIMARY KEY,

    first_name VARCHAR(100) NOT NULL,

    last_name VARCHAR(100) NOT NULL,

    email VARCHAR(255) NOT NULL UNIQUE,

    password_hash TEXT NOT NULL,

    profile_image TEXT,

    role VARCHAR(20) NOT NULL,

    active BOOLEAN NOT NULL DEFAULT TRUE,

    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    updated_at TIMESTAMPTZ,

    created_by UUID,

    updated_by UUID,

    version BIGINT NOT NULL DEFAULT 0
);

-- ============================================================
-- Indexes
-- ============================================================

CREATE INDEX IF NOT EXISTS idx_user_email
    ON user_info(email);

CREATE INDEX IF NOT EXISTS idx_user_role
    ON user_info(role);

CREATE INDEX IF NOT EXISTS idx_user_active
    ON user_info(active);

-- ============================================================
-- Self Referencing Foreign Keys
-- ============================================================

ALTER TABLE user_info
ADD CONSTRAINT fk_user_created_by
FOREIGN KEY (created_by)
REFERENCES user_info(id)
ON DELETE SET NULL;

ALTER TABLE user_info
ADD CONSTRAINT fk_user_updated_by
FOREIGN KEY (updated_by)
REFERENCES user_info(id)
ON DELETE SET NULL;