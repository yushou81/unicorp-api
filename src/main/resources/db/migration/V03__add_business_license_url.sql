-- =================================================================
-- 数据库变更脚本 (Database Migration Script)
-- 版本: 1.3.1
-- 目标: unicorp
-- 说明: 为 enterprise_details 表添加营业执照图片URL字段。
-- =================================================================

-- 使用目标数据库
-- USE unicorp;

-- 为 enterprise_details 表添加 business_license_url 字段
-- 这个字段将用于存储营业执照图片的存放地址
ALTER TABLE `enterprise_details`
ADD COLUMN `business_license_url` VARCHAR(255) NOT NULL COMMENT '营业执照图片URL' AFTER `company_size`;
