# GitHub登录实现文档

## 一、实现概述

已完成GitHub登录功能的后端实现，主要包括以下内容：

1. 配置OAuth2客户端，支持GitHub登录
2. 实现OAuth2登录成功处理器，处理GitHub登录成功后的逻辑
3. 数据库添加github_id字段，用于关联GitHub账号
4. 提供获取GitHub登录URL的API接口

## 二、实现细节

### 1. 配置文件

在`application.yml`中添加了GitHub OAuth2配置：

```yaml
spring:
  security:
    oauth2:
      client:
        registration:
          github:
            client-id: Ov23liJgcPyAbNEKj8uo
            client-secret: de07c0a751a6372400258bf04779cf5102a0f940
            scope: user:email
        provider:
          github:
            authorization-uri: https://github.com/login/oauth/authorize
            token-uri: https://github.com/login/oauth/access_token
            user-info-uri: https://api.github.com/user
            user-name-attribute: login
```

### 2. 安全配置

在`SecurityConfig`中配置了OAuth2登录相关的路径和处理器：

```java
.oauth2Login(oauth2 -> oauth2
    .loginPage("/login")
    .successHandler(oAuth2LoginSuccessHandler)
    .defaultSuccessUrl("http://localhost:8082/login-success", true)
)
```

### 3. 数据库迁移

创建了`V32__add_github_id_to_users.sql`迁移文件，为users表添加github_id字段：

```sql
ALTER TABLE users ADD COLUMN github_id VARCHAR(255) DEFAULT NULL COMMENT 'GitHub用户ID';
CREATE INDEX idx_users_github_id ON users (github_id);
```

### 4. 实体类更新

在`User`实体类中添加了githubId字段：

```java
/**
 * GitHub用户ID
 */
private String githubId;
```

### 5. 登录处理逻辑

实现了`OAuth2LoginSuccessHandler`类，处理GitHub登录成功后的逻辑：

- 获取GitHub用户信息
- 检查用户是否已存在
- 如果不存在，创建新用户
- 生成JWT令牌
- 重定向到前端，带上token参数

### 6. API接口

在`AuthController`中添加了获取GitHub登录URL的接口：

```java
@GetMapping("/github/login-url")
public ResultVO<String> getGitHubLoginUrl() {
    return ResultVO.success("获取GitHub登录URL成功", "/oauth2/authorization/github");
}
```

## 三、使用方法

### 后端

1. 确保配置文件中的GitHub OAuth2配置正确
2. 确保数据库迁移已执行，users表已添加github_id字段
3. 确保SecurityConfig中已配置OAuth2登录相关的路径和处理器

### 前端

1. 在登录页面添加"使用GitHub登录"按钮
2. 调用`/v1/auth/github/login-url`接口获取GitHub登录URL
3. 点击按钮时跳转到获取的URL
4. 用户在GitHub上授权后，会被重定向回前端，带上token参数
5. 前端获取token参数，完成登录流程

## 四、注意事项

1. GitHub OAuth应用的回调地址必须与配置的一致，当前为`http://localhost:8081/login/oauth2/code/github`
2. 前端重定向地址当前为`http://localhost:8082/login-success`，需要根据实际情况调整
3. 用户首次通过GitHub登录时，会自动创建账号并分配学生角色
4. 已实现的功能仅包括登录，未实现账号绑定和解绑功能 