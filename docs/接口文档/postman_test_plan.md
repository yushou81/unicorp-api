# 实验设备资源管理与预约审核测试方案

## 1. 环境准备

- 导入Postman Collection
- 设置环境变量：
  - `host`: API服务器地址，如 `http://localhost:8080`
  - `token`: 登录后获取的JWT令牌

## 2. 前置条件

1. 运行数据库迁移脚本，确保数据库表结构正确
2. 创建测试用户，分配角色（普通学生、教师、管理员）
3. 创建测试用资源（实验设备类型）

## 3. 测试流程

### 3.1 创建实验设备资源

#### 请求

```
POST {{host}}/v1/resources
Authorization: Bearer {{token}}
Content-Type: application/json

{
  "title": "光学显微镜",
  "resourceType": "equipment",
  "description": "最新型号的光学显微镜，可用于观察细胞组织等微观结构",
  "location": "实验楼203室",
  "organizationId": 1,
  "visibility": "organization_only"
}
```

#### 期望响应

```json
{
  "code": 0,
  "message": "资源创建成功",
  "data": {
    "id": 1,
    "title": "光学显微镜",
    "resourceType": "equipment",
    "description": "最新型号的光学显微镜，可用于观察细胞组织等微观结构",
    "location": "实验楼203室",
    "organizationId": 1,
    "visibility": "organization_only",
    "createdAt": "2023-06-30T15:30:00"
  }
}
```

### 3.2 申请使用实验设备

#### 请求 (正常申请)

```
POST {{host}}/v1/resources/bookings
Authorization: Bearer {{token}} (学生用户)
Content-Type: application/json

{
  "resourceId": 1,
  "startTime": "2025-07-05T10:00:00",
  "endTime": "2025-07-05T12:00:00",
  "purpose": "细胞观察实验"
}
```

#### 期望响应

```json
{
  "code": 0,
  "message": "设备预约申请成功",
  "data": {
    "id": 1,
    "resourceId": 1,
    "resourceTitle": "光学显微镜",
    "userId": 123,
    "userName": "张三",
    "startTime": "2025-07-05T10:00:00",
    "endTime": "2025-07-05T12:00:00",
    "purpose": "细胞观察实验",
    "status": "PENDING",
    "createdAt": "2023-06-30T15:43:21"
  }
}
```

#### 请求 (时间冲突测试)

```
POST {{host}}/v1/resources/bookings
Authorization: Bearer {{token}} (另一个学生用户)
Content-Type: application/json

{
  "resourceId": 1,
  "startTime": "2025-07-05T11:00:00",
  "endTime": "2025-07-05T13:00:00",
  "purpose": "实验课程使用"
}
```

#### 期望响应 (当第一个预约已审核通过)

```json
{
  "code": 400,
  "message": "预约时间冲突：该时间段已有预约。冲突的预约：预约ID=1，时间=2025-07-05T10:00:00至2025-07-05T12:00:00"
}
```

### 3.3 获取预约列表

#### 请求 (管理员查看)

```
GET {{host}}/v1/resources/bookings?page=0&size=10&status=PENDING
Authorization: Bearer {{token}} (管理员用户)
```

#### 期望响应

```json
{
  "code": 0,
  "message": "获取预约列表成功",
  "data": {
    "records": [
      {
        "id": 1,
        "resourceId": 1,
        "resourceTitle": "光学显微镜",
        "userId": 123,
        "userName": "张三",
        "startTime": "2025-07-05T10:00:00",
        "endTime": "2025-07-05T12:00:00",
        "purpose": "细胞观察实验",
        "status": "PENDING",
        "createdAt": "2023-06-30T15:43:21"
      }
      // ... 其他预约记录
    ],
    "total": 5,
    "size": 10,
    "current": 0,
    "pages": 1
  }
}
```

### 3.4 预约审核 (批准)

#### 请求

```
POST {{host}}/v1/resources/bookings/review
Authorization: Bearer {{token}} (管理员或设备管理员用户)
Content-Type: application/json

{
  "bookingId": 1,
  "approve": true
}
```

#### 期望响应

```json
{
  "code": 0,
  "message": "预约审核完成",
  "data": {
    "id": 1,
    "resourceId": 1,
    "resourceTitle": "光学显微镜",
    "userId": 123,
    "userName": "张三",
    "startTime": "2025-07-05T10:00:00",
    "endTime": "2025-07-05T12:00:00",
    "purpose": "细胞观察实验",
    "status": "APPROVED",
    "reviewerId": 456,
    "reviewerName": "李老师",
    "updatedAt": "2023-06-30T16:20:05"
  }
}
```

### 3.5 预约审核 (拒绝)

#### 请求

```
POST {{host}}/v1/resources/bookings/review
Authorization: Bearer {{token}} (管理员或设备管理员用户)
Content-Type: application/json

{
  "bookingId": 2,
  "approve": false,
  "rejectReason": "该设备当天已安排维护"
}
```

#### 期望响应

```json
{
  "code": 0,
  "message": "预约审核完成",
  "data": {
    "id": 2,
    "resourceId": 1,
    "resourceTitle": "光学显微镜",
    "userId": 124,
    "userName": "李四",
    "startTime": "2025-07-06T10:00:00",
    "endTime": "2025-07-06T12:00:00",
    "purpose": "科研项目使用",
    "status": "REJECTED",
    "rejectReason": "该设备当天已安排维护",
    "reviewerId": 456,
    "reviewerName": "李老师",
    "updatedAt": "2023-06-30T16:30:15"
  }
}
```

### 3.6 取消预约

#### 请求

```
POST {{host}}/v1/resources/bookings/3/cancel
Authorization: Bearer {{token}} (预约用户)
```

#### 期望响应

```json
{
  "code": 0,
  "message": "预约取消成功",
  "data": {
    "id": 3,
    "resourceId": 1,
    "resourceTitle": "光学显微镜",
    "userId": 123,
    "userName": "张三",
    "startTime": "2025-07-07T10:00:00",
    "endTime": "2025-07-07T12:00:00",
    "purpose": "实验课程使用",
    "status": "CANCELED",
    "updatedAt": "2023-06-30T16:40:22"
  }
}
```

## 4. 边界测试

### 4.1 时间边界测试

- 尝试预约过去的时间（应当失败）
- 尝试预约结束时间早于开始时间（应当失败）
- 尝试预约与已审核通过预约的时间恰好相连（如第一个预约到12:00，第二个预约从12:00开始，应当成功）
- 尝试预约非设备类型的资源（应当失败）

### 4.2 权限边界测试

- 使用普通用户token审核预约（应当失败）
- 使用其他用户token取消他人的预约（应当失败）
- 使用非管理员用户查看所有预约（应当返回适合权限的结果）
- 尝试预约非本组织的设备资源（当visibility为organization_only时应当失败）

## 5. 注意事项

1. 所有请求需要先登录获取token
2. 时间冲突检查只针对已审核通过的预约
3. 已取消或已完成的预约不能再变更状态
4. 测试时使用2025年以后的日期，避免日期过期
5. 确保预约的资源类型为"equipment"，其他类型的资源不支持预约功能 