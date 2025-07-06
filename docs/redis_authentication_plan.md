# Redis认证系统改造计划

## 概述

本文档描述了使用Redis实现认证相关功能的改造计划，包括会话管理、令牌存储和登录限制等功能。

## 1. 改造目标

- 会话管理：使用Redis存储用户会话信息，包括JWT令牌，设置适当的过期时间
- 令牌存储：实现JWT令牌黑名单，支持令牌刷新机制
- 登录限制：实现登录尝试次数限制和账户锁定功能

## 2. 需要修改的类

### 2.1 新增类

1. **RedisService**：封装Redis操作的服务类
   - 提供对Redis的基本操作方法
   - 实现会话管理、令牌存储和登录限制的具体逻辑

2. **TokenBlacklistService**：令牌黑名单服务
   - 管理已失效的JWT令牌
   - 提供令牌检查和添加黑名单功能

3. **LoginAttemptService**：登录尝试服务
   - 记录用户登录尝试次数
   - 实现账户锁定功能

### 2.2 修改现有类

1. **AuthController**
   - 添加登出接口
   - 添加令牌刷新接口

2. **JwtUtil**
   - 增加生成刷新令牌的方法
   - 增加验证刷新令牌的方法

3. **JwtAuthenticationFilter**
   - 集成令牌黑名单检查
   - 处理令牌过期情况

4. **UserServiceImpl**
   - 集成登录尝试限制功能
   - 添加登出功能
   - 添加令牌刷新功能

5. **SecurityConfig**
   - 更新安全配置，添加新的公开接口

6. **RedisConfig**
   - 确保配置支持存储认证相关数据

## 3. 实现步骤

1. 创建基础服务类（RedisService、TokenBlacklistService、LoginAttemptService）
2. 修改JwtUtil，增加刷新令牌相关功能
3. 更新JwtAuthenticationFilter，集成令牌黑名单检查
4. 修改UserServiceImpl，集成登录限制功能
5. 更新AuthController，添加登出和令牌刷新接口
6. 更新SecurityConfig，配置新的接口权限

## 4. 数据结构设计

### 4.1 Redis键设计

1. **会话管理**
   - `auth:session:{userId}` - 存储用户会话信息
   - 值类型：Hash（包含token、refreshToken、lastLoginTime等信息）
   - 过期时间：与JWT令牌相同

2. **令牌黑名单**
   - `auth:blacklist:{token}` - 存储已失效的令牌
   - 值类型：String（存储失效原因）
   - 过期时间：与原令牌过期时间相同

3. **登录限制**
   - `auth:attempts:{username}` - 存储登录尝试次数
   - 值类型：Integer
   - 过期时间：锁定时间（如1小时）

   - `auth:locked:{username}` - 标记账户锁定状态
   - 值类型：String（存储锁定原因和时间）
   - 过期时间：锁定持续时间（如24小时）

## 5. 安全考虑

1. 确保Redis服务器安全配置
2. 所有存储在Redis中的敏感信息应当加密
3. 设置合理的过期时间，避免数据永久存储
4. 实现适当的错误处理和日志记录

## 6. 测试计划

1. 单元测试各个服务类的功能
2. 集成测试认证流程
3. 性能测试Redis缓存的效率
4. 安全测试，确保没有引入新的安全漏洞 