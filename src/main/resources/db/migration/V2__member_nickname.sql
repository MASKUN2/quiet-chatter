ALTER TABLE member
    ADD COLUMN IF NOT EXISTS nickname VARCHAR(255);

ALTER TABLE talk
    ADD COLUMN IF NOT EXISTS nickname VARCHAR(255);
COMMENT ON COLUMN talk.nickname IS '작성 당시의 닉네임을 둔다(중복허용)';