# OpenAPI 规范版本
openapi: 3.0.3

# API 基本信息
info:
  title: "校企联盟平台 (Unicorp) API"
  description: "为校企联盟平台提供后端服务的RESTful API。包含了用户认证、组织管理、岗位招聘、项目合作等核心功能。"
  version: "1.0.0"

# 服务器配置
servers:
  - url: "http://localhost:8080/api/v1"
    description: "本地开发服务器"

# 标签定义，用于对API进行分组
tags:
  - name: "Authentication"
    description: "用户认证、注册与会话管理"
  - name: "Organizations"
    description: "组织信息管理 (学校与企业)"
  - name: "Users"
    description: "用户个人信息管理"
  - name: "Jobs"
    description: "招聘岗位管理"
  - name: "Admin"
    description: "系统管理员专属接口"

# API 路径定义
paths:
  # ===============================================================
  # Authentication 标签下的路径
  # ===============================================================
  /auth/register:
    post:
      tags:
        - "Authentication"
      summary: "用户公开注册"
      description: "供学生或企业代表进行公开注册，注册后状态为'pending_approval'，需要管理员审核。"
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/UserRegistration'
      responses:
        '201':
          description: "注册成功，等待审核"
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/User'
        '400':
          description: "无效的输入，或用户名/邮箱已存在"

  /auth/login:
    post:
      tags:
        - "Authentication"
      summary: "用户登录"
      description: "用户使用用户名和密码登录，成功后返回JWT。"
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/LoginCredentials'
      responses:
        '200':
          description: "登录成功"
          content:
            application/json:
              schema:
                type: object
                properties:
                  token:
                    type: string
                    description: "JWT认证令牌"
        '401':
          description: "认证失败 (用户名或密码错误 / 账户未激活或待审核)"

  /auth/me:
    get:
      tags:
        - "Authentication"
      summary: "获取当前用户信息"
      description: "根据提供的JWT获取当前登录用户的详细信息。"
      security:
        - BearerAuth: []
      responses:
        '200':
          description: "成功获取用户信息"
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/User'
        '401':
          description: "未授权"

  # ===============================================================
  # Organizations 标签下的路径
  # ===============================================================
  /organizations:
    get:
      tags:
        - "Organizations"
      summary: "获取组织列表"
      description: "获取所有状态为 'approved' 的组织列表，支持按类型筛选。"
      parameters:
        - name: type
          in: query
          schema:
            type: string
            enum: [School, Enterprise]
          description: "按组织类型筛选"
      responses:
        '200':
          description: "成功获取组织列表"
          content:
            application/json:
              schema:
                type: array
                items:
                  $ref: '#/components/schemas/Organization'

  /organizations/{id}:
    get:
      tags:
        - "Organizations"
      summary: "获取特定组织详情"
      parameters:
        - name: id
          in: path
          required: true
          schema:
            type: integer
      responses:
        '200':
          description: "成功获取组织详情"
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/OrganizationDetail'
        '404':
          description: "组织未找到"

  # ===============================================================
  # Admin 标签下的路径
  # ===============================================================
  /admin/users:
    post:
      tags:
        - "Admin"
      summary: "[Admin] 创建用户"
      description: "由管理员直接创建用户（如教师、学校管理员），创建后用户状态直接为'active'。"
      security:
        - BearerAuth: []
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/AdminCreateUser'
      responses:
        '201':
          description: "用户创建成功"
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/User'

  /admin/approvals/users:
    get:
      tags:
        - "Admin"
      summary: "[Admin] 获取待审核用户列表"
      security:
        - BearerAuth: []
      responses:
        '200':
          description: "成功获取列表"
          content:
            application/json:
              schema:
                type: array
                items:
                  $ref: '#/components/schemas/User'

  /admin/approvals/users/{id}/approve:
    patch:
      tags:
        - "Admin"
      summary: "[Admin] 批准用户注册"
      security:
        - BearerAuth: []
      parameters:
        - name: id
          in: path
          required: true
          schema:
            type: integer
      responses:
        '200':
          description: "用户已批准"
        '404':
          description: "用户未找到"


# 可重用组件定义
components:
  # 安全方案定义
  securitySchemes:
    BearerAuth:
      type: http
      scheme: bearer
      bearerFormat: JWT

  # 数据模型 (Schemas) 定义
  schemas:
    # 基础模型
    User:
      type: object
      properties:
        UserID:
          type: integer
          example: 1
        Username:
          type: string
          example: "zhangsan"
        Email:
          type: string
          format: email
          example: "zhangsan@example.com"
        PhoneNumber:
          type: string
          example: "13800138000"
        Status:
          type: string
          enum: [active, inactive, pending_approval]
        OrganizationID:
          type: integer
        Roles:
          type: array
          items:
            type: string
            example: "学生"
        CreatedAt:
          type: string
          format: date-time

    Organization:
      type: object
      properties:
        OrganizationID:
          type: integer
          example: 101
        OrganizationName:
          type: string
          example: "中南大学"
        Type:
          type: string
          enum: [School, Enterprise]
        Description:
          type: string
        Website:
          type: string
          format: uri

    OrganizationDetail:
      allOf:
        - $ref: '#/components/schemas/Organization'
        - type: object
          properties:
            Address:
              type: string
            SchoolDetails:
              $ref: '#/components/schemas/SchoolDetails'
            EnterpriseDetails:
              $ref: '#/components/schemas/EnterpriseDetails'

    SchoolDetails:
      type: object
      properties:
        SchoolType:
          type: string
        EducationLevels:
          type: string

    EnterpriseDetails:
      type: object
      properties:
        Industry:
          type: string
        CompanySize:
          type: string

    # 请求体模型
    UserRegistration:
      type: object
      required:
        - Username
        - Password
        - Email
        - OrganizationName
        - Type
      properties:
        Username:
          type: string
        Password:
          type: string
          format: password
        Email:
          type: string
          format: email
        OrganizationName:
          type: string
          description: "用户所属组织名称，如果是新组织，系统将创建。"
        Type:
          type: string
          enum: [School, Enterprise]
          description: "组织的类型"

    LoginCredentials:
      type: object
      required:
        - Username
        - Password
      properties:
        Username:
          type: string
        Password:
          type: string
          format: password

    AdminCreateUser:
      type: object
      required:
        - Username
        - Password
        - Email
        - OrganizationID
        - RoleIDs
      properties:
        Username:
          type: string
        Password:
          type: string
          format: password
        Email:
          type: string
          format: email
        OrganizationID:
          type: integer
        RoleIDs:
          type: array
          items:
            type: integer
