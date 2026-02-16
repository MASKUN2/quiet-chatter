ALTER TABLE member ADD COLUMN IF NOT EXISTS provider VARCHAR(20) DEFAULT 'NONE';
ALTER TABLE member ADD COLUMN IF NOT EXISTS provider_id VARCHAR(100);

-- 기존 데이터에 대해 provider 설정 (이미 DEFAULT 'NONE' 이지만 명시적으로 실행)
UPDATE member SET provider = 'NONE' WHERE provider IS NULL;
CREATE INDEX IF NOT EXISTS idx_member_provider_provider_id ON member (provider, provider_id);
