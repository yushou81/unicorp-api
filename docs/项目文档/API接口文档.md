# 校企联盟平台API接口文档

## API概述

校企联盟平台API采用RESTful风格设计，使用JWT进行身份验证。所有API请求均需要在Header中携带有效的Authorization令牌（除了公开接口）。

基础URL: `http://{server-address}:{port}/`

## 身份验证

### 登录

- **URL**: `/v1/auth/login`
- **方法**: POST
- **描述**: 用户登录并获取JWT令牌
- **请求体**:
  ```json
  {
    "account": "user123",
    "password": "password123"
  }
  ```
- **响应**:
  ```json
  {
    "code": 200,
    "message": "登录成功",
    "data": {
      "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
      "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
      "expiresIn": 86400,
      "user": {
        "id": 1,
        "account": "user123",
        "nickname": "用户昵称",
        "email": "user@example.com",
        "role": "STUDENT"
      }
    }
  }
  ```

### 注册

- **URL**: `/v1/auth/register/student`
- **方法**: POST
- **描述**: 学生用户注册
- **请求体**:
  ```json
  {
    "account": "student123",
    "password": "password123",
    "email": "student@example.com",
    "phone": "13800138000",
    "nickname": "学生昵称",
    "organizationId": 1
  }
  ```
- **响应**:
  ```json
  {
    "code": 200,
    "message": "注册成功",
    "data": {
      "id": 1,
      "account": "student123",
      "nickname": "学生昵称",
      "email": "student@example.com",
      "role": "STUDENT"
    }
  }
  ```

## 用户管理

### 获取用户列表

- **URL**: `/v1/admin/users`
- **方法**: GET
- **权限**: 系统管理员、学校管理员、企业管理员
- **参数**:
  - `page`: 页码，默认0
  - `size`: 每页大小，默认10
  - `keyword`: 搜索关键词
  - `status`: 用户状态筛选
  - `role`: 角色筛选
- **响应**:
  ```json
  {
    "code": 200,
    "message": "获取用户列表成功",
    "data": {
      "content": [
        {
          "id": 1,
          "account": "user123",
          "nickname": "用户昵称",
          "email": "user@example.com",
          "phone": "13800138000",
          "status": "active",
          "organizationId": 1,
          "role": "STUDENT",
          "createdAt": "2023-06-01T10:00:00"
        }
      ],
      "totalElements": 100,
      "totalPages": 10,
      "size": 10,
      "number": 0
    }
  }
  ```

### 更新用户状态

- **URL**: `/v1/admin/users/{id}/status`
- **方法**: PATCH
- **权限**: 系统管理员、学校管理员、企业管理员
- **请求体**:
  ```json
  {
    "status": "active"
  }
  ```
- **响应**:
  ```json
  {
    "code": 200,
    "message": "用户状态更新成功",
    "data": true
  }
  ```

## 组织管理

### 获取组织列表

- **URL**: `/v1/organizations`
- **方法**: GET
- **参数**:
  - `page`: 页码，默认0
  - `size`: 每页大小，默认10
  - `type`: 组织类型（School/Enterprise）
  - `keyword`: 搜索关键词
- **响应**:
  ```json
  {
    "code": 200,
    "message": "获取组织列表成功",
    "data": {
      "content": [
        {
          "id": 1,
          "organizationName": "示例大学",
          "type": "School",
          "description": "示例大学描述",
          "address": "示例地址",
          "website": "http://example.edu",
          "status": "approved"
        }
      ],
      "totalElements": 50,
      "totalPages": 5,
      "size": 10,
      "number": 0
    }
  }
  ```

### 获取学校列表（公开）

- **URL**: `/v1/organizations/schools`
- **方法**: GET
- **描述**: 获取所有已审核通过的学校列表，用于注册时选择
- **响应**:
  ```json
  {
    "code": 200,
    "message": "获取学校列表成功",
    "data": [
      {
        "id": 1,
        "organizationName": "示例大学"
      }
    ]
  }
  ```

## 岗位管理

### 获取岗位列表

- **URL**: `/v1/jobs`
- **方法**: GET
- **描述**: 获取所有状态为'open'的岗位列表，支持多条件筛选
- **参数**:
  - `page`: 页码，默认0
  - `size`: 每页大小，默认10
  - `keyword`: 搜索关键词
  - `location`: 城市筛选
  - `jobType`: 工作类型筛选
  - `educationRequirement`: 学历要求筛选
  - `salaryMin`: 最低薪资
  - `salaryMax`: 最高薪资
  - `sortBy`: 排序方式（latest/salary_asc/salary_desc）
  - `organizeId`: 组织ID筛选
- **响应**:
  ```json
  {
    "code": 200,
    "message": "获取岗位列表成功",
    "data": {
      "content": [
        {
          "id": 1,
          "title": "Java开发工程师",
          "description": "岗位描述...",
          "location": "北京",
          "organizationId": 1,
          "organizationName": "示例企业",
          "salaryMin": 15,
          "salaryMax": 25,
          "salaryUnit": "per_month",
          "jobType": "full_time",
          "educationRequirement": "bachelor",
          "experienceRequirement": "1_to_3_years",
          "createdAt": "2023-06-01T10:00:00"
        }
      ],
      "totalElements": 200,
      "totalPages": 20,
      "size": 10,
      "number": 0
    }
  }
  ```

