-- 修改users表的password字段，允许为NULL，以支持OAuth2登录
ALTER TABLE users MODIFY COLUMN password VARCHAR(255) NULL COMMENT '存储加密后的密码，OAuth2登录用户可为NULL';

-- 修改表注释
ALTER TABLE users COMMENT = '用户核心认证与基本信息表，支持本地账号和OAuth2登录'; 