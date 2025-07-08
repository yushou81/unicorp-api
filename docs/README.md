# UniCorp API文档

本目录包含UniCorp平台的API文档，使用OpenAPI 3.0.3规范编写。

## 文档列表

- [社区模块API文档](openapi-community-api.yaml) - 包含社区话题、问题、回答、评论、标签和板块相关API
- [社区通知模块API文档](openapi-community-notifications-api.yaml) - 包含社区通知相关API

## 如何使用

### 在线查看

1. 使用[Swagger Editor](https://editor.swagger.io/)
   - 复制YAML文件内容
   - 粘贴到Swagger Editor中查看

2. 使用[Redoc](https://redocly.github.io/redoc/)
   - 上传YAML文件
   - 使用Redoc查看更友好的文档界面

### 本地查看

1. 安装Swagger UI
   ```bash
   npm install -g swagger-ui-cli
   ```

2. 运行Swagger UI
   ```bash
   swagger-ui-cli serve openapi-community-api.yaml
   ```

3. 在浏览器中打开 http://localhost:8080 查看文档

## 文档结构

每个API文档包含以下部分：

- 基本信息：标题、描述、版本等
- 服务器信息：API的基础URL
- 标签：API的分类
- 组件：通用的数据模型和安全定义
- 路径：API的具体端点和操作

## 认证方式

API使用JWT Bearer Token进行认证，在请求头中添加：

```
Authorization: Bearer <your_token>
```

## 响应格式

所有API响应都遵循统一的格式：

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    // 响应数据
  },
  "success": true
}
```

## 错误处理

常见的HTTP状态码：

- 200: 成功
- 400: 请求参数错误
- 401: 未授权（未登录）
- 403: 无权限
- 404: 资源不存在
- 500: 服务器内部错误

## 分页格式

分页响应的格式：

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "records": [
      // 数据列表
    ],
    "total": 100,
    "size": 10,
    "current": 1,
    "pages": 10
  },
  "success": true
}
``` 