### 创建岗位

- **URL**: `/v1/jobs`
- **方法**: POST
- **权限**: 企业管理员、企业导师
- **请求体**:
  ```json
  {
    "title": "Java开发工程师",
    "description": "岗位描述...",
    "location": "北京",
    "salaryMin": 15,
    "salaryMax": 25,
    "salaryUnit": "per_month",
    "jobType": "full_time",
    "headcount": 2,
    "educationRequirement": "bachelor",
    "experienceRequirement": "1_to_3_years",
    "categoryId": 101,
    "jobRequirements": "岗位具体要求...",
    "jobBenefits": "工作福利描述..."
  }
  ```
- **响应**:
  ```json
  {
    "code": 200,
    "message": "岗位创建成功",
    "data": 1
  }
  ```

### 获取岗位详情

- **URL**: `/v1/jobs/{id}`
- **方法**: GET
- **描述**: 获取指定ID的岗位详细信息
- **响应**:
  ```json
  {
    "code": 200,
    "message": "获取岗位详情成功",
    "data": {
      "id": 1,
      "title": "Java开发工程师",
      "description": "岗位描述...",
      "location": "北京",
      "organizationId": 1,
      "organizationName": "示例企业",
      "organization": {
        "id": 1,
        "organizationName": "示例企业",
        "type": "Enterprise",
        "description": "企业描述",
        "address": "企业地址",
        "website": "http://example.com"
      },
      "enterpriseDetail": {
        "organizationId": 1,
        "industry": "IT",
        "scale": "100-499",
        "foundingYear": 2010,
        "logo": "logo.jpg"
      },
      "postedByUserId": 1,
      "postedByUser": {
        "id": 1,
        "account": "hr123",
        "nickname": "HR专员",
        "email": "hr@example.com",
        "role": "EN_ADMIN"
      },
      "salaryMin": 15,
      "salaryMax": 25,
      "salaryUnit": "per_month",
      "jobType": "full_time",
      "headcount": 2,
      "educationRequirement": "bachelor",
      "experienceRequirement": "1_to_3_years",
      "category": {
        "id": 101,
        "name": "Java开发",
        "level": 3
      },
      "jobRequirements": "岗位具体要求...",
      "jobBenefits": "工作福利描述...",
      "status": "open",
      "createdAt": "2023-06-01T10:00:00"
    }
  }
  ```

## 岗位申请

### 申请岗位

- **URL**: `/v1/applications`
- **方法**: POST
- **权限**: 学生
- **请求体**:
  ```json
  {
    "jobId": 1,
    "resumeId": 1,
    "coverLetter": "申请说明..."
  }
  ```
- **响应**:
  ```json
  {
    "code": 200,
    "message": "申请提交成功",
    "data": 1
  }
  ```

### 获取我的申请列表

- **URL**: `/v1/applications/my`
- **方法**: GET
- **权限**: 学生
- **参数**:
  - `page`: 页码，默认0
  - `size`: 每页大小，默认10
  - `status`: 申请状态筛选
- **响应**:
  ```json
  {
    "code": 200,
    "message": "获取申请列表成功",
    "data": {
      "content": [
        {
          "id": 1,
          "jobId": 1,
          "jobTitle": "Java开发工程师",
          "organizationId": 1,
          "organizationName": "示例企业",
          "resumeId": 1,
          "status": "submitted",
          "appliedAt": "2023-06-01T10:00:00"
        }
      ],
      "totalElements": 5,
      "totalPages": 1,
      "size": 10,
      "number": 0
    }
  }
  ```

## 双师课堂

### 创建双师课堂

- **URL**: `/v1/dual-courses`
- **方法**: POST
- **权限**: 教师、学校管理员
- **请求体**:
  ```json
  {
    "title": "Java高级编程实战",
    "description": "课程描述...",
    "mentorId": 2,
    "scheduledTime": "2023-07-01T14:00:00",
    "maxStudents": 30,
    "location": "线上",
    "courseType": "online"
  }
  ```
- **响应**:
  ```json
  {
    "code": 200,
    "message": "课程创建成功",
    "data": {
      "id": 1,
      "title": "Java高级编程实战",
      "description": "课程描述...",
      "teacherId": 1,
      "teacherName": "张老师",
      "mentorId": 2,
      "mentorName": "李导师",
      "scheduledTime": "2023-07-01T14:00:00",
      "maxStudents": 30,
      "location": "线上",
      "courseType": "online",
      "status": "planning",
      "createdAt": "2023-06-01T10:00:00"
    }
  }
  ```

### 获取可报名课程列表

- **URL**: `/v1/dual-courses/enrollable`
- **方法**: GET
- **描述**: 获取当前可供学生报名的所有课程
- **参数**:
  - `page`: 页码，默认1
  - `size`: 每页大小，默认10
- **响应**:
  ```json
  {
    "code": 200,
    "message": "获取可报名课程列表成功",
    "data": {
      "content": [
        {
          "id": 1,
          "title": "Java高级编程实战",
          "description": "课程描述...",
          "teacherId": 1,
          "teacherName": "张老师",
          "mentorId": 2,
          "mentorName": "李导师",
          "scheduledTime": "2023-07-01T14:00:00",
          "maxStudents": 30,
          "enrolledCount": 15,
          "location": "线上",
          "courseType": "online",
          "status": "open",
          "createdAt": "2023-06-01T10:00:00"
        }
      ],
      "totalElements": 8,
      "totalPages": 1,
      "size": 10,
      "number": 1
    }
  }
  ```

### 报名课程

- **URL**: `/v1/dual-courses/enroll`
- **方法**: POST
- **权限**: 学生
- **请求体**:
  ```json
  {
    "courseId": 1
  }
  ```
- **响应**:
  ```json
  {
    "code": 200,
    "message": "课程报名成功",
    "data": null
  }
  ```

## 推荐系统

### 获取岗位推荐

- **URL**: `/v1/recommendations/jobs`
- **方法**: GET
- **权限**: 学生
- **参数**:
  - `page`: 页码，默认0
  - `size`: 每页大小，默认10
- **响应**:
  ```json
  {
    "code": 200,
    "message": "获取岗位推荐列表成功",
    "data": {
      "content": [
        {
          "id": 1,
          "jobId": 1,
          "jobTitle": "Java开发工程师",
          "organizationId": 1,
          "organizationName": "示例企业",
          "location": "北京",
          "salaryMin": 15,
          "salaryMax": 25,
          "salaryUnit": "per_month",
          "jobType": "full_time",
          "score": 0.85,
          "reason": "根据您的技能和经验推荐",
          "status": "new"
        }
      ],
      "totalElements": 20,
      "totalPages": 2,
      "size": 10,
      "number": 0
    }
  }
  ```

### 获取人才推荐

- **URL**: `/v1/recommendations/talents`
- **方法**: GET
- **权限**: 企业管理员、企业导师
- **参数**:
  - `page`: 页码，默认0
  - `size`: 每页大小，默认10
- **响应**:
  ```json
  {
    "code": 200,
    "message": "获取人才推荐列表成功",
    "data": {
      "content": [
        {
          "id": 1,
          "studentId": 1,
          "studentName": "张三",
          "avatar": "avatar.jpg",
          "major": "计算机科学",
          "educationLevel": "本科",
          "skills": ["Java", "Spring Boot", "MySQL"],
          "score": 0.78,
          "reason": "技能与您的岗位需求匹配",
          "status": "new"
        }
      ],
      "totalElements": 15,
      "totalPages": 2,
      "size": 10,
      "number": 0
    }
  }
  ```

## 社区功能

### 获取社区板块列表

- **URL**: `/v1/community/categories`
- **方法**: GET
- **描述**: 获取社区板块列表
- **响应**:
  ```json
  {
    "code": 200,
    "message": "获取板块列表成功",
    "data": [
      {
        "id": 1,
        "name": "校企合作",
        "description": "讨论校企合作相关话题",
        "icon": "cooperation.png",
        "permissionLevel": 0,
        "topicCount": 56,
        "childCategories": [
          {
            "id": 2,
            "name": "项目合作",
            "description": "项目合作相关讨论",
            "icon": "project.png",
            "permissionLevel": 0,
            "topicCount": 23
          }
        ]
      }
    ]
  }
  ```

### 获取话题列表

- **URL**: `/v1/community/topics`
- **方法**: GET
- **描述**: 获取话题列表
- **参数**:
  - `page`: 页码，默认1
  - `size`: 每页大小，默认10
  - `categoryId`: 板块ID筛选
  - `keyword`: 搜索关键词
  - `sort`: 排序方式（latest/hot/essence）
- **响应**:
  ```json
  {
    "code": 200,
    "message": "获取话题列表成功",
    "data": {
      "content": [
        {
          "id": 1,
          "title": "如何提高校企合作效率？",
          "content": "话题内容摘要...",
          "userId": 1,
          "userName": "张三",
          "userAvatar": "avatar.jpg",
          "categoryId": 1,
          "categoryName": "校企合作",
          "viewCount": 156,
          "commentCount": 23,
          "likeCount": 45,
          "isSticky": true,
          "isEssence": true,
          "createdAt": "2023-06-01T10:00:00"
        }
      ],
      "totalElements": 56,
      "totalPages": 6,
      "size": 10,
      "number": 1
    }
  }
  ```

## 错误码说明

| 错误码 | 描述 |
| --- | --- |
| 200 | 成功 |
| 400 | 请求参数错误 |
| 401 | 未授权（未登录或token无效） |
| 403 | 权限不足 |
| 404 | 资源不存在 |
| 409 | 资源冲突（如用户名已存在） |
| 500 | 服务器内部错误 | 