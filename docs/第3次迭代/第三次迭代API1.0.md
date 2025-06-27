# OpenAPI 规范版本
openapi: 3.0.3

# API 基本信息
info:
  title: "校企联盟平台 - 第三次迭代API"
  description: "本次迭代的核心目标是实现“校企双方发布、管理、浏览合作项目”的功能闭环。"
  version: "1.0.0-sprint3"

# 服务器配置
servers:
  - url: "http://localhost:8080/api/v1"
    description: "本地开发服务器"

# 标签定义，用于对API进行分组
tags:
  - name: "Authentication"
    description: "（继承自第一次迭代）"
  - name: "Organizations"
    description: "（继承自第一次迭代）"
  - name: "Admin"
    description: "（继承自第一次迭代）"
  - name: "Jobs"
    description: "（继承自第二次迭代）"
  - name: "Applications"
    description: "（继承自第二次迭代）"
  - name: "Projects"
    description: "合作项目管理"

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
