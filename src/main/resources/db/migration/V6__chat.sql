-- 聊天会话表
CREATE TABLE IF NOT EXISTS chat_session (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user1_id BIGINT NOT NULL,
    user2_id BIGINT NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uc_session UNIQUE (user1_id, user2_id)
);

-- 聊天消息表
CREATE TABLE IF NOT EXISTS chat_message (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    session_id BIGINT NOT NULL,
    sender_id BIGINT NOT NULL,
    content TEXT NOT NULL,
    sent_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    is_read TINYINT DEFAULT 0,
    CONSTRAINT fk_message_session FOREIGN KEY (session_id) REFERENCES chat_session(id)
);
   
