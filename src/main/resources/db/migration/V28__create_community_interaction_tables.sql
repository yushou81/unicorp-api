-- 创建用户关系表
CREATE TABLE community_user_relation (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL COMMENT '用户ID',
    target_id INT NOT NULL COMMENT '目标用户ID',
    relation_type VARCHAR(20) NOT NULL COMMENT '关系类型：FOLLOW-关注，BLOCK-拉黑',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (target_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE KEY uk_user_target_relation (user_id, target_id, relation_type)
) COMMENT '用户关系表';

-- 创建通知表
CREATE TABLE community_notification (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL COMMENT '接收用户ID',
    content TEXT NOT NULL COMMENT '通知内容',
    notification_type VARCHAR(50) NOT NULL COMMENT '通知类型：COMMENT-评论，LIKE-点赞，FOLLOW-关注，SYSTEM-系统',
    related_id BIGINT COMMENT '相关内容ID',
    is_read TINYINT(1) DEFAULT 0 COMMENT '是否已读',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) COMMENT '通知表';

-- 创建点赞表
CREATE TABLE community_like (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL COMMENT '用户ID',
    content_type VARCHAR(20) NOT NULL COMMENT '内容类型：TOPIC-话题，COMMENT-评论，QUESTION-问题，ANSWER-回答',
    content_id BIGINT NOT NULL COMMENT '内容ID',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE KEY uk_user_content (user_id, content_type, content_id)
) COMMENT '点赞表';

-- 创建收藏表
CREATE TABLE community_favorite (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL COMMENT '用户ID',
    content_type VARCHAR(20) NOT NULL COMMENT '内容类型：TOPIC-话题，QUESTION-问题，RESOURCE-资源',
    content_id BIGINT NOT NULL COMMENT '内容ID',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE KEY uk_user_content (user_id, content_type, content_id)
) COMMENT '收藏表'; 