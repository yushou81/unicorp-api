-- 创建测试数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS unicorp_test DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 使用测试数据库
USE unicorp_test;

-- 清空现有表（如果存在）
SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS flyway_schema_history;
SET FOREIGN_KEY_CHECKS = 1; 