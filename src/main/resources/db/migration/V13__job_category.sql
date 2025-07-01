-- =================================================================
-- 数据库变更脚本 (Database Migration Script)
-- 版本: 1.10
-- 目标: unicorp
-- 说明: 引入专业的三级联动工作分类体系。
-- 1. 移除 jobs 表中旧的分类字段。
-- 2. 创建新的 job_categories 表用于存储层级分类。
-- 3. 创建新的 job_category_relations 表用于关联岗位和分类。
-- =================================================================

-- 使用目标数据库
-- USE unicorp;

-- -----------------------------------------------------------------
-- 步骤 1: 从 `jobs` 表中移除旧的、非结构化的分类字段
-- -----------------------------------------------------------------
ALTER TABLE `jobs`
DROP COLUMN `job_category`,
DROP COLUMN `skill_tags`;

-- -----------------------------------------------------------------
-- 步骤 2: 创建 `job_categories` 表 (单表自关联)
-- -----------------------------------------------------------------
-- 这张表将存储所有层级的工作分类和技能标签。
CREATE TABLE `job_categories` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `name` VARCHAR(100) NOT NULL COMMENT '分类/标签名称 (e.g., 后端开发, Java)',
    `parent_id` INT NULL COMMENT '父级分类的ID，第一级分类此字段为NULL',
    `level` INT NOT NULL COMMENT '层级 (1, 2, 3)，便于查询',
    `is_deleted` BOOLEAN NOT NULL DEFAULT FALSE,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY `uk_name_parent` (`name`, `parent_id`),
    FOREIGN KEY (`parent_id`) REFERENCES `job_categories`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB COMMENT='工作分类与技能标签表';

-- -----------------------------------------------------------------
-- 步骤 3: 创建 `job_category_relations` 表 (多对多关系)
-- -----------------------------------------------------------------
-- 这张中间表用于将一个岗位与多个分类/标签进行关联。
CREATE TABLE `job_category_relations` (
    `job_id` INT NOT NULL COMMENT '关联的岗位ID',
    `category_id` INT NOT NULL COMMENT '关联的分类ID',
    PRIMARY KEY (`job_id`, `category_id`),
    FOREIGN KEY (`job_id`) REFERENCES `jobs`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`category_id`) REFERENCES `job_categories`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB COMMENT='岗位与分类的关联表';

-- -----------------------------------------------------------------
-- 为 `jobs` 表添加 `tags` 字段
-- -----------------------------------------------------------------
-- 这个字段将用于存储由发布者自定义的、以逗号分隔的字符串标签。
-- 例如: "五险一金,不加班,团队氛围好"
ALTER TABLE `jobs`
ADD COLUMN `tags` VARCHAR(255) NULL COMMENT '企业自定义的岗位亮点标签 (逗号分隔)' AFTER `view_count`;

-- =================================================================
-- 脚本结束
-- =================================================================
