-- ============================================================
-- Migration : V11__align_schema_with_entities.sql
--
-- Description:
--     Aligns the PostgreSQL schema with the current JPA
--     entity definitions.
--
--     This migration fixes differences between the existing
--     V1-V10 database schema and the current entity mappings.
--
-- Author : Bhupendra Sambare
-- ============================================================


-- ============================================================
-- 1. USER INFO
-- ============================================================
--
-- Entity:
--
-- @Column(name = "role")
-- private UserRole role;
--
-- @Column(name = "active")
-- private Boolean active;
--
-- Both properties are nullable in the entity.
-- Therefore the database columns should also allow NULL.
-- ============================================================

ALTER TABLE user_info
    ALTER COLUMN role DROP NOT NULL;

ALTER TABLE user_info
    ALTER COLUMN active DROP NOT NULL;


-- ============================================================
-- 2. DOCUMENT INFO
-- ============================================================
--
-- Entity:
--
-- @Column(name = "file_name", nullable = false, length = 225)
-- private String fileName;
--
-- Existing migration uses VARCHAR(255).
--
-- Change it to VARCHAR(225) to match the entity.
-- ============================================================

ALTER TABLE document_info
    ALTER COLUMN file_name TYPE VARCHAR(225);


-- ============================================================
-- 3. DOCUMENT INFO - UPDATED AT
-- ============================================================
--
-- Entity:
--
-- @Column(name = "updated_at")
-- private LocalDateTime updatedAt;
--
-- updated_at is nullable in the entity.
--
-- Existing migration:
--
-- updated_at TIMESTAMPTZ NOT NULL
--
-- Remove NOT NULL.
-- ============================================================

ALTER TABLE document_info
    ALTER COLUMN updated_at DROP NOT NULL;


-- ============================================================
-- 4. DOCUMENT INFO - DELETED
-- ============================================================
--
-- Entity:
--
-- @Column(name = "deleted")
-- private Boolean deleted;
--
-- This column is already nullable in the existing schema.
--
-- No structural change required.
--
-- However, setting a default is useful because newly created
-- documents should normally start as not deleted.
-- ============================================================

ALTER TABLE document_info
    ALTER COLUMN deleted SET DEFAULT FALSE;


-- ============================================================
-- 5. USER INFO - ACTIVE
-- ============================================================
--
-- The entity allows NULL, but application logic normally expects
-- a newly created user to be active.
--
-- Keep the database default TRUE while allowing NULL.
-- ============================================================

ALTER TABLE user_info
    ALTER COLUMN active SET DEFAULT TRUE;


-- ============================================================
-- 6. DOCUMENT INFO - CHUNK COUNT
-- ============================================================
--
-- Entity:
--
-- private Long chunkCount;
--
-- Existing schema already uses BIGINT.
--
-- Keep default 0 so newly created documents start with zero
-- chunks.
-- ============================================================

ALTER TABLE document_info
    ALTER COLUMN chunk_count SET DEFAULT 0;


-- ============================================================
-- 7. DOCUMENT INFO - CREATED AT
-- ============================================================
--
-- Entity:
--
-- private LocalDateTime createdAt;
--
-- Existing schema already provides:
--
-- DEFAULT CURRENT_TIMESTAMP
--
-- No change required.
-- ============================================================


-- ============================================================
-- 8. REFRESH TOKEN
-- ============================================================
--
-- Entity:
--
-- @Column(name = "expires_at", nullable = false)
-- private LocalDateTime expiredAt;
--
-- IMPORTANT:
--
-- The Java field is named expiredAt, but the database column is
-- explicitly mapped to:
--
--     expires_at
--
-- Therefore the existing migration is correct.
--
-- No change required.
-- ============================================================


-- ============================================================
-- 9. VERSION COLUMNS
-- ============================================================
--
-- All entities contain:
--
-- @Version
-- private Long version;
--
-- BIGINT is the correct PostgreSQL representation.
--
-- Existing migrations already use:
--
-- version BIGINT NOT NULL DEFAULT 0
--
-- No change required.
-- ============================================================


-- ============================================================
-- 10. INDEXES
-- ============================================================
--
-- Existing indexes already cover the major query paths.
--
-- Add an index for active users because user authentication and
-- account validation commonly query by email + active status.
-- ============================================================

CREATE INDEX IF NOT EXISTS idx_user_email_active
    ON user_info(email, active);


-- ============================================================
-- 11. REFRESH TOKEN QUERY OPTIMIZATION
-- ============================================================
--
-- Refresh token validation commonly checks:
--
-- user_id
-- revoked
-- expires_at
--
-- Add a composite index for this operation.
-- ============================================================

CREATE INDEX IF NOT EXISTS idx_refresh_token_user_valid
    ON refresh_token(user_id, revoked, expires_at);


-- ============================================================
-- 12. DOCUMENT STATUS + USER
-- ============================================================
--
-- Document listing and processing operations commonly query:
--
-- user_id + status
--
-- Add a composite index.
-- ============================================================

CREATE INDEX IF NOT EXISTS idx_document_user_status
    ON document_info(user_id, status);


-- ============================================================
-- 13. DOCUMENT DELETED + USER
-- ============================================================
--
-- Your DocumentService uses user_id and deleted when retrieving
-- user documents.
--
-- Add a composite index for that query pattern.
-- ============================================================

CREATE INDEX IF NOT EXISTS idx_document_user_deleted
    ON document_info(user_id, deleted);


-- ============================================================
-- END OF V11
-- ============================================================