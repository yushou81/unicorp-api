-- 修改users表的email字段，允许为NULL，以支持OAuth2登录
ALTER TABLE users MODIFY COLUMN email VARCHAR(255) NULL COMMENT '用户邮箱，OAuth2登录用户可为NULL'; 