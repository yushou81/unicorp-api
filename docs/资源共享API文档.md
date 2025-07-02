
## 1. 资源管理API

### 1.1 获取资源列表

获取所有已发布的资源列表，支持分页和搜索。

#### 请求

```
GET /v1/resources?page={page}&size={size}&keyword={keyword}
```

**参数说明：**

| 参数名 | 类型 | 必填 | 说明 |
|-------|------|------|------|
| page | Integer | 否 | 页码，默认为0 |
| size | Integer | 否 | 每页大小，默认为10 |
| keyword | String | 否 | 搜索关键词 |

#### 响应

```json
{
  "code": 0,
  "message": "获取资源列表成功",
  "data": {
    "records": [
      {
        "id": 1,
        "title": "资源标题",
        "description": "资源描述",
        "resourceType": "技术文档",
        "fileUrl": "http://example.com/files/resource.pdf",
        "imageUrl": null,
        "createdAt": "2025-07-01T12:00:00",
        "nickname": "张教授",
        "organizationName": "计算机科学学院"
      },
      // 更多资源...
    ],
    "total": 100,
    "size": 10,
    "current": 1,
    "pages": 10
  }
}
```

### 1.2 获取资源详情

根据ID获取资源的详细信息。

#### 请求

```
GET /v1/resources/{id}
```

**参数说明：**

| 参数名 | 类型 | 必填 | 说明 |
|-------|------|------|------|
| id | Integer | 是 | 资源ID |

#### 响应

```json
{
  "code": 0,
  "message": "获取资源详情成功",
  "data": {
    "id": 1,
    "title": "资源标题",
    "description": "资源描述",
    "resourceType": "技术文档",
    "fileUrl": "http://example.com/files/resource.pdf",
    "imageUrl": null,
    "createdAt": "2025-07-01T12:00:00",
    "nickname": "张教授",
    "organizationName": "计算机科学学院"
  }
}
```

### 1.3 创建资源（JSON方式）

由教师或企业导师调用，用于发布一个新的共享资源。

#### 请求

```
POST /v1/resources/
Content-Type: application/json
Authorization: Bearer {token}

{
  "title": "资源标题",
  "description": "资源描述",
  "resourceType": "技术文档",
  "fileUrl": "http://example.com/files/resource.pdf",
  "visibility": "public"
}
```

**参数说明：**

| 参数名 | 类型 | 必填 | 说明 |
|-------|------|------|------|
| title | String | 是 | 资源标题 |
| description | String | 否 | 资源描述 |
| resourceType | String | 是 | 资源类型，如技术文档、教学课件、案例分析、专利、著作权等 |
| fileUrl | String | 是 | 文件URL，需要先上传文件获取URL |
| imageUrl | String | 否 | 图片URL，专利、著作权类型可用 |
| visibility | String | 否 | 可见性，默认为public，可选值：public、private、organization |

#### 响应

```json
{
  "code": 0,
  "message": "资源创建成功",
  "data": {
    "id": 1,
    "title": "资源标题",
    "description": "资源描述",
    "resourceType": "技术文档",
    "fileUrl": "http://example.com/files/resource.pdf",
    "imageUrl": null,
    "createdAt": "2025-07-01T12:00:00",
    "nickname": "张教授",
    "organizationName": "计算机科学学院"
  }
}
```

### 1.4 上传资源文件并创建资源

一步完成文件上传和资源创建。

#### 请求

```
POST /v1/resources/upload
Content-Type: multipart/form-data
Authorization: Bearer {token}

title: 资源标题
resourceType: 技术文档
description: 资源描述
visibility: public
file: [文件]
image: [图片，仅专利/著作权类型可选]
```

**参数说明：**

| 参数名 | 类型 | 必填 | 说明 |
|-------|------|------|------|
| title | String | 是 | 资源标题 |
| resourceType | String | 是 | 资源类型，如技术文档、教学课件、案例分析、专利、著作权等 |
| description | String | 否 | 资源描述 |
| visibility | String | 否 | 可见性，默认为public，可选值：public、private、organization |
| file | File | 条件必填 | 资源文件，非专利/著作权类型必填 |
| image | File | 否 | 资源图片，专利/著作权类型可用 |

**注意：**
- 对于普通资源类型（非专利/著作权），必须提供文件
- 对于专利/著作权类型，至少需要提供文件或图片之一

#### 响应

```json
{
  "code": 0,
  "message": "资源创建成功",
  "data": {
    "id": 1,
    "title": "资源标题",
    "description": "资源描述",
    "resourceType": "技术文档",
    "fileUrl": "http://example.com/files/resource.pdf",
    "imageUrl": null,
    "createdAt": "2025-07-01T12:00:00",
    "nickname": "张教授",
    "organizationName": "计算机科学学院"
  }
}
```

### 1.5 上传文件并更新资源

一步完成文件上传和资源更新。

#### 请求

```
POST /v1/resources/{id}/upload
Content-Type: multipart/form-data
Authorization: Bearer {token}

title: 更新后的资源标题
resourceType: 技术文档
description: 更新后的资源描述
visibility: public
file: [新文件，可选]
image: [新图片，可选]
```

