# OpenAPI 规范版本
openapi: 3.0.3

# API 基本信息
info:
  title: "校企联盟平台 - 第一次迭代API"
  description: "本次迭代的核心目标是实现“管理员录入学校 -> 学生根据学校列表成功注册并登录”以及“企业注册与审核”的完整功能闭环。"
  version: "1.7.0-sprint1"

# 服务器配置
servers:
  - url: "http://localhost:8080/api/v1"
    description: "本地开发服务器"

# 标签定义，用于对API进行分组
tags:
  - name: "Authentication"
    description: "登录与注册"
  - name: "Organizations"
    description: "组织管理 (本次迭代仅含获取学校列表)"
  - name: "Admin"
    description: "系统管理员后台接口"

# API 路径定义
paths:
  # ===============================================================
  # Authentication (登录与注册)
  # ===============================================================
  /auth/login:
    post:
      tags:
        - "Authentication"
      summary: "通用登录接口 (前端指定凭证类型)"
      description: "用户使用账号、邮箱或手机号以及密码进行登录。前端需明确指定登录类型。成功后返回JWT。"
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/login_credentials'
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
          description: "认证失败"

  /auth/register/student:
    post:
      tags:
        - "Authentication"
      summary: "学生注册接口"
      description: "学生选择已存在的学校进行注册，并提供实名信息。后端将为其生成唯一的账号。注册后状态直接为'active'。"
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/student_registration'
      responses:
        '201':
          description: "学生注册成功"
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/user'
        '400':
          description: "无效的输入，或邮箱/手机号已存在"

  /auth/register/enterprise:
    post:
      tags:
        - "Authentication"
      summary: "企业注册接口 (需审核)"
      description: "企业代表进行公开注册。注册后，企业和其管理员账号状态均为'pending'，需要系统管理员审核。"
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/enterprise_registration'
      responses:
        '202':
          description: "注册申请已提交，等待审核"
        '400':
          description: "无效的输入，或邮箱/手机号/企业名称已存在"

  /auth/me:
    get:
      tags:
        - "Authentication"
      summary: "获取当前登录用户信息"
      description: "根据提供的JWT获取当前登录用户的详细信息。"
      security:
        - BearerAuth: []
      responses:
        '200':
          description: "成功获取用户信息"
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/user'
        '401':
          description: "未授权"

  # ===============================================================
  # Organizations (组织管理)
  # ===============================================================
  /organizations/schools:
    get:
      tags:
        - "Organizations"
      summary: "获取学校列表 (公开)"
      description: "这是一个公开接口，用于获取所有已批准的学校列表，供学生注册时选择。"
      responses:
        '200':
          description: "成功获取学校列表"
          content:
            application/json:
              schema:
                type: array
                items:
                  $ref: '#/components/schemas/organization_simple'

  # ===============================================================
  # Admin (管理员后台)
  # ===============================================================
  /admin/organizations/schools:
    post:
      tags:
        - "Admin"
      summary: "[Admin] 手动创建学校信息"
      description: "由系统管理员调用，用于录入一个已合作的学校。后端将自动为该校生成管理员账号。"
      security:
        - BearerAuth: []
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/school_creation'
      responses:
        '201':
          description: "学校及管理员账号创建成功"
        '403':
          description: "权限不足"

  /admin/approvals/organizations:
    get:
      tags:
        - "Admin"
      summary: "[Admin] 获取待审核的组织列表"
      description: "获取所有状态为 'pending' 的组织列表，供管理员审核。"
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
                  $ref: '#/components/schemas/organization'
        '403':
          description: "权限不足"

  /admin/approvals/organizations/{id}/approve:
    patch:
      tags:
        - "Admin"
      summary: "[Admin] 批准组织注册"
      description: "批准一个待审核的组织。后端需要将该组织及其关联的初始管理员账号的状态都更新为 'approved'/'active'。"
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
          description: "组织已批准"
        '404':
          description: "组织未找到"
        '403':
          description: "权限不足"

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
    user:
      type: object
      properties:
        id:
          type: integer
        account:
          type: string
        nickname:
          type: string
        email:
          type: string
        phone:
          type: string
        status:
          type: string
        organization_id:
          type: integer
        roles:
          type: array
          items:
            type: string
        created_at:
          type: string

    organization:
      type: object
      properties:
        id:
          type: integer
        organization_name:
          type: string
        type:
          type: string
        description:
          type: string
        website:
          type: string
        address:
          type: string

    organization_simple:
      type: object
      properties:
        id:
          type: integer
        organization_name:
          type: string

    # 请求体模型
    login_credentials:
      type: object
      required:
        - principal
        - password
        - login_type
      properties:
        login_type:
          type: string
          enum: [account, email, phone]
        principal:
          type: string
        password:
          type: string

    student_registration:
      type: object
      required:
        - password
        - email
        - organization_id
        - real_name
        - id_card
      properties:
        nickname:
          type: string
        password:
          type: string
        email:
          type: string
        phone:
          type: string
        organization_id:
          type: integer
        real_name:
          type: string
        id_card:
          type: string

    school_creation:
      type: object
      required:
        - organization_name
        - admin_email
        - admin_password
      properties:
        organization_name:
          type: string
        description:
          type: string
        address:
          type: string
        website:
          type: string
        admin_email:
          type: string
        admin_nickname:
          type: string
        admin_password:
          type: string

    enterprise_registration:
      type: object
      description: "企业注册时所需的数据"
      required:
        - organization_name
        - admin_email
        - admin_password
        - business_license_url
      properties:
        # 企业信息
        organization_name:
          type: string
        description:
          type: string
        address:
          type: string
        website:
          type: string
        industry:
          type: string
        company_size:
          type: string
        business_license_url:
          type: string
          format: uri
          description: "营业执照图片的URL (前端需先上传图片获取URL)"
        # 初始企业管理员信息
        admin_email:
          type: string
        admin_nickname:
          type: string
        admin_password:
          type: string
        admin_phone:
          type: string
