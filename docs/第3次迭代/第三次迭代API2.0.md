# OpenAPI 规范版本
openapi: 3.0.3

# API 基本信息
info:
  title: "校企联盟平台 - 第三次迭代API"
  description: "本次迭代的核心目标是实现“校企双方发布、管理、浏览合作项目”以及“学生申请并参与项目”的完整功能闭环。"
  version: "1.1.0-sprint3"

# 服务器配置
servers:
  - url: "http://localhost:8080/api/v1"
    description: "本地开发服务器"

# 标签定义，用于对API进行分组
tags:
  - name: "Projects"
    description: "合作项目管理"
  - name: "Project Applications"
    description: "项目申请与成员管理"
  - name: "Me"
    description: "与当前登录用户相关的功能"

# API 路径定义
paths:
  # ===============================================================
  # Projects (合作项目管理)
  # ===============================================================
  /projects:
    get:
      tags:
        - "Projects"
      summary: "获取合作项目列表 (公开)"
      description: "获取所有状态为 'recruiting' 的项目列表，支持分页和搜索。"
      parameters:
        - name: page
          in: query
          schema:
            type: integer
            default: 0
        - name: size
          in: query
          schema:
            type: integer
            default: 10
        - name: keyword
          in: query
          schema:
            type: string
      responses:
        '200':
          description: "成功获取项目列表"
          content:
            application/json:
              schema:
                type: array
                items:
                  $ref: '#/components/schemas/project'
    post:
      tags:
        - "Projects"
      summary: "[校/企] 创建新项目"
      description: "由教师或企业用户调用，用于发布一个新的合作项目。"
      security:
        - BearerAuth: []
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/project_creation'
      responses:
        '201':
          description: "项目创建成功"
        '403':
          description: "权限不足"

  /projects/{id}:
    get:
      tags:
        - "Projects"
      summary: "获取特定项目详情"
      parameters:
        - name: id
          in: path
          required: true
          schema:
            type: integer
      responses:
        '200':
          description: "成功获取项目详情"
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/project'
        '404':
          description: "项目未找到"
    put:
      tags:
        - "Projects"
      summary: "[所有者] 更新项目信息"
      security:
        - BearerAuth: []
      parameters:
        - name: id
          in: path
          required: true
          schema:
            type: integer
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/project_creation'
      responses:
        '200':
          description: "项目更新成功"
        '403':
          description: "权限不足"
        '404':
          description: "项目未找到"
    delete:
      tags:
        - "Projects"
      summary: "[所有者] 删除项目"
      description: "逻辑删除一个项目。"
      security:
        - BearerAuth: []
      parameters:
        - name: id
          in: path
          required: true
          schema:
            type: integer
      responses:
        '204':
          description: "删除成功"
        '403':
          description: "权限不足"

  # ===============================================================
  # Project Applications (项目申请与成员管理)
  # ===============================================================
  /projects/{id}/apply:
    post:
      tags:
        - "Project Applications"
      summary: "[学生] 申请加入项目"
      description: "学生用户提交加入某个项目的申请。"
      security:
        - BearerAuth: []
      parameters:
        - name: id
          in: path
          required: true
          schema:
            type: integer
      requestBody:
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/project_application_creation'
      responses:
        '201':
          description: "申请成功"
        '400':
          description: "已申请过该项目"
        '403':
          description: "权限不足 (非学生用户)"

  /projects/{id}/applications:
    get:
      tags:
        - "Project Applications"
      summary: "[所有者] 查看项目申请列表"
      description: "项目所有者查看指定项目的所有申请人列表。"
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
          description: "成功获取申请列表"
          content:
            application/json:
              schema:
                type: array
                items:
                  $ref: '#/components/schemas/project_application_detail'
        '403':
          description: "权限不足"

  /project-applications/{id}:
    patch:
      tags:
        - "Project Applications"
      summary: "[所有者] 更新项目申请状态"
      description: "由项目所有者调用，用于更新某个项目申请的状态（如：批准、拒绝）。"
      security:
        - BearerAuth: []
      parameters:
        - name: id
          in: path
          required: true
          schema:
            type: integer
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/project_application_status_update'
      responses:
        '200':
          description: "状态更新成功"
        '400':
          description: "无效的状态值"
        '403':
          description: "权限不足"
        '404':
          description: "申请未找到"

  # ===============================================================
  # Me (当前用户相关功能)
  # ===============================================================
  /me/project-applications:
    get:
      tags:
        - "Me"
      summary: "[学生] 查看我的项目申请"
      description: "获取当前登录学生的所有项目申请记录及其最新状态。"
      security:
        - BearerAuth: []
      parameters:
        - name: page
          in: query
          schema:
            type: integer
            default: 0
        - name: size
          in: query
          schema:
            type: integer
            default: 10
      responses:
        '200':
          description: "成功获取申请列表"
          content:
            application/json:
              schema:
                type: array
                items:
                  $ref: '#/components/schemas/my_project_application_detail'
        '403':
          description: "权限不足 (非学生用户)"

# 可重用组件定义
components:
  securitySchemes:
    BearerAuth:
      type: http
      scheme: bearer
      bearerFormat: JWT

  schemas:
    project:
      type: object
      properties:
        id:
          type: integer
        organization_id:
          type: integer
        organization_name:
          type: string
        title:
          type: string
        description:
          type: string
        status:
          type: string
        created_at:
          type: string

    project_creation:
      type: object
      required:
        - title
        - description
      properties:
        title:
          type: string
        description:
          type: string
        status:
          type: string
          enum: [recruiting, in_progress, completed]
          default: "recruiting"

    project_application_creation:
      type: object
      properties:
        application_statement:
          type: string
          description: "申请陈述或备注"

    project_application_detail:
      type: object
      properties:
        id:
          type: integer
        project_id:
          type: integer
        user_id:
          type: integer
        status:
          type: string
        application_statement:
          type: string
        created_at:
          type: string
        applicant_profile:
          type: object
          properties:
            nickname:
              type: string
            real_name:
              type: string
            major:
              type: string

    project_application_status_update:
      type: object
      required:
        - status
      properties:
        status:
          type: string
          enum: [viewed, approved, rejected]
          description: "项目申请的新状态"

    my_project_application_detail:
      type: object
      properties:
        application_id:
          type: integer
        status:
          type: string
        applied_at:
          type: string
        project_info:
          type: object
          properties:
            project_id:
              type: integer
            project_title:
              type: string
            organization_name:
              type: string
