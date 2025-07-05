-- 创建问题表
CREATE TABLE community_question (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL COMMENT '问题标题',
    content TEXT COMMENT '问题描述',
    user_id INT NOT NULL COMMENT '提问用户ID',
    category_id BIGINT NOT NULL COMMENT '所属分类ID',
    view_count INT DEFAULT 0 COMMENT '浏览次数',
    answer_count INT DEFAULT 0 COMMENT '回答数量',
    bounty_points INT DEFAULT 0 COMMENT '悬赏积分',
    best_answer_id BIGINT COMMENT '最佳答案ID',
    status VARCHAR(20) DEFAULT 'UNSOLVED' COMMENT '状态：UNSOLVED-未解决，SOLVED-已解决，CLOSED-已关闭',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (category_id) REFERENCES community_category(id) ON DELETE CASCADE
) COMMENT '社区问题表';

-- 创建回答表
CREATE TABLE community_answer (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    content TEXT NOT NULL COMMENT '回答内容',
    user_id INT NOT NULL COMMENT '回答用户ID',
    question_id BIGINT NOT NULL COMMENT '所属问题ID',
    like_count INT DEFAULT 0 COMMENT '点赞数量',
    is_accepted TINYINT(1) DEFAULT 0 COMMENT '是否被采纳',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (question_id) REFERENCES community_question(id) ON DELETE CASCADE
) COMMENT '社区回答表';

-- 添加外键约束
ALTER TABLE community_question 
ADD CONSTRAINT fk_question_best_answer 
FOREIGN KEY (best_answer_id) REFERENCES community_answer(id) ON DELETE SET NULL; 