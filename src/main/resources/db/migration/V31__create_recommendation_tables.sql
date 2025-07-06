-- 创建用户特征表
CREATE TABLE IF NOT EXISTS user_features (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    feature_vector TEXT COMMENT '特征向量（JSON格式存储）',
    skills TEXT COMMENT '技能标签（JSON数组格式存储）',
    interests TEXT COMMENT '兴趣领域（JSON数组格式存储）',
    major VARCHAR(100) COMMENT '专业领域',
    education_level VARCHAR(50) COMMENT '学历等级',
    preferred_location VARCHAR(100) COMMENT '偏好工作地点',
    preferred_job_type VARCHAR(50) COMMENT '偏好工作类型',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY idx_user_id (user_id),
    CONSTRAINT fk_user_features_user_id FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户特征表';

-- 创建岗位特征表
CREATE TABLE IF NOT EXISTS job_features (
    id INT AUTO_INCREMENT PRIMARY KEY,
    job_id INT NOT NULL,
    feature_vector TEXT COMMENT '特征向量（JSON格式存储）',
    required_skills TEXT COMMENT '技能要求（JSON数组格式存储）',
    keywords TEXT COMMENT '岗位关键词（JSON数组格式存储）',
    category_id INT COMMENT '岗位分类ID',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY idx_job_id (job_id),
    CONSTRAINT fk_job_features_job_id FOREIGN KEY (job_id) REFERENCES jobs(id) ON DELETE CASCADE,
    CONSTRAINT fk_job_features_category_id FOREIGN KEY (category_id) REFERENCES job_categories(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='岗位特征表';

-- 创建岗位推荐表
CREATE TABLE IF NOT EXISTS job_recommendations (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    job_id INT NOT NULL,
    score DOUBLE COMMENT '推荐分数，用于排序（分数越高表示匹配度越高）',
    reason VARCHAR(255) COMMENT '推荐原因',
    status VARCHAR(20) NOT NULL DEFAULT 'new' COMMENT '推荐状态（new-新推荐, viewed-已查看, ignored-已忽略, applied-已申请）',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY idx_user_job (user_id, job_id),
    CONSTRAINT fk_job_recommendations_user_id FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_job_recommendations_job_id FOREIGN KEY (job_id) REFERENCES jobs(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='岗位推荐表';

-- 创建人才推荐表
CREATE TABLE IF NOT EXISTS talent_recommendations (
    id INT AUTO_INCREMENT PRIMARY KEY,
    organization_id INT NOT NULL,
    student_id INT NOT NULL,
    score DOUBLE COMMENT '推荐分数，用于排序（分数越高表示匹配度越高）',
    reason VARCHAR(255) COMMENT '推荐原因',
    status VARCHAR(20) NOT NULL DEFAULT 'new' COMMENT '推荐状态（new-新推荐, viewed-已查看, contacted-已联系, ignored-已忽略）',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY idx_org_student (organization_id, student_id),
    CONSTRAINT fk_talent_recommendations_org_id FOREIGN KEY (organization_id) REFERENCES organizations(id) ON DELETE CASCADE,
    CONSTRAINT fk_talent_recommendations_student_id FOREIGN KEY (student_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='人才推荐表';

-- 创建用户行为记录表
CREATE TABLE IF NOT EXISTS user_behaviors (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    behavior_type VARCHAR(20) NOT NULL COMMENT '行为类型（view-浏览, search-搜索, apply-申请, favorite-收藏）',
    target_type VARCHAR(20) NOT NULL COMMENT '目标类型（job-岗位, category-分类）',
    target_id INT NOT NULL COMMENT '目标ID（如岗位ID、分类ID等）',
    weight DOUBLE DEFAULT 1.0 COMMENT '行为权重（不同行为的重要性不同）',
    search_keyword VARCHAR(255) COMMENT '搜索关键词（当行为类型为search时使用）',
    occurred_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id),
    INDEX idx_target (target_type, target_id),
    INDEX idx_behavior_type (behavior_type),
    CONSTRAINT fk_user_behaviors_user_id FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户行为记录表'; 