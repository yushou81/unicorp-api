-- 为users表添加github_id字段，用于存储GitHub用户ID
ALTER TABLE users ADD COLUMN github_id VARCHAR(255) DEFAULT NULL COMMENT 'GitHub用户ID';

-- 添加索引以提高查询效率
CREATE INDEX idx_users_github_id ON users (github_id); 