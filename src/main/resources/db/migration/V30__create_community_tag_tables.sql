-- 创建标签表
CREATE TABLE community_tag (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL COMMENT '标签名称',
    description VARCHAR(255) COMMENT '标签描述',
    usage_count INT DEFAULT 0 COMMENT '使用次数',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_tag_name (name)
) COMMENT '标签表';

-- 创建内容标签关联表
CREATE TABLE community_content_tag (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    content_type VARCHAR(20) NOT NULL COMMENT '内容类型：TOPIC-话题，QUESTION-问题，RESOURCE-资源',
    content_id BIGINT NOT NULL COMMENT '内容ID',
    tag_id BIGINT NOT NULL COMMENT '标签ID',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (tag_id) REFERENCES community_tag(id) ON DELETE CASCADE,
    UNIQUE KEY uk_content_tag (content_type, content_id, tag_id)
) COMMENT '内容标签关联表';

-- 创建敏感词表
CREATE TABLE community_sensitive_word (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    word VARCHAR(50) NOT NULL COMMENT '敏感词',
    level TINYINT DEFAULT 1 COMMENT '级别：1-轻度，2-中度，3-重度',
    replacement VARCHAR(50) COMMENT '替换词',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_word (word)
) COMMENT '敏感词表'; 