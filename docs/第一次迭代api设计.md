openapi: 3.0.3
info:
  title: "邻客内江 API"
  description: "邻客内江本地生活服务平台的后端API接口文档。这是第一次迭代的API，主要包含商户信息的读取功能。"
  version: "1.0.0"
servers:
  - url: http://localhost:8081/api
    description: 本地开发服务器

tags:
  - name: Merchant
    description: 商户相关操作

paths:
  /merchants/list:
    get:
      tags:
        - Merchant
      summary: 获取商户列表 (分页)
      description: 用于在首页或商户列表页，分页获取平台上的商户简要信息。
      parameters:
        - name: page
          in: query
          description: 请求的页码
          required: false
          schema:
            type: integer
            default: 1
        - name: pageSize
          in: query
          description: 每页的数据条数
          required: false
          schema:
            type: integer
            default: 10
      responses:
        '200':
          description: 成功获取商户列表
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
                    type: object
                    properties:
                      total:
                        type: integer
                        description: "总记录数"
                        example: 100
                      pages:
                        type: integer
                        description: "总页数"
                        example: 10
                      list:
                        type: array
                        items:
                          $ref: '#/components/schemas/MerchantSummaryVO'
        '500':
          description: 服务器内部错误
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/ErrorResponse'

  /merchants/{id}:
    get:
      tags:
        - Merchant
      summary: 根据ID获取商户详情
      description: 用于在用户点击某个商户后，获取该商户的完整详细信息，包括其发布的产品列表。
      parameters:
        - name: id
          in: path
          description: 商户的唯一ID
          required: true
          schema:
            type: integer
            format: int64
      responses:
        '200':
          description: 成功获取商户详情
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
                    $ref: '#/components/schemas/MerchantDetailVO'
        '404':
          description: 商户未找到
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/ErrorResponse'
        '500':
          description: 服务器内部错误
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/ErrorResponse'

components:
  schemas:
    # 用于商户列表的视图对象
    MerchantSummaryVO:
      type: object
      properties:
        id:
          type: integer
          format: int64
          example: 1
        name:
          type: string
          description: "商户名称"
          example: "张三烧烤"
        address:
          type: string
          description: "商户地址"
          example: "内江市东兴区XX路XX号"
        logoUrl:
          type: string
          description: "商户Logo图片地址"
          example: "https://example.com/logo.jpg"
        averageRating:
          type: number
          format: float
          description: "平均评分"
          example: 4.5

    # 用于商户详情的视图对象
    MerchantDetailVO:
      type: object
      properties:
        id:
          type: integer
          format: int64
          example: 1
        name:
          type: string
          example: "张三烧烤"
        address:
          type: string
          example: "内江市东兴区XX路XX号"
        phone:
          type: string
          example: "13888888888"
        description:
          type: string
          example: "本店炭火烧烤，秘制酱料，欢迎品尝！"
        products:
          type: array
          description: "该商户的产品列表"
          items:
            $ref: '#/components/schemas/ProductSummaryVO'
    
    # 用于商户详情中内嵌的产品简要信息
    ProductSummaryVO:
      type: object
      properties:
        id:
          type: integer
          format: int64
          example: 101
        name:
          type: string
          example: "招牌烤五花肉"
        price:
          type: number
          format: double
          example: 2.5
        imageUrl:
          type: string
          example: "https://example.com/wuhuarou.jpg"

    # 通用错误响应
    ErrorResponse:
      type: object
      properties:
        code:
          type: integer
          example: 500
        message:
          type: string
          example: "服务器内部错误"
