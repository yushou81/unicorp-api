# GitHub登录功能使用文档

## 概述

本文档介绍如何在前端应用中集成UniCorp平台的GitHub登录功能，使用户能够通过GitHub账号快速登录系统。

## 前端集成步骤

### 1. 获取GitHub登录URL

前端应用需要调用API获取GitHub登录URL：

```javascript
async function getGitHubLoginUrl() {
  const response = await fetch('http://localhost:8081/api/v1/auth/github/login-url');
  const data = await response.json();
  
  if (data.code === 200) {
    // 返回的是相对路径，需要拼接完整URL
    return `http://localhost:8081/api${data.data}`;
  }
  throw new Error('获取GitHub登录URL失败');
}
```

### 2. 创建GitHub登录按钮

在登录页面添加"使用GitHub登录"按钮，并绑定点击事件：

```html
<!-- 登录页面HTML -->
<button id="github-login-btn" class="github-login-button">
  <i class="github-icon"></i> 使用GitHub登录
</button>

<script>
  document.getElementById('github-login-btn').addEventListener('click', async () => {
    try {
      const loginUrl = await getGitHubLoginUrl();
      // 重定向到GitHub授权页面
      window.location.href = loginUrl;
    } catch (error) {
      console.error('GitHub登录失败:', error);
    }
  });
</script>
```

### 3. 处理登录回调

用户在GitHub授权后，系统会将用户重定向到前端页面：`http://localhost:8082/login-success`，并携带以下参数：
- `token`: JWT访问令牌
- `refreshToken`: 刷新令牌
- `nickname`: 用户昵称
- `role`: 用户角色
- `avatar`: 用户头像URL（如果有）

在前端应用中创建`/login-success`页面，处理回调：

```javascript
// login-success.js
function handleLoginSuccess() {
  // 解析URL参数
  const urlParams = new URLSearchParams(window.location.search);
  const token = urlParams.get('token');
  const refreshToken = urlParams.get('refreshToken');
  const nickname = urlParams.get('nickname');
  const role = urlParams.get('role');
  const avatar = urlParams.get('avatar');
  
  if (token) {
    // 保存令牌
    localStorage.setItem('token', token);
    localStorage.setItem('refreshToken', refreshToken);
    
    // 保存用户信息
    localStorage.setItem('user', JSON.stringify({
      nickname,
      role,
      avatar
    }));
    
    // 更新认证状态
    updateAuthState(true);
    
    // 重定向到主页
    window.location.href = '/home';
  } else {
    // 处理登录失败情况
    console.error('登录失败：未获取到令牌');
    window.location.href = '/login?error=auth_failed';
  }
}

// 页面加载时执行
document.addEventListener('DOMContentLoaded', handleLoginSuccess);
```

### 4. 完整登录流程

1. 用户点击"使用GitHub登录"按钮
2. 前端调用API获取GitHub登录URL
3. 前端重定向用户到GitHub授权页面
4. 用户在GitHub上授权应用访问
5. GitHub重定向回应用的回调URL
6. 后端处理回调，创建或登录用户
7. 后端重定向到前端的`login-success`页面，带上token等参数
8. 前端解析参数，保存token，完成登录

## 示例代码

### 完整的GitHub登录模块

```javascript
// github-login.js

// 获取GitHub登录URL
async function getGitHubLoginUrl() {
  try {
    const response = await fetch('http://localhost:8081/api/v1/auth/github/login-url');
    const data = await response.json();
    
    if (data.code === 200) {
      return `http://localhost:8081/api${data.data}`;
    }
    throw new Error(data.message || '获取GitHub登录URL失败');
  } catch (error) {
    console.error('获取GitHub登录URL失败:', error);
    throw error;
  }
}

// 初始化GitHub登录按钮
function initGitHubLogin() {
  const githubLoginBtn = document.getElementById('github-login-btn');
  if (githubLoginBtn) {
    githubLoginBtn.addEventListener('click', async () => {
      try {
        // 显示加载状态
        githubLoginBtn.disabled = true;
        githubLoginBtn.textContent = '正在跳转...';
        
        const loginUrl = await getGitHubLoginUrl();
        window.location.href = loginUrl;
      } catch (error) {
        console.error('GitHub登录失败:', error);
        
        // 恢复按钮状态
        githubLoginBtn.disabled = false;
        githubLoginBtn.innerHTML = '<i class="github-icon"></i> 使用GitHub登录';
        
        // 显示错误信息
        showError('GitHub登录失败，请稍后重试');
      }
    });
  }
}

// 页面加载时初始化
document.addEventListener('DOMContentLoaded', initGitHubLogin);
```

## 注意事项

1. **端口配置**：
   - 后端API服务运行在 `http://localhost:8081/api`
   - 前端应用假设运行在 `http://localhost:8082`
   - 如果端口有变化，需要相应调整配置

2. **首次登录**：
   - 用户首次通过GitHub登录时，系统会自动创建账号并分配学生角色
   - 用户信息会从GitHub获取，包括昵称、邮箱等

3. **安全考虑**：
   - 前端应该验证接收到的token有效性
   - 使用HTTPS保护传输过程中的敏感信息
   - 定期刷新token以提高安全性

4. **跨域问题**：
   - 如果前后端部署在不同域名下，需要确保后端正确配置了CORS
   - 前端请求需要设置正确的credentials选项

## 常见问题

### Q: 用户取消GitHub授权后会发生什么？
A: 用户取消授权后，GitHub不会重定向到我们的回调URL，用户会停留在GitHub页面。

### Q: 如何处理GitHub账号与已有账号的绑定？
A: 当前版本未实现账号绑定功能，用户首次通过GitHub登录时会自动创建新账号。

### Q: 如何更新GitHub登录后的用户信息？
A: 用户登录成功后，可以通过调用`/v1/auth/me`接口获取最新的用户信息。

### Q: 如何处理GitHub API访问限制？
A: GitHub API有访问频率限制，建议在前端添加适当的错误处理和重试机制。 