**参数说明：**

| 参数名 | 类型 | 必填 | 说明 |
|-------|------|------|------|
| id | Integer | 是 | 资源ID |
| title | String | 是 | 更新后的资源标题 |
| resourceType | String | 否 | 资源类型 |
| description | String | 否 | 更新后的资源描述 |
| visibility | String | 否 | 可见性 |
| file | File | 否 | 新的资源文件，不提供则保留原文件 |
| image | File | 否 | 新的资源图片，不提供则保留原图片 |

#### 响应

```json
{
  "code": 0,
  "message": "资源更新成功",
  "data": {
    "id": 1,
    "title": "更新后的资源标题",
    "description": "更新后的资源描述",
    "resourceType": "技术文档",
    "fileUrl": "http://example.com/files/updated_resource.pdf",
    "imageUrl": null,
    "createdAt": "2025-07-01T12:00:00",
    "nickname": "张教授",
    "organizationName": "计算机科学学院"
  }
}
```

### 1.6 删除资源

由资源所有者调用，用于删除资源。

#### 请求

```
DELETE /v1/resources/{id}
Authorization: Bearer {token}
```

**参数说明：**

| 参数名 | 类型 | 必填 | 说明 |
|-------|------|------|------|
| id | Integer | 是 | 资源ID |

#### 响应

成功删除时返回204 No Content，无响应体。

### 1.7 下载资源文件

下载指定ID的资源文件。

#### 请求

```
GET /v1/resources/{id}/download
Authorization: Bearer {token}
```

**参数说明：**

| 参数名 | 类型 | 必填 | 说明 |
|-------|------|------|------|
| id | Integer | 是 | 资源ID |

#### 响应

直接返回文件流，Content-Type为application/octet-stream，并设置Content-Disposition头指示浏览器下载文件。

## 2. 实验设备预约API

### 2.1 申请使用实验设备

创建设备使用申请。

#### 请求

```
POST /v1/resources/equipment/bookings
Content-Type: application/json
Authorization: Bearer {token}

{
  "resourceId": 1,
  "startTime": "2025-07-10T09:00:00",
  "endTime": "2025-07-10T12:00:00",
  "purpose": "进行网络安全实验"
}
```

**参数说明：**

| 参数名 | 类型 | 必填 | 说明 |
|-------|------|------|------|
| resourceId | Integer | 是 | 设备资源ID |
| startTime | DateTime | 是 | 开始使用时间 |
| endTime | DateTime | 是 | 结束使用时间 |
| purpose | String | 是 | 使用目的 |

#### 响应

```json
{
  "code": 0,
  "message": "设备预约申请成功",
  "data": {
    "id": 1,
    "resourceId": 1,
    "resourceTitle": "网络安全实验设备",
    "userId": 100,
    "userName": "张三",
    "startTime": "2025-07-10T09:00:00",
    "endTime": "2025-07-10T12:00:00",
    "purpose": "进行网络安全实验",
    "status": "PENDING",
    "rejectReason": null,
    "reviewerId": null,
    "reviewerName": null,
    "createdAt": "2025-07-01T12:00:00",
    "updatedAt": "2025-07-01T12:00:00"
  }
}
```

### 2.2 获取预约列表

获取设备预约列表，支持分页和搜索。

#### 请求

```
GET /v1/resources/equipment/bookings?page={page}&size={size}&userId={userId}&resourceId={resourceId}&status={status}&organizationId={organizationId}
Authorization: Bearer {token}
```

**参数说明：**

| 参数名 | 类型 | 必填 | 说明 |
|-------|------|------|------|
| page | Integer | 否 | 页码，默认为0 |
| size | Integer | 否 | 每页大小，默认为10 |
| userId | Integer | 否 | 用户ID，筛选特定用户的预约 |
| resourceId | Integer | 否 | 资源ID，筛选特定设备的预约 |
| status | String | 否 | 预约状态，如PENDING、APPROVED、REJECTED、CANCELED |
| organizationId | Integer | 否 | 组织ID，筛选特定组织的预约 |

#### 响应

```json
{
  "code": 0,
  "message": "获取预约列表成功",
  "data": {
    "records": [
      {
        "id": 1,
        "resourceId": 1,
        "resourceTitle": "网络安全实验设备",
        "userId": 100,
        "userName": "张三",
        "startTime": "2025-07-10T09:00:00",
        "endTime": "2025-07-10T12:00:00",
        "purpose": "进行网络安全实验",
        "status": "PENDING",
        "rejectReason": null,
        "reviewerId": null,
        "reviewerName": null,
        "createdAt": "2025-07-01T12:00:00",
        "updatedAt": "2025-07-01T12:00:00"
      },
      // 更多预约...
    ],
    "total": 50,
    "size": 10,
    "current": 1,
    "pages": 5
  }
}
```

### 2.3 获取预约详情

根据ID获取预约详情。

#### 请求

```
GET /v1/resources/equipment/bookings/{id}
Authorization: Bearer {token}
```

**参数说明：**

| 参数名 | 类型 | 必填 | 说明 |
|-------|------|------|------|
| id | Integer | 是 | 预约ID |

#### 响应

```json
{
  "code": 0,
  "message": "获取预约详情成功",
  "data": {
    "id": 1,
    "resourceId": 1,
    "resourceTitle": "网络安全实验设备",
    "userId": 100,
    "userName": "张三",
    "startTime": "2025-07-10T09:00:00",
    "endTime": "2025-07-10T12:00:00",
    "purpose": "进行网络安全实验",
    "status": "PENDING",
    "rejectReason": null,
    "reviewerId": null,
    "reviewerName": null,
    "createdAt": "2025-07-01T12:00:00",
    "updatedAt": "2025-07-01T12:00:00"
  }
}
```

### 2.4 取消预约

取消自己的预约。

#### 请求

```
POST /v1/resources/equipment/bookings/{id}/cancel
Authorization: Bearer {token}
```

**参数说明：**

| 参数名 | 类型 | 必填 | 说明 |
|-------|------|------|------|
| id | Integer | 是 | 预约ID |

#### 响应

```json
{
  "code": 0,
  "message": "预约取消成功",
  "data": {
    "id": 1,
    "resourceId": 1,
    "resourceTitle": "网络安全实验设备",
    "userId": 100,
    "userName": "张三",
    "startTime": "2025-07-10T09:00:00",
    "endTime": "2025-07-10T12:00:00",
    "purpose": "进行网络安全实验",
    "status": "CANCELED",
    "rejectReason": null,
    "reviewerId": null,
    "reviewerName": null,
    "createdAt": "2025-07-01T12:00:00",
    "updatedAt": "2025-07-01T12:30:00"
  }
}
```

### 2.5 审核预约

批准或拒绝预约申请。

#### 请求

```
POST /v1/resources/equipment/bookings/review
Content-Type: application/json
Authorization: Bearer {token}

{
  "bookingId": 1,
  "approved": true,
  "rejectReason": null
}
```

**参数说明：**

| 参数名 | 类型 | 必填 | 说明 |
|-------|------|------|------|
| bookingId | Integer | 是 | 预约ID |
| approved | Boolean | 是 | 是否批准，true为批准，false为拒绝 |
| rejectReason | String | 条件必填 | 拒绝原因，当approved为false时必填 |

#### 响应

```json
{
  "code": 0,
  "message": "预约审核完成",
  "data": {
    "id": 1,
    "resourceId": 1,
    "resourceTitle": "网络安全实验设备",
    "userId": 100,
    "userName": "张三",
    "startTime": "2025-07-10T09:00:00",
    "endTime": "2025-07-10T12:00:00",
    "purpose": "进行网络安全实验",
    "status": "APPROVED",
    "rejectReason": null,
    "reviewerId": 200,
    "reviewerName": "李教授",
    "createdAt": "2025-07-01T12:00:00",
    "updatedAt": "2025-07-01T14:00:00"
  }
}
```

## 3. 专利与著作权资源特殊说明

专利和著作权类型的资源具有以下特点：

1. 可以同时拥有文件（fileUrl）和图片（imageUrl）
2. 创建时至少需要提供文件或图片之一
3. 如果只提供了文件没有提供图片，系统会自动将文件URL作为图片URL
4. 前端展示时应优先使用imageUrl来展示资源的图片预览

### 3.1 创建专利/著作权资源示例

```
POST /v1/resources/upload
Content-Type: multipart/form-data
Authorization: Bearer {token}

title: 一种新型计算机网络安全防护方法
resourceType: 专利
description: 本专利提出了一种基于人工智能的网络安全防护方法
visibility: public
file: [专利文件.pdf]
image: [专利证书图片.jpg]
```

### 3.2 仅上传图片的专利资源示例

```
POST /v1/resources/upload
Content-Type: multipart/form-data
Authorization: Bearer {token}

title: 仅图片的专利资源
resourceType: 专利
description: 这是一个只有图片的专利资源
visibility: public
image: [专利证书图片.jpg]
```

### 3.3 仅上传文件的专利资源示例

```
POST /v1/resources/upload
Content-Type: multipart/form-data
Authorization: Bearer {token}

title: 仅文件的专利资源
resourceType: 专利
description: 这是一个只有文件的专利资源
visibility: public
file: [专利文件.pdf]
```

## 4. 权限说明

1. 资源创建：仅教师或企业导师可以创建资源
2. 资源更新：仅资源所有者或管理员可以更新资源
3. 资源删除：仅资源所有者或管理员可以删除资源
4. 资源下载：所有用户都可以下载公开资源
5. 设备预约：所有登录用户都可以申请使用设备
6. 预约审核：仅设备管理员或管理员可以审核预约申请

## 5. 错误码说明

| 错误码 | 说明 |
|-------|------|
| 200 | 成功 |
| 201 | 创建成功 |
| 204 | 删除成功 |
| 400 | 请求参数错误 |
| 401 | 未授权（未登录或token无效） |
| 403 | 权限不足 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 | 