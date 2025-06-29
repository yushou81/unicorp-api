package com.csu.unicorp.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 此控制器不包含实际API，仅用于在Swagger UI中记录WebSocket API的使用方法
 * 实际WebSocket处理在WebSocketConfig和ChatController中
 */
@RestController
@RequestMapping("/v1/docs/websocket")
@Tag(name = "WebSocket API", description = "WebSocket连接和使用说明")
public class WebSocketDocController {

    /**
     * 此方法仅用于文档目的，不会被实际调用
     */
    @Operation(
        summary = "WebSocket连接端点",
        description = "WebSocket连接端点为: /api/ws\n\n" +
                "## 认证方式\n" + 
                "连接时需要在请求头中添加`Authorization: Bearer {your_jwt_token}`\n\n" +
                "## 主要步骤\n" +
                "1. 使用SockJS创建连接: `new SockJS('/api/ws')`\n" +
                "2. 使用STOMP协议: `Stomp.over(socket)`\n" +
                "3. 连接并认证: `stompClient.connect({Authorization: 'Bearer token'}, successCallback)`\n" +
                "4. 订阅个人消息: `stompClient.subscribe('/user/queue/messages', messageHandler)`\n\n" + 
                "## 重要提示\n" +
                "- Principal标识使用用户ID，确保接收消息时使用正确的用户ID\n" +
                "- 消息发送通过REST API: `POST /api/v1/chat/messages`\n" +
                "- 消息接收通过WebSocket订阅\n\n" +
                "更多详细信息可参考测试页面: `/api/test.html`"
    )
    public void webSocketEndpoint() {
        // 此方法仅用于文档，不会被调用
    }

    /**
     * 此方法仅用于文档目的，不会被实际调用
     */
    @Operation(
        summary = "可用的STOMP消息目标",
        description = "## 消息代理前缀\n" +
                "- `/topic`: 公开频道\n" +
                "- `/queue`: 私人队列\n\n" +
                "## 应用前缀\n" +
                "- `/app`: 发送到应用的消息\n\n" +
                "## 用户前缀\n" +
                "- `/user`: 发送到特定用户的消息\n\n" +
                "## 订阅端点\n" +
                "- 个人消息: `/user/queue/messages`\n\n" +
                "## 注意事项\n" +
                "实际订阅时不需要添加用户前缀，STOMP会自动处理，例如订阅`/user/queue/messages`，" +
                "而不是`/user/{userId}/queue/messages`"
    )
    public void stompDestinations() {
        // 此方法仅用于文档，不会被调用
    }
}
