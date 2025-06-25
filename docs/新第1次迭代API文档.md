openapi: 3.0.3
info:
  title: "校企联盟平台 API - V2.0"
  description: "基于V2.0数据库模型的全新API。本次为第一次迭代，主要包含用户认证功能。"
  version: "2.0.0"
servers:
  - url: http://localhost:8081/api
    description: 本地开发服务器

tags:
  - name: Authentication
    description: 用户认证相关操作 (注册与登录)

# ---------------------------------------------------
#  安全方案定义 (保持不变)
# ---------------------------------------------------
components:
  securitySchemes:
    BearerAuth:
      type: http
      scheme: bearer
      bearerFormat: JWT
  schemas:
    # 用于注册的数据传输对象
    RegisterDTO:
      type: object
      required:
        - username
        - password
      properties:
        username:
          type: string
          description: "登录用户名"
          example: "chenqigang"
        password:
          type: string
          description: "登录密码"
          example: "password123"
        nickname:
          type: string
          description: "初始昵称 (可选)"
          example: "起刚"

    # 用于登录的数据传输对象
    LoginDTO:
      type: object
      required:
        - username
        - password
      properties:
        username:
          type: string
          example: "chenqigang"
        password:
          type: string
          example: "password123"

    # 登录成功后返回的视图对象
    JwtResponseVO:
      type: object
      properties:
        token:
          type: string
          description: "JWT令牌"
          example: "eyJhbGciOiJIUzI1NiJ9..."
        username:
          type: string
          description: "用户名"
          example: "chenqigang"

    # 通用错误响应
    ErrorResponse:
      type: object
      properties:
        code:
          type: integer
          example: 400
        message:
          type: string
          example: "用户名已存在"

# ---------------------------------------------------
#  API路径定义
# ---------------------------------------------------
paths:
  # 将认证相关接口统一归类到 /auth 路径下
  /auth/register:
    post:
      tags:
        - Authentication
      summary: 新用户注册
      description: 创建一个新用户，并同时为其生成一个空的个人档案。
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/RegisterDTO'
      responses:
        '201':
          description: 注册成功
          content:
            application/json:
              schema:
                type: object
                properties:
                  code:
                    type: integer
                    example: 201
                  message:
                    type: string
                    example: "注册成功！"
        '400':
          description: 注册失败 (例如用户名已存在)
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/ErrorResponse'

  /auth/login:
    post:
      tags:
        - Authentication
      summary: 用户登录
      description: 使用用户名和密码进行认证，成功后返回JWT令牌。
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/LoginDTO'
      responses:
        '200':
          description: 登录成功
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
                    example: "登录成功"
                  data:
                    $ref: '#/components/schemas/JwtResponseVO'
        '401':
          description: 认证失败 (用户名或密码错误)
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/ErrorResponse'
