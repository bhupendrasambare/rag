-- ============================================================
-- Migration: V1__enable_extensions.sql
-- Description:
--     Enables required PostgreSQL extensions for the Offline
--     RAG Document Search application.
--
-- Extensions:
--     - pgcrypto : UUID generation using gen_random_uuid()
--
-- Author : Bhupendra Sambare
-- ============================================================

-- ----------------------------------------------------------------
-- Enable pgcrypto extension
-- ----------------------------------------------------------------
CREATE EXTENSION IF NOT EXISTS pgcrypto;