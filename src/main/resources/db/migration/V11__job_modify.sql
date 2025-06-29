-- =================================================================
-- 数据库变更脚本 (Database Migration Script)
-- 版本: 1.9
-- 目标: unicorp
-- 说明: 为 jobs 表添加详细的结构化字段，以支持更专业的招聘功能。
-- =================================================================

-- 使用目标数据库
-- USE unicorp;

-- -----------------------------------------------------------------
-- 为 `jobs` 表批量添加新字段
-- -----------------------------------------------------------------
ALTER TABLE `jobs`
    -- 核心薪酬与工作信息
    ADD COLUMN `salary_min` INT NULL COMMENT '最低薪资 (单位: k)',
    ADD COLUMN `salary_max` INT NULL COMMENT '最高薪资 (单位: k)',
    ADD COLUMN `salary_unit` ENUM('per_month', 'per_year') NULL COMMENT '薪资单位 (月/年)',
    ADD COLUMN `job_type` ENUM('full_time', 'internship', 'part_time', 'remote') NOT NULL COMMENT '工作类型',
    ADD COLUMN `headcount` INT NOT NULL DEFAULT 1 COMMENT '招聘人数',

    -- 候选人要求
    ADD COLUMN `education_requirement` ENUM('bachelor', 'master', 'doctorate', 'any') NOT NULL DEFAULT 'any' COMMENT '学历要求',
    ADD COLUMN `experience_requirement` ENUM('fresh_graduate', 'less_than_1_year', '1_to_3_years', 'any') NOT NULL DEFAULT 'any' COMMENT '经验要求',

    -- 分类与标签
    ADD COLUMN `job_category` VARCHAR(100) NULL COMMENT '职能分类',
    ADD COLUMN `skill_tags` TEXT NULL COMMENT '技能标签 (以逗号分隔)',

    -- 流程与统计信息
    ADD COLUMN `application_deadline` DATE NULL COMMENT '申请截止日期',
    ADD COLUMN `view_count` INT NOT NULL DEFAULT 0 COMMENT '浏览量';


-- =================================================================
-- 脚本结束
-- =================================================================
