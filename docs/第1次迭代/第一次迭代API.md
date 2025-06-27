# OpenAPI 规范版本
openapi: 3.0.3

# API 基本信息
info:
  title: "校企联盟平台 - 第一次迭代API"
  description: "本次迭代的核心目标是实现“管理员录入学校 -> 学生根据学校列表成功注册并登录”的完整功能闭环。"
  version: "1.1.0-sprint1"

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
    description: "系统管理员后台接口 (本次迭代仅含创建学校)"

# API 路径定义
paths:
  # ===============================================================
  # Authentication (登录与注册)
  # ===============================================================
  /auth/login:
    post:
      tags:
        - "Authentication"
      summary: "通用登录接口"
      description: "用户使用账号和密码登录，成功后返回JWT。后端需校验用户status为'active'。"
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
      description: "学生选择已存在的学校进行注册，并提供实名信息。注册后状态直接为'active'。"
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
          description: "无效的输入，或账号/邮箱已存在"

  /auth/me:
    get:
      tags:
        - "Authentication"
      summary: "获取当前登录用户信息"
      description: "根据提供的JWT获取当前登录用户的详细信息，用于验证token和获取用户信息。"
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
      description: "由系统管理员调用，用于录入一个已合作的学校。创建后，学校和默认的学校管理员账号状态均为'approved'/'active'。"
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
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/organization'
        '403':
          description: "权限不足 (非系统管理员)"

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
          example: "zhangsan2025"
        nickname:
          type: string
          example: "三哥"
        email:
          type: string
          format: email
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
          format: date-time

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
          format: uri
        address:
          type: string

    organization_simple:
      type: object
      properties:
        id:
          type: integer
          example: 101
        organization_name:
          type: string
          example: "中南大学"

    # 请求体模型
    login_credentials:
      type: object
      required:
        - account
        - password
      properties:
        account:
          type: string
        password:
          type: string
          format: password

    student_registration:
      type: object
      required:
        - account
        - password
        - email
        - organization_id
        - real_name
        - id_card
      properties:
        account:
          type: string
        nickname:
          type: string
        password:
          type: string
          format: password
        email:
          type: string
          format: email
        organization_id:
          type: integer
          description: "学生从下拉列表中选择的学校ID"
        real_name:
          type: string
          description: "用于实名认证的真实姓名"
        id_card:
          type: string
          description: "用于实名认证的身份证号"

    school_creation:
      type: object
      description: "管理员创建学校时所需的数据"
      required:
        - organization_name
        - admin_account
        - admin_password
        - admin_email
      properties:
        # 学校信息
        organization_name:
          type: string
        description:
          type: string
        address:
          type: string
        website:
          type: string
        # 初始学校管理员信息
        admin_account:
          type: string
        admin_nickname:
          type: string
        admin_password:
          type: string
        admin_email:
          type: string
          format: email
