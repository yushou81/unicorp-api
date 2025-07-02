# 专利与著作权资源测试计划

## 1. 环境准备

- 导入Postman Collection
- 设置环境变量：
  - `host`: API服务器地址，如 `http://localhost:8080`
  - `token`: 登录后获取的JWT令牌

## 2. 前置条件

1. 运行最新的数据库迁移脚本，确保resources表有image_url字段
2. 创建测试用户，分配教师或企业导师角色
3. 准备测试用的专利文件和图片
4. 准备测试用的著作权文件和图片

## 3. 测试流程

### 3.1 创建专利资源（同时上传文件和图片）

#### 请求

```
POST {{host}}/v1/resources/upload
Authorization: Bearer {{token}}
Content-Type: multipart/form-data

title: 测试专利资源
resourceType: 专利
description: 这是一个测试专利资源，包含文件和图片
visibility: public
file: [选择专利PDF文件]
image: [选择专利证书图片]
```

#### 期望响应

```json
{
  "code": 0,
  "message": "资源创建成功",
  "data": {
    "id": 1,
    "title": "测试专利资源",
    "description": "这是一个测试专利资源，包含文件和图片",
    "resourceType": "专利",
    "fileUrl": "[文件URL]",
    "imageUrl": "[图片URL]",
    "createdAt": "[创建时间]",
    "nickname": "[上传者昵称]",
    "organizationName": "[组织名称]"
  }
}
```

### 3.2 创建著作权资源（同时上传文件和图片）

#### 请求

```
POST {{host}}/v1/resources/upload
Authorization: Bearer {{token}}
Content-Type: multipart/form-data

title: 测试著作权资源
resourceType: 著作权
description: 这是一个测试著作权资源，包含文件和图片
visibility: public
file: [选择著作权PDF文件]
image: [选择著作权证书图片]
```

#### 期望响应

```json
{
  "code": 0,
  "message": "资源创建成功",
  "data": {
    "id": 2,
    "title": "测试著作权资源",
    "description": "这是一个测试著作权资源，包含文件和图片",
    "resourceType": "著作权",
    "fileUrl": "[文件URL]",
    "imageUrl": "[图片URL]",
    "createdAt": "[创建时间]",
    "nickname": "[上传者昵称]",
    "organizationName": "[组织名称]"
  }
}
```

### 3.3 创建专利资源（仅上传图片）

#### 请求

```
POST {{host}}/v1/resources/upload
Authorization: Bearer {{token}}
Content-Type: multipart/form-data

title: 仅图片的专利资源
resourceType: 专利
description: 这是一个只有图片的专利资源
visibility: public
image: [选择专利证书图片]
```

#### 期望响应

```json
{
  "code": 0,
  "message": "资源创建成功",
  "data": {
    "id": 3,
    "title": "仅图片的专利资源",
    "description": "这是一个只有图片的专利资源",
    "resourceType": "专利",
    "fileUrl": null,
    "imageUrl": "[图片URL]",
    "createdAt": "[创建时间]",
    "nickname": "[上传者昵称]",
    "organizationName": "[组织名称]"
  }
}
```

### 3.4 创建专利资源（仅上传文件）

#### 请求

```
POST {{host}}/v1/resources/upload
Authorization: Bearer {{token}}
Content-Type: multipart/form-data

title: 仅文件的专利资源
resourceType: 专利
description: 这是一个只有文件的专利资源
visibility: public
file: [选择专利PDF文件]
```

#### 期望响应

```json
{
  "code": 0,
  "message": "资源创建成功",
  "data": {
    "id": 4,
    "title": "仅文件的专利资源",
    "description": "这是一个只有文件的专利资源",
    "resourceType": "专利",
    "fileUrl": "[文件URL]",
    "imageUrl": "[文件URL]",  // imageUrl应该等于fileUrl
    "createdAt": "[创建时间]",
    "nickname": "[上传者昵称]",
    "organizationName": "[组织名称]"
  }
}
```

### 3.5 创建专利资源（无文件无图片 - 错误测试）

#### 请求

```
POST {{host}}/v1/resources/upload
Authorization: Bearer {{token}}
Content-Type: multipart/form-data

title: 无文件无图片的专利资源
resourceType: 专利
description: 这是一个测试错误情况的专利资源
visibility: public
```

#### 期望响应

```json
{
  "code": 400,
  "message": "专利/著作权类型资源必须提供文件或图片"
}
```

### 3.6 获取专利资源详情

#### 请求

```
GET {{host}}/v1/resources/1
Authorization: Bearer {{token}}
```

#### 期望响应

```json
{
  "code": 0,
  "message": "获取资源详情成功",
  "data": {
    "id": 1,
    "title": "测试专利资源",
    "description": "这是一个测试专利资源，包含文件和图片",
    "resourceType": "专利",
    "fileUrl": "[文件URL]",
    "imageUrl": "[图片URL]",
    "createdAt": "[创建时间]",
    "nickname": "[上传者昵称]",
    "organizationName": "[组织名称]"
  }
}
```

### 3.7 更新专利资源（更新图片）

#### 请求

```
PUT {{host}}/v1/resources/1/upload
Authorization: Bearer {{token}}
Content-Type: multipart/form-data

title: 更新后的专利资源
resourceType: 专利
description: 这是更新后的专利资源描述
visibility: public
image: [选择新的专利证书图片]
```

#### 期望响应

```json
{
  "code": 0,
  "message": "资源更新成功",
  "data": {
    "id": 1,
    "title": "更新后的专利资源",
    "description": "这是更新后的专利资源描述",
    "resourceType": "专利",
    "fileUrl": "[原文件URL]",
    "imageUrl": "[新图片URL]",
    "createdAt": "[创建时间]",
    "nickname": "[上传者昵称]",
    "organizationName": "[组织名称]"
  }
}
```

### 3.8 删除专利资源

#### 请求

```
DELETE {{host}}/v1/resources/1
Authorization: Bearer {{token}}
```

#### 期望响应

```
204 No Content
```

## 4. 边界测试

### 4.1 资源类型边界测试

- 尝试使用不存在的资源类型（应当返回错误）
- 尝试使用空资源类型（应当返回错误）

### 4.2 文件类型边界测试

- 尝试上传不支持的文件类型（如exe文件，应当返回错误）
- 尝试上传超大文件（应当返回错误）

### 4.3 权限边界测试

- 使用普通学生用户token创建专利资源（应当返回权限错误）
- 使用其他用户token更新他人的专利资源（应当返回权限错误）
- 使用其他用户token删除他人的专利资源（应当返回权限错误）

## 5. 注意事项

1. 所有请求需要先登录获取token
2. 专利和著作权类型资源必须至少提供文件或图片之一
3. 测试时需要准备实际的文件和图片
4. 更新资源时，如果不提供新的文件或图片，将保留原有的文件或图片
5. 删除资源后，应当无法再通过ID获取该资源 