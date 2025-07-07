# GitHub 登录集成计划

## 一、目标
实现用户通过 GitHub 账号快捷登录系统，提升用户体验，降低注册门槛，并为后续与 GitHub 相关的功能扩展（如项目同步、贡献展示等）打下基础。

## 二、技术选型
- 使用 [Spring Security OAuth2 Client](https://docs.spring.io/spring-security/reference/servlet/oauth2/login/index.html) 实现第三方登录。
- 前端通过重定向或弹窗方式引导用户授权。
- 后端负责与 GitHub OAuth2 服务器交互，获取用户信息。
- 用户信息与本地账户体系关联。

## 三、实现步骤

### 1. 注册 GitHub OAuth 应用
- 登录 [GitHub Developer Settings](https://github.com/settings/developers)
- 新建 OAuth App，填写回调地址（如 `https://your-domain.com/login/oauth2/code/github`）
- 获取 Client ID 和 Client Secret

### 2. 配置后端
- 在 `application.yml` 中添加 GitHub OAuth2 配置：
  ```yaml
  spring:
    security:
      oauth2:
        client:
          registration:
            github:
              client-id: <your-client-id>
              client-secret: <your-client-secret>
              scope: user:email
          provider:
            github:
              authorization-uri: https://github.com/login/oauth/authorize
              token-uri: https://github.com/login/oauth/access_token
              user-info-uri: https://api.github.com/user
              user-name-attribute: login
  ```
- 配置 `SecurityConfig`，允许 `/oauth2/**`、`/login/oauth2/**` 等相关路径匿名访问。
- 实现 OAuth2 登录成功处理逻辑（如自动注册、绑定本地用户、生成 JWT 等）。

### 3. 前端对接
- 提供“使用 GitHub 登录”按钮，点击后跳转到 `/oauth2/authorization/github`。
- 登录成功后，前端获取后端返回的用户信息和 Token，完成登录流程。

### 4. 用户信息处理
- 第一次登录时，自动注册或引导用户绑定本地账号。
- 后续登录时自动识别并登录。
- 可扩展：同步 GitHub 头像、昵称等信息。

### 5. 测试与优化
- 测试不同场景下的登录流程（新用户、已绑定用户、取消授权等）。
- 处理异常情况（如 GitHub 授权失败、网络异常等）。
- 日志记录与安全加固。

## 四、注意事项
- 保管好 Client Secret，避免泄露。
- 回调地址需与 GitHub 应用配置一致。
- 需考虑与现有本地登录、其他第三方登录的兼容。
- 用户唯一性设计（如 GitHub id 与本地用户的映射关系）。
- 处理 GitHub 账号注销、解绑等场景。

## 五、时间安排（建议）
| 阶段         | 任务内容                         | 预计用时 |
| ------------ | -------------------------------- | -------- |
| 需求分析     | 明确业务流程、接口设计           | 0.5 天   |
| 技术调研     | OAuth2、Spring Security 配置     | 0.5 天   |
| 后端开发     | 配置 OAuth2、实现登录逻辑        | 1 天     |
| 前端开发     | 登录按钮、流程对接               | 0.5 天   |
| 测试与优化   | 全流程测试、异常处理、安全加固   | 0.5 天   |
| **合计**     |                                  | **3 天** |

---

如有问题或需调整，欢迎随时沟通。 