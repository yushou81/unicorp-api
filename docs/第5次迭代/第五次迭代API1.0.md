# OpenAPI 规范版本
openapi: 3.0.3

# API 基本信息
info:
  title: "校企联盟平台 - 第五次迭代API"
  description: "本次迭代的核心目标是实现用户个人主页的展示与编辑，并为学生提供作品集管理功能。"
  version: "1.0.0-sprint5"

# 服务器配置
servers:
  - url: "http://localhost:8080/api/v1"
    description: "本地开发服务器"

# 标签定义，用于对API进行分组
tags:
  - name: "Profiles"
    description: "用户个人主页与档案管理"
  - name: "Portfolio"
    description: "学生作品集管理"
  - name: "Me"
    description: "与当前登录用户相关的功能"

# API 路径定义
paths:
  # ===============================================================
  # Profiles (个人主页)
  # ===============================================================
  /profiles/{user_id}:
    get:
      tags:
        - "Profiles"
      summary: "获取指定用户的公开主页信息"
      description: "获取一个用户的公开展示信息，返回的数据结构会根据用户角色有所不同。"
      parameters:
        - name: user_id
          in: path
          required: true
          schema:
            type: integer
      responses:
        '200':
          description: "成功获取用户主页信息"
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/user_profile_view'
        '404':
          description: "用户未找到"

  # ===============================================================
  # Me (当前用户相关功能)
  # ===============================================================
  /me/profile:
    get:
      tags:
        - "Me"
      summary: "获取我自己的详细档案信息"
      description: "获取当前登录用户的完整档案信息，用于“编辑我的主页”页面。"
      security:
        - BearerAuth: []
      responses:
        '200':
          description: "成功获取档案信息"
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/user_profile_view'
    put:
      tags:
        - "Me"
      summary: "更新我自己的基本档案"
      description: "更新当前登录用户的基本信息，如昵称、简介、头像等。"
      security:
        - BearerAuth: []
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/profile_update'
      responses:
        '200':
          description: "档案更新成功"

  # ===============================================================
  # Portfolio (学生作品集管理)
  # ===============================================================
  /me/portfolio:
    get:
      tags:
        - "Portfolio"
      summary: "[学生] 获取我的作品集列表"
      description: "获取当前登录学生的所有作品集项目。"
      security:
        - BearerAuth: []
      responses:
        '200':
          description: "成功获取作品集列表"
          content:
            application/json:
              schema:
                type: array
                items:
                  $ref: '#/components/schemas/portfolio_item'
        '403':
          description: "权限不足 (非学生用户)"
    post:
      tags:
        - "Portfolio"
      summary: "[学生] 添加新的作品集项目"
      security:
        - BearerAuth: []
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/portfolio_item_creation'
      responses:
        '201':
          description: "作品集项目创建成功"
        '403':
          description: "权限不足"

  /me/portfolio/{item_id}:
    put:
      tags:
        - "Portfolio"
      summary: "[学生] 更新一个作品集项目"
      security:
        - BearerAuth: []
      parameters:
        - name: item_id
          in: path
          required: true
          schema:
            type: integer
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/portfolio_item_creation'
      responses:
        '200':
          description: "更新成功"
        '403':
          description: "权限不足"
        '404':
          description: "项目未找到"
    delete:
      tags:
        - "Portfolio"
      summary: "[学生] 删除一个作品集项目"
      security:
        - BearerAuth: []
      parameters:
        - name: item_id
          in: path
          required: true
          schema:
            type: integer
      responses:
        '204':
          description: "删除成功"
        '403':
          description: "权限不足"

# 可重用组件定义
components:
  securitySchemes:
    BearerAuth:
      type: http
      scheme: bearer
      bearerFormat: JWT

  schemas:
    user_profile_view:
      type: object
      properties:
        id:
          type: integer
        account:
          type: string
        nickname:
          type: string
        avatar_url:
          type: string
          format: uri
        bio:
          type: string
        organization_name:
          type: string
        roles:
          type: array
          items:
            type: string
        # 学生专属信息
        student_profile:
          type: object
          properties:
            major:
              type: string
            education_level:
              type: string
        # 学生作品集
        portfolio:
          type: array
          items:
            $ref: '#/components/schemas/portfolio_item'

    profile_update:
      type: object
      properties:
        nickname:
          type: string
        bio:
          type: string
        avatar_url:
          type: string
          format: uri
          description: "头像文件上传后获取的URL"

    portfolio_item:
      type: object
      properties:
        id:
          type: integer
        title:
          type: string
        description:
          type: string
        project_url:
          type: string
          format: uri
        cover_image_url:
          type: string
          format: uri
        created_at:
          type: string
          format: date-time

    portfolio_item_creation:
      type: object
      required:
        - title
      properties:
        title:
          type: string
        description:
          type: string
        project_url:
          type: string
          format: uri
        cover_image_url:
          type: string
          format: uri
          description: "封面图上传后获取的URL"
