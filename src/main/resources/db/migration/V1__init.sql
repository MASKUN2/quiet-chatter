-- Member 테이블
CREATE TABLE IF NOT EXISTS member
(
    id               UUID PRIMARY KEY,
    created_at       TIMESTAMP,
    last_modified_at TIMESTAMP,
    role             VARCHAR(255),
    status VARCHAR(255)
);

-- Book 테이블
CREATE TABLE IF NOT EXISTS book
(
    id                UUID PRIMARY KEY,
    created_at        TIMESTAMP,
    last_modified_at  TIMESTAMP,
    title             VARCHAR(255),
    isbn VARCHAR(255) NOT NULL,
    author            VARCHAR(255),
    thumbnail_image_url TEXT,
    description       TEXT,
    external_link_url TEXT
);

CREATE INDEX IF NOT EXISTS idx_book_isbn ON book (isbn);
CREATE INDEX IF NOT EXISTS idx_book_title ON book (title);

-- Talk 테이블
CREATE TABLE IF NOT EXISTS talk
(
    id               UUID PRIMARY KEY,
    created_at       TIMESTAMP,
    last_modified_at TIMESTAMP,
    book_id          UUID,
    member_id        UUID,
    content          VARCHAR(250),
    date_to_hidden   DATE,
    is_hidden        BOOLEAN DEFAULT FALSE,
    like_count       BIGINT  DEFAULT 0,
    support_count    BIGINT  DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_talk_book_id ON talk (book_id);
CREATE INDEX IF NOT EXISTS idx_talk_member_id ON talk (member_id);
CREATE INDEX IF NOT EXISTS idx_talk_created_at ON talk (created_at);
CREATE INDEX IF NOT EXISTS idx_talk_date_to_hidden_is_hidden ON talk (date_to_hidden, is_hidden);

-- Reaction 테이블
CREATE TABLE IF NOT EXISTS reaction
(
    id               UUID PRIMARY KEY,
    talk_id   UUID,
    member_id UUID,
    type      VARCHAR(255),
    created_at TIMESTAMP,
    last_modified_at TIMESTAMP,
    CONSTRAINT uq_reaction_talk_member_type UNIQUE (talk_id, member_id, type)
);

CREATE INDEX IF NOT EXISTS idx_reaction_talk_id ON reaction (talk_id);
CREATE INDEX IF NOT EXISTS idx_reaction_member_id ON reaction (member_id);
CREATE INDEX IF NOT EXISTS idx_reaction_type ON reaction (type);

-- CustomerMessage 테이블
CREATE TABLE IF NOT EXISTS customer_message
(
    id               UUID PRIMARY KEY,
    created_at       TIMESTAMP,
    last_modified_at TIMESTAMP,
    message          TEXT
);
