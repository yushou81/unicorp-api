# Redis认证系统实现总结

## 1. 实现的功能

### 1.1 会话管理
- 使用Redis存储用户会话信息，包括JWT令牌和刷新令牌
- 设置会话信息的过期时间与JWT令牌相同
- 提供会话信息的存储和清除功能

### 1.2 令牌存储与管理
- 实现JWT令牌黑名单，用于处理已登出的令牌
- 实现刷新令牌机制，支持令牌刷新
- 令牌过期时间可配置

### 1.3 登录限制
- 实现登录尝试次数限制，防止暴力破解
- 实现账户锁定功能，当登录失败次数超过阈值时自动锁定
- 提供锁定时间和剩余尝试次数查询功能

## 2. 新增的类

### 2.1 服务接口
- `TokenBlacklistService`：令牌黑名单服务接口
- `LoginAttemptService`：登录尝试服务接口

### 2.2 服务实现
- `TokenBlacklistServiceImpl`：令牌黑名单服务实现
- `LoginAttemptServiceImpl`：登录尝试服务实现

### 2.3 DTO
- `RefreshTokenDTO`：刷新令牌数据传输对象

## 3. 修改的类

### 3.1 工具类
- `JwtUtil`：添加刷新令牌相关功能

### 3.2 过滤器
- `JwtAuthenticationFilter`：集成令牌黑名单检查

### 3.3 服务类
- `UserServiceImpl`：集成登录尝试限制、登出和刷新令牌功能
- `CacheService`：添加getExpire方法

### 3.4 控制器
- `AuthController`：添加登出和刷新令牌接口

### 3.5 配置类
- `SecurityConfig`：更新安全配置，添加刷新令牌接口为公开接口

### 3.6 值对象
- `TokenVO`：添加刷新令牌字段

## 4. Redis键设计

### 4.1 会话管理
- `auth:session:{userId}`：存储用户会话信息（Hash类型）
  - 包含token、refreshToken、lastLoginTime等信息
  - 过期时间与JWT令牌相同

### 4.2 刷新令牌
- `auth:refresh:{userId}`：存储刷新令牌ID（String类型）
  - 过期时间与刷新令牌相同

### 4.3 令牌黑名单
- `auth:blacklist:{token}`：存储已失效的令牌（String类型）
  - 值为失效原因
  - 过期时间与原令牌过期时间相同

### 4.4 登录限制
- `auth:attempts:{username}`：存储登录尝试次数（Integer类型）
  - 过期时间可配置（默认60分钟）
- `auth:locked:{username}`：标记账户锁定状态（String类型）
  - 值为锁定原因
  - 过期时间可配置（默认30分钟）

## 5. 配置参数

系统提供以下可配置参数：

- `jwt.secret`：JWT密钥
- `jwt.expiration`：JWT令牌过期时间（毫秒）
- `jwt.refresh-expiration`：刷新令牌过期时间（毫秒）
- `auth.login.max-attempts`：最大登录尝试次数
- `auth.login.attempts-timeout`：尝试记录过期时间（分钟）
- `auth.login.default-lock-time`：默认锁定时间（分钟）

## 6. 使用流程

### 6.1 用户登录
1. 用户提交登录凭据
2. 系统验证用户是否被锁定
3. 系统验证用户凭据
4. 生成访问令牌和刷新令牌
5. 存储会话信息和刷新令牌ID
6. 返回令牌信息

### 6.2 用户登出
1. 用户提交登出请求
2. 系统将当前令牌添加到黑名单
3. 清除会话信息和刷新令牌ID

### 6.3 令牌刷新
1. 用户提交刷新令牌
2. 系统验证刷新令牌有效性
3. 生成新的访问令牌和刷新令牌
4. 更新会话信息和刷新令牌ID
5. 返回新的令牌信息

### 6.4 令牌验证
1. 系统接收请求，提取JWT令牌
2. 检查令牌是否在黑名单中
3. 验证令牌有效性
4. 加载用户信息并授权 