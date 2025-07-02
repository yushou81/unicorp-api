# 专利与著作权资源API文档

本文档详细说明了专利和著作权类型资源的API使用方法，包括创建、获取、更新和删除操作，以及Postman测试方法。

## 1. 资源类型说明

系统支持多种资源类型，其中包括专利（专利）和著作权（著作权）类型。这两种特殊类型的资源具有以下特点：

- 可以同时拥有文件（fileUrl）和图片（imageUrl）
- 创建时至少需要提供文件或图片之一
- 如果只提供了文件没有提供图片，系统会自动将文件URL作为图片URL
- 前端展示时应优先使用imageUrl来展示资源的图片预览

## 2. API接口说明

### 2.1 创建专利/著作权资源

#### 请求

```
POST {{host}}/v1/resources/upload
Authorization: Bearer {{token}}
Content-Type: multipart/form-data

title: 一种新型计算机网络安全防护方法
resourceType: 专利
description: 本专利提出了一种基于人工智能的网络安全防护方法
visibility: public
file: [专利文件.pdf]
image: [专利证书图片.jpg]
```

#### 响应

```json
{
  "code": 0,
  "message": "资源创建成功",
  "data": {
    "id": 123,
    "title": "一种新型计算机网络安全防护方法",
    "description": "本专利提出了一种基于人工智能的网络安全防护方法",
    "resourceType": "专利",
    "fileUrl": "http://example.com/uploads/resources/patent_file_123.pdf",
    "imageUrl": "http://example.com/uploads/resource_images/patent_image_123.jpg",
    "createdAt": "2025-07-01T12:34:56",
    "nickname": "张教授",
    "organizationName": "计算机科学学院"
  }
}
```

### 2.2 获取专利/著作权资源详情

#### 请求

```
GET {{host}}/v1/resources/123
Authorization: Bearer {{token}}
```

#### 响应

```json
{
  "code": 0,
  "message": "获取资源详情成功",
  "data": {
    "id": 123,
    "title": "一种新型计算机网络安全防护方法",
    "description": "本专利提出了一种基于人工智能的网络安全防护方法",
    "resourceType": "专利",
    "fileUrl": "http://example.com/uploads/resources/patent_file_123.pdf",
    "imageUrl": "http://example.com/uploads/resource_images/patent_image_123.jpg",
    "createdAt": "2025-07-01T12:34:56",
    "nickname": "张教授",
    "organizationName": "计算机科学学院"
  }
}
```

### 2.3 更新专利/著作权资源

#### 请求

```
PUT {{host}}/v1/resources/123/upload
Authorization: Bearer {{token}}
Content-Type: multipart/form-data

title: 一种改进的计算机网络安全防护方法
resourceType: 专利
description: 本专利提出了一种基于深度学习的网络安全防护方法
visibility: public
file: [更新后的专利文件.pdf] (可选)
image: [更新后的专利证书图片.jpg] (可选)
```

#### 响应

```json
{
  "code": 0,
  "message": "资源更新成功",
  "data": {
    "id": 123,
    "title": "一种改进的计算机网络安全防护方法",
    "description": "本专利提出了一种基于深度学习的网络安全防护方法",
    "resourceType": "专利",
    "fileUrl": "http://example.com/uploads/resources/patent_file_123_updated.pdf",
    "imageUrl": "http://example.com/uploads/resource_images/patent_image_123_updated.jpg",
    "createdAt": "2025-07-01T12:34:56",
    "nickname": "张教授",
    "organizationName": "计算机科学学院"
  }
}
```

### 2.4 删除专利/著作权资源

#### 请求

```
DELETE {{host}}/v1/resources/123
Authorization: Bearer {{token}}
```

#### 响应

```
204 No Content
```

## 3. Postman测试方法

### 3.1 创建专利资源测试

1. 选择 POST 方法，输入URL: `{{host}}/v1/resources/upload`
2. 在Headers中添加:
   - Authorization: Bearer {{token}}
3. 选择Body > form-data，添加以下字段:
   - title: 测试专利资源
   - resourceType: 专利
   - description: 这是一个测试专利资源
   - visibility: public
   - file: 选择一个PDF文件作为专利文件
   - image: 选择一个JPG/PNG文件作为专利证书图片
4. 点击Send，检查响应是否成功，并记录返回的资源ID

### 3.2 创建著作权资源测试

1. 选择 POST 方法，输入URL: `{{host}}/v1/resources/upload`
2. 在Headers中添加:
   - Authorization: Bearer {{token}}
3. 选择Body > form-data，添加以下字段:
   - title: 测试著作权资源
   - resourceType: 著作权
   - description: 这是一个测试著作权资源
   - visibility: public
   - file: 选择一个PDF文件作为著作权文件
   - image: 选择一个JPG/PNG文件作为著作权证书图片
4. 点击Send，检查响应是否成功，并记录返回的资源ID

### 3.3 仅上传图片的测试

1. 选择 POST 方法，输入URL: `{{host}}/v1/resources/upload`
2. 在Headers中添加:
   - Authorization: Bearer {{token}}
3. 选择Body > form-data，添加以下字段:
   - title: 仅图片的专利资源
   - resourceType: 专利
   - description: 这是一个只有图片的专利资源
   - visibility: public
   - image: 选择一个JPG/PNG文件作为专利证书图片
4. 点击Send，检查响应是否成功

### 3.4 仅上传文件的测试

1. 选择 POST 方法，输入URL: `{{host}}/v1/resources/upload`
2. 在Headers中添加:
   - Authorization: Bearer {{token}}
3. 选择Body > form-data，添加以下字段:
   - title: 仅文件的专利资源
   - resourceType: 专利
   - description: 这是一个只有文件的专利资源
   - visibility: public
   - file: 选择一个PDF文件作为专利文件
4. 点击Send，检查响应是否成功，并验证返回的imageUrl是否与fileUrl相同

### 3.5 错误测试 - 既没有文件也没有图片

1. 选择 POST 方法，输入URL: `{{host}}/v1/resources/upload`
2. 在Headers中添加:
   - Authorization: Bearer {{token}}
3. 选择Body > form-data，添加以下字段:
   - title: 无文件无图片的专利资源
   - resourceType: 专利
   - description: 这是一个测试错误情况的专利资源
   - visibility: public
4. 点击Send，验证是否返回400错误，错误信息应为"专利/著作权类型资源必须提供文件或图片"

## 4. 前端展示建议

1. 在资源列表和详情页面中，对于专利和著作权类型的资源:
   - 使用imageUrl来展示资源的图片预览
   - 提供fileUrl的下载链接，让用户可以下载完整的文件
   
2. 在资源上传表单中:
   - 对于专利和著作权类型，允许用户同时上传文件和图片，但至少要求提供一项
   - 对于其他类型的资源，要求必须提供文件

3. 在资源编辑表单中:
   - 显示当前的文件和图片预览
   - 允许用户更新文件和/或图片，或保持原样 