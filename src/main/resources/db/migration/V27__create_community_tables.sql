-- 创建社区板块表
CREATE TABLE community_category (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL COMMENT '板块名称',
    description TEXT COMMENT '板块描述',
    icon VARCHAR(255) COMMENT '板块图标',
    sort_order INT DEFAULT 0 COMMENT '排序顺序',
    parent_id BIGINT COMMENT '父板块ID',
    permission_level INT DEFAULT 0 COMMENT '权限级别：0-公开，1-登录可见，2-组织成员可见，3-管理员可见',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (parent_id) REFERENCES community_category(id) ON DELETE SET NULL
) COMMENT '社区板块表';

-- 创建话题表
CREATE TABLE community_topic (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL COMMENT '话题标题',
    content TEXT COMMENT '话题内容',
    user_id INT NOT NULL COMMENT '发布用户ID',
    category_id BIGINT NOT NULL COMMENT '所属板块ID',
    view_count INT DEFAULT 0 COMMENT '浏览次数',
    comment_count INT DEFAULT 0 COMMENT '评论数量',
    like_count INT DEFAULT 0 COMMENT '点赞数量',
    is_sticky TINYINT(1) DEFAULT 0 COMMENT '是否置顶',
    is_essence TINYINT(1) DEFAULT 0 COMMENT '是否精华',
    status VARCHAR(20) DEFAULT 'NORMAL' COMMENT '状态：NORMAL-正常，PENDING-待审核，DELETED-已删除',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (category_id) REFERENCES community_category(id) ON DELETE CASCADE
) COMMENT '社区话题表';

-- 创建评论表
CREATE TABLE community_comment (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    content TEXT NOT NULL COMMENT '评论内容',
    user_id INT NOT NULL COMMENT '评论用户ID',
    topic_id BIGINT NOT NULL COMMENT '所属话题ID',
    parent_id BIGINT COMMENT '父评论ID',
    like_count INT DEFAULT 0 COMMENT '点赞数量',
    status VARCHAR(20) DEFAULT 'NORMAL' COMMENT '状态：NORMAL-正常，DELETED-已删除',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (topic_id) REFERENCES community_topic(id) ON DELETE CASCADE,
    FOREIGN KEY (parent_id) REFERENCES community_comment(id) ON DELETE SET NULL
) COMMENT '社区评论表'; 