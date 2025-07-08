-- =================================================================
-- 版本: V18
-- 描述: 添加组织logo字段
-- =================================================================

-- 为organizations表添加logo字段
ALTER TABLE `organizations` 
ADD COLUMN `logo_url` VARCHAR(255) COMMENT '组织logo图片的相对路径' AFTER `website`; 