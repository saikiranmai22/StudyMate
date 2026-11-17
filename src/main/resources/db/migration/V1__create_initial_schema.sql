
-- =========================================================
-- StudyMate
-- V1: Initial database schema
-- Database: MySQL 8
-- =========================================================

-- 1. USERS
-- Stores student account information.

CREATE TABLE users (
    id BIGINT NOT NULL AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL,
    email VARCHAR(255) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    phone VARCHAR(20),
    role VARCHAR(20) NOT NULL DEFAULT 'STUDENT',
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6)
        ON UPDATE CURRENT_TIMESTAMP(6),

    PRIMARY KEY (id),
    CONSTRAINT uk_users_username UNIQUE (username),
    CONSTRAINT uk_users_email UNIQUE (email),
    CONSTRAINT chk_users_role CHECK (role IN ('STUDENT'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_0900_ai_ci;


-- 2. WORKSPACES
-- Each workspace belongs to one user.

CREATE TABLE workspaces (
    id BIGINT NOT NULL AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    name VARCHAR(150) NOT NULL,
    description VARCHAR(500),
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6)
        ON UPDATE CURRENT_TIMESTAMP(6),

    PRIMARY KEY (id),

    CONSTRAINT uk_workspace_user_name
        UNIQUE (user_id, name),

    CONSTRAINT fk_workspaces_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE,

    INDEX idx_workspaces_user (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_0900_ai_ci;


-- 3. DOCUMENTS
-- Stores metadata for PDFs uploaded to a workspace.
-- The actual PDF is stored in external object storage.

CREATE TABLE documents (
    id BIGINT NOT NULL AUTO_INCREMENT,
    workspace_id BIGINT NOT NULL,
    original_filename VARCHAR(255) NOT NULL,
    storage_key VARCHAR(500) NOT NULL,
    file_size_bytes BIGINT NOT NULL,
    page_count INT,
    status VARCHAR(20) NOT NULL DEFAULT 'PROCESSING',
    embedding_model VARCHAR(100),
    embedding_dimensions INT,
    failure_reason VARCHAR(1000),
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6)
        ON UPDATE CURRENT_TIMESTAMP(6),

    PRIMARY KEY (id),

    CONSTRAINT uk_documents_storage_key UNIQUE (storage_key),

    CONSTRAINT fk_documents_workspace
        FOREIGN KEY (workspace_id)
        REFERENCES workspaces(id)
        ON DELETE CASCADE,

    CONSTRAINT chk_documents_status
        CHECK (status IN ('PROCESSING', 'COMPLETED', 'FAILED')),

    CONSTRAINT chk_documents_file_size
        CHECK (file_size_bytes > 0),

    INDEX idx_documents_workspace_status (workspace_id, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_0900_ai_ci;


-- 4. DOCUMENT_CHUNKS
-- Stores extracted text and its embedding vector.
-- Embeddings are stored as JSON for the initial MVP.

CREATE TABLE document_chunks (
    id BIGINT NOT NULL AUTO_INCREMENT,
    document_id BIGINT NOT NULL,
    chunk_index INT NOT NULL,
    page_start INT NOT NULL,
    page_end INT NOT NULL,
    content LONGTEXT NOT NULL,
    embedding JSON NOT NULL,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),

    PRIMARY KEY (id),

    CONSTRAINT uk_document_chunk_index
        UNIQUE (document_id, chunk_index),

    CONSTRAINT fk_chunks_document
        FOREIGN KEY (document_id)
        REFERENCES documents(id)
        ON DELETE CASCADE,

    CONSTRAINT chk_chunks_page_range
        CHECK (page_start > 0 AND page_end >= page_start),

    CONSTRAINT chk_chunks_index
        CHECK (chunk_index >= 0),

    INDEX idx_chunks_document (document_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_0900_ai_ci;


-- 5. CONVERSATIONS
-- Stores independent chat sessions inside a workspace.

CREATE TABLE conversations (
    id BIGINT NOT NULL AUTO_INCREMENT,
    workspace_id BIGINT NOT NULL,
    title VARCHAR(200) NOT NULL DEFAULT 'New conversation',
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6)
        ON UPDATE CURRENT_TIMESTAMP(6),

    PRIMARY KEY (id),

    CONSTRAINT fk_conversations_workspace
        FOREIGN KEY (workspace_id)
        REFERENCES workspaces(id)
        ON DELETE CASCADE,

    INDEX idx_conversations_workspace_updated
        (workspace_id, updated_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_0900_ai_ci;


-- 6. MESSAGES
-- Stores user questions and assistant responses.
-- Source references are stored as JSON.

CREATE TABLE messages (
    id BIGINT NOT NULL AUTO_INCREMENT,
    conversation_id BIGINT NOT NULL,
    role VARCHAR(20) NOT NULL,
    content LONGTEXT NOT NULL,
    sources JSON,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),

    PRIMARY KEY (id),

    CONSTRAINT fk_messages_conversation
        FOREIGN KEY (conversation_id)
        REFERENCES conversations(id)
        ON DELETE CASCADE,

    CONSTRAINT chk_messages_role
        CHECK (role IN ('USER', 'ASSISTANT')),

    INDEX idx_messages_conversation_created
        (conversation_id, created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_0900_ai_ci;


-- 7. NOTES
-- Stores manually created notes and notes saved from AI responses.

CREATE TABLE notes (
    id BIGINT NOT NULL AUTO_INCREMENT,
    workspace_id BIGINT NOT NULL,
    title VARCHAR(200) NOT NULL,
    content LONGTEXT NOT NULL,
    source_message_id BIGINT,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6)
        ON UPDATE CURRENT_TIMESTAMP(6),

    PRIMARY KEY (id),

    CONSTRAINT fk_notes_workspace
        FOREIGN KEY (workspace_id)
        REFERENCES workspaces(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_notes_source_message
        FOREIGN KEY (source_message_id)
        REFERENCES messages(id)
        ON DELETE SET NULL,

    INDEX idx_notes_workspace (workspace_id),
    INDEX idx_notes_source_message (source_message_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_0900_ai_ci;


-- 8. REFRESH_TOKENS
-- Stores hashes of refresh tokens for session management.
-- Never store the original refresh token here.

CREATE TABLE refresh_tokens (
    id BIGINT NOT NULL AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    token_hash CHAR(64) NOT NULL,
    expires_at DATETIME(6) NOT NULL,
    revoked_at DATETIME(6),
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),

    PRIMARY KEY (id),

    CONSTRAINT uk_refresh_token_hash UNIQUE (token_hash),

    CONSTRAINT fk_refresh_tokens_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE,

    INDEX idx_refresh_tokens_user (user_id),
    INDEX idx_refresh_tokens_expiry (expires_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_0900_ai_ci;
