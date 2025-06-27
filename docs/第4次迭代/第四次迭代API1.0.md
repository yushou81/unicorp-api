# OpenAPI 规范版本
openapi: 3.0.3

# API 基本信息
info:
  title: "校企联盟平台 - 第四次迭代API"
  description: "本次迭代的核心目标是实现“资源共享中心”的基础功能，包括资源的上传、浏览、管理和下载。"
  version: "1.0.0-sprint4"

# 服务器配置
servers:
  - url: "http://localhost:8080/api/v1"
    description: "本地开发服务器"

# 标签定义，用于对API进行分组
tags:
  - name: "Resources"
    description: "资源共享管理"
  - name: "File Upload"
    description: "文件上传服务"

# API 路径定义
paths:
  # ===============================================================
  # File Upload (文件上传服务)
  # ===============================================================
  /files/upload:
    post:
      tags:
        - "File Upload"
      summary: "通用文件上传接口"
      description: "用于上传文件（如资源文件、营业执照等），成功后返回文件的访问URL。"
      security:
        - BearerAuth: []
      requestBody:
        required: true
        content:
          multipart/form-data:
            schema:
              type: object
              properties:
                file:
                  type: string
                  format: binary
      responses:
        '200':
          description: "文件上传成功"
          content:
            application/json:
              schema:
                type: object
                properties:
                  file_url:
                    type: string
                    format: uri
                    description: "文件的可访问URL"

  # ===============================================================
  # Resources (资源共享管理)
  # ===============================================================
  /resources:
    get:
      tags:
        - "Resources"
      summary: "获取资源列表 (公开)"
      description: "获取所有已发布的资源列表，支持分页和搜索。"
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
          description: "成功获取资源列表"
          content:
            application/json:
              schema:
                type: array
                items:
                  $ref: '#/components/schemas/resource'
    post:
      tags:
        - "Resources"
      summary: "[教师/导师] 上传新资源"
      description: "由教师或企业导师调用，用于发布一个新的共享资源。"
      security:
        - BearerAuth: []
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/resource_creation'
      responses:
        '201':
          description: "资源创建成功"
        '403':
          description: "权限不足"

  /resources/{id}:
    get:
      tags:
        - "Resources"
      summary: "获取特定资源详情"
      parameters:
        - name: id
          in: path
          required: true
          schema:
            type: integer
      responses:
        '200':
          description: "成功获取资源详情"
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/resource'
        '404':
          description: "资源未找到"
    put:
      tags:
        - "Resources"
      summary: "[所有者] 更新资源信息"
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
              $ref: '#/components/schemas/resource_creation'
      responses:
        '200':
          description: "资源更新成功"
        '403':
          description: "权限不足"
        '404':
          description: "资源未找到"
    delete:
      tags:
        - "Resources"
      summary: "[所有者] 删除资源"
      description: "逻辑删除一个资源。"
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
    resource:
      type: object
      properties:
        id:
          type: integer
        title:
          type: string
        description:
          type: string
        resource_type:
          type: string
        file_url:
          type: string
          format: uri
        created_at:
          type: string
        uploader:
          type: object
          properties:
            nickname:
              type: string
            organization_name:
              type: string

    resource_creation:
      type: object
      required:
        - title
        - file_url
      properties:
        title:
          type: string
        description:
          type: string
        resource_type:
          type: string
          description: "e.g., 技术文档, 教学课件, 案例分析"
        file_url:
          type: string
          format: uri
          description: "文件上传后获取到的URL"
