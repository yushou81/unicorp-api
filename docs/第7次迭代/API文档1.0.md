# OpenAPI 规范版本
openapi: 3.0.3

# API 基本信息
info:
  title: "校企联盟平台 - 岗位收藏功能API"
  description: "为人才招聘模块增加岗位收藏功能，允许学生收藏、取消收藏和查看已收藏的岗位。"
  version: "1.0.0"

# 服务器配置
servers:
  - url: "http://localhost:8080/api/v1"
    description: "本地开发服务器"

# 标签定义，用于对API进行分组
tags:
  - name: "Favorites"
    description: "岗位收藏管理"
  - name: "Me"
    description: "与当前登录用户相关的功能"

# API 路径定义
paths:
  # ===============================================================
  # Me (当前用户相关功能)
  # ===============================================================
  /me/favorites/jobs:
    get:
      tags:
        - "Me"
      summary: "[学生] 获取我收藏的岗位列表"
      description: "获取当前登录学生收藏的所有招聘岗位列表，支持分页。"
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
          description: "成功获取收藏列表"
          content:
            application/json:
              schema:
                type: array
                items:
                  $ref: '#/components/schemas/job' # 复用已有的job模型
        '403':
          description: "权限不足 (非学生用户)"

  # ===============================================================
  # Favorites (岗位收藏管理)
  # ===============================================================
  /jobs/{id}/favorite:
    post:
      tags:
        - "Favorites"
      summary: "[学生] 收藏一个岗位"
      description: "将指定的岗位添加至当前登录学生的收藏列表。"
      security:
        - BearerAuth: []
      parameters:
        - name: id
          in: path
          required: true
          schema:
            type: integer
            description: "要收藏的岗位ID"
      responses:
        '201':
          description: "收藏成功"
        '400':
          description: "已收藏过该岗位"
        '403':
          description: "权限不足 (非学生用户)"
        '404':
          description: "岗位未找到"
    delete:
      tags:
        - "Favorites"
      summary: "[学生] 取消收藏一个岗位"
      description: "将指定的岗位从当前登录学生的收藏列表中移除。"
      security:
        - BearerAuth: []
      parameters:
        - name: id
          in: path
          required: true
          schema:
            type: integer
            description: "要取消收藏的岗位ID"
      responses:
        '204':
          description: "取消收藏成功"
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
