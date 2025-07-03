-- 创建资源表 (优化版)
CREATE TABLE IF NOT EXISTS `resources` (
    `id` INT AUTO_INCREMENT PRIMARY KEY COMMENT '资源ID',
    `uploaded_by_user_id` INT NOT NULL COMMENT '上传用户ID',
    `title` VARCHAR(100) NOT NULL COMMENT '资源标题',
    `description` TEXT COMMENT '资源描述',
    `resource_type` VARCHAR(50) NOT NULL COMMENT '资源类型',
    `file_url` VARCHAR(255) NOT NULL COMMENT '文件URL',
    `visibility` ENUM('public', 'private', 'organization_only') NOT NULL DEFAULT 'public' COMMENT '可见性',
    `is_deleted` BOOLEAN NOT NULL DEFAULT FALSE COMMENT '是否逻辑删除',
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (`uploaded_by_user_id`) REFERENCES `users`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='共享资源表';

-- 为资源表添加索引
CREATE INDEX idx_resources_uploaded_by_user_id ON resources(uploaded_by_user_id);
CREATE INDEX idx_resources_resource_type ON resources(resource_type);
CREATE INDEX idx_resources_visibility ON resources(visibility);
CREATE INDEX idx_resources_created_at ON resources(created_at);



 