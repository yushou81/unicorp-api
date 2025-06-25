openapi: 3.0.3
info:
  title: "邻客内江 API - V1.2"
  description: "邻客内江本地生活服务平台的后端API接口文档。本次更新增加了个人资料管理相关接口。"
  version: "1.2.0"
servers:
  - url: http://localhost:8081/api
    description: 本地开发服务器

tags:
  - name: User Profile
    description: 当前登录用户的个人资料管理

# ---------------------------------------------------
#  安全方案定义: 我们使用JWT Bearer Token进行认证
# ---------------------------------------------------
components:
  securitySchemes:
    BearerAuth:
      type: http
      scheme: bearer
      bearerFormat: JWT
  schemas:
    # 用于展示个人资料的视图对象
    UserProfileVO:
      type: object
      properties:
        username:
          type: string
          description: "用户名 (不可修改)"
          example: "zhangsan"
        nickname:
          type: string
          description: "用户昵称"
          example: "爱吃烧烤的张三"
        avatarUrl:
          type: string
          description: "用户头像URL"
          example: "https://example.com/avatar.jpg"
        phone:
          type: string
          description: "手机号码"
          example: "138****8888" # 注意脱敏
        role:
          type: string
          description: "用户角色"
          example: "USER"

    # 用于更新个人资料的数据传输对象
    ProfileUpdateDTO:
      type: object
      properties:
        nickname:
          type: string
          description: "新的用户昵称"
          example: "爱吃火锅的张三"
        avatarUrl:
          type: string
          description: "新的用户头像URL"
          example: "https://example.com/new_avatar.jpg"
        phone:
          type: string
          description: "新的手机号码"
          example: "13999999999"

    # 通用错误响应
    ErrorResponse:
      type: object
      properties:
        code:
          type: integer
          example: 401
        message:
          type: string
          example: "未授权访问"

# ---------------------------------------------------
#  API路径定义
# ---------------------------------------------------
paths:
  /user/profile:
    get:
      tags:
        - User Profile
      summary: 获取当前登录用户的个人资料
      description: 根据请求头中携带的JWT Token，获取并返回当前用户的详细个人信息。
      security:
        - BearerAuth: [] # 声明此接口需要JWT认证
      responses:
        '200':
          description: 成功获取个人资料
          content:
            application/json:
              schema:
                type: object
                properties:
                  code:
                    type: integer
                    example: 200
                  message:
                    type: string
                    example: "操作成功"
                  data:
                    $ref: '#/components/schemas/UserProfileVO'
        '401':
          description: 未授权
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/ErrorResponse'

    put:
      tags:
        - User Profile
      summary: 更新当前登录用户的个人资料
      description: 根据请求头中携带的JWT Token，更新当前用户的个人信息（如昵称、头像等）。
      security:
        - BearerAuth: [] # 声明此接口需要JWT认证
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/ProfileUpdateDTO'
      responses:
        '200':
          description: 成功更新个人资料
          content:
            application/json:
              schema:
                type: object
                properties:
                  code:
                    type: integer
                    example: 200
                  message:
                    type: string
                    example: "更新成功！"
        '400':
          description: 请求参数错误 (例如手机号格式不正确)
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/ErrorResponse'
        '401':
          description: 未授权
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/ErrorResponse'
```