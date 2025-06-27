# OpenAPI 规范版本
openapi: 3.0.3

# API 基本信息
info:
  title: "校企联盟平台 - 第二次迭代API"
  description: "本次迭代的核心目标是实现“企业发布岗位 -> 学生浏览并申请岗位 -> 企业查看并审核申请 -> 学生查看申请状态”的完整招聘流程闭环。"
  version: "1.2.0-sprint2"

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
    description: "招聘岗位管理"
  - name: "Applications"
    description: "岗位申请管理"
  - name: "Me"
    description: "与当前登录用户相关的功能"


# API 路径定义
paths:
  # ===============================================================
  # Jobs (招聘岗位管理)
  # ===============================================================
  /jobs:
    get:
      tags:
        - "Jobs"
      summary: "获取岗位列表 (公开)"
      description: "获取所有状态为 'open' 的岗位列表，支持分页和搜索。"
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
          description: "成功获取岗位列表"
          content:
            application/json:
              schema:
                type: array
                items:
                  $ref: '#/components/schemas/job'
    post:
      tags:
        - "Jobs"
      summary: "[企业] 创建新岗位"
      description: "由企业管理员或企业导师调用，用于发布一个新的招聘岗位。"
      security:
        - BearerAuth: []
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/job_creation'
      responses:
        '201':
          description: "岗位创建成功"
        '403':
          description: "权限不足"

  /jobs/{id}:
    get:
      tags:
        - "Jobs"
      summary: "获取特定岗位详情"
      parameters:
        - name: id
          in: path
          required: true
          schema:
            type: integer
      responses:
        '200':
          description: "成功获取岗位详情"
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/job'
        '404':
          description: "岗位未找到"
    put:
      tags:
        - "Jobs"
      summary: "[企业] 更新岗位信息"
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
              $ref: '#/components/schemas/job_creation'
      responses:
        '200':
          description: "岗位更新成功"
        '403':
          description: "权限不足"
        '404':
          description: "岗位未找到"
    delete:
      tags:
        - "Jobs"
      summary: "[企业] 删除岗位"
      description: "逻辑删除一个岗位。"
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
  # Applications (岗位申请管理)
  # ===============================================================
  /jobs/{id}/apply:
    post:
      tags:
        - "Applications"
      summary: "[学生] 申请岗位"
      description: "学生用户申请一个岗位。"
      security:
        - BearerAuth: []
      parameters:
        - name: id
          in: path
          required: true
          schema:
            type: integer
      responses:
        '201':
          description: "申请成功"
        '400':
          description: "已申请过该岗位"
        '403':
          description: "权限不足 (非学生用户)"

  /jobs/{id}/applications:
    get:
      tags:
        - "Applications"
      summary: "[企业] 查看岗位申请列表"
      description: "企业用户查看指定岗位的申请人列表。"
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
                  $ref: '#/components/schemas/application_detail'
        '403':
          description: "权限不足"

  /applications/{id}:
    patch:
      tags:
        - "Applications"
      summary: "[企业] 更新申请状态"
      description: "由企业用户调用，用于更新某个岗位申请的状态（如：标记为面试中、已录用、已拒绝等）。"
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
              $ref: '#/components/schemas/application_status_update'
      responses:
        '200':
          description: "状态更新成功"
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/application_detail'
        '400':
          description: "无效的状态值"
        '403':
          description: "权限不足"
        '404':
          description: "申请未找到"
          
  # ===============================================================
  # Me (当前用户相关功能)
  # ===============================================================
  /me/applications:
    get:
      tags:
        - "Me"
      summary: "[学生] 查看我的申请"
      description: "获取当前登录学生的所有岗位申请记录及其最新状态。"
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
                  $ref: '#/components/schemas/my_application_detail'
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
    job:
      type: object
      properties:
        id:
          type: integer
        organization_id:
          type: integer
        organization_name:
          type: string
        posted_by_user_id:
          type: integer
        title:
          type: string
        description:
          type: string
        location:
          type: string
        status:
          type: string
        created_at:
          type: string

    job_creation:
      type: object
      required:
        - title
        - description
      properties:
        title:
          type: string
        description:
          type: string
        location:
          type: string

    application_detail:
      type: object
      properties:
        id:
          type: integer
        job_id:
          type: integer
        student_id:
          type: integer
        status:
          type: string
        applied_at:
          type: string
        student_profile:
          type: object
          properties:
            nickname:
              type: string
            real_name:
              type: string
            major:
              type: string
            education_level:
              type: string
            resume_url:
              type: string

    application_status_update:
      type: object
      required:
        - status
      properties:
        status:
          type: string
          enum: [viewed, interviewing, offered, rejected]
          description: "岗位申请的新状态"
          
    my_application_detail:
      type: object
      properties:
        application_id:
          type: integer
        status:
          type: string
        applied_at:
          type: string
        job_info:
          type: object
          properties:
            job_id:
              type: integer
            job_title:
              type: string
            organization_name:
              type: string
