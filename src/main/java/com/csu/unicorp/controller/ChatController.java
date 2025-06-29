package com.csu.unicorp.controller;

import java.util.List;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.csu.unicorp.config.security.CustomUserDetails;
import com.csu.unicorp.dto.ChatMessageDTO;
import com.csu.unicorp.service.ChatService;
import com.csu.unicorp.vo.ChatMessageVO;
import com.csu.unicorp.vo.ChatSessionVO;
import com.csu.unicorp.vo.ResultVO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 聊天控制器
 */
@RestController
@RequestMapping("/v1/chat")
@RequiredArgsConstructor
@Tag(name = "聊天API", description = "聊天相关的API，包含REST接口和WebSocket消息")
@Slf4j
public class ChatController {
    
    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;
    
    /**
     * 获取用户所有聊天会话
     * @param userDetails 当前登录用户
     * @return 聊天会话列表
     */
    @GetMapping("/sessions")
    @Operation(summary = "获取用户所有聊天会话")
    public ResultVO<List<ChatSessionVO>> getSessions(@AuthenticationPrincipal CustomUserDetails userDetails) {
        List<ChatSessionVO> sessions = chatService.getUserSessions(Long.valueOf(userDetails.getUser().getId()));
        return ResultVO.success("获取聊天会话成功", sessions);
    }
    
    /**
     * 获取会话详情
     * @param sessionId 会话ID
     * @param userDetails 当前登录用户
     * @return 会话详情
     */
    // @GetMapping("/sessions/{sessionId}")
    // @Operation(summary = "获取会话详情")
    // public ResultVO<ChatSessionVO> getSessionDetail(
    //         @PathVariable @Parameter(description = "会话ID") Long sessionId,
    //         @AuthenticationPrincipal CustomUserDetails userDetails) {
        
    //     ChatSessionVO session = chatService.getSessionDetail(sessionId, Long.valueOf(userDetails.getUser().getId()));
    //     return ResultVO.success("获取会话详情成功", session);
    // }
    
    /**
     * 获取会话消息历史
     * @param sessionId 会话ID
     * @param page 页码
     * @param size 每页大小
     * @return 消息列表
     */
    @GetMapping("/sessions/{sessionId}/messages")
    @Operation(summary = "获取会话消息历史")
    public ResultVO<List<ChatMessageVO>> getSessionMessages(
            @PathVariable @Parameter(description = "会话ID") Long sessionId,
            @RequestParam(required = false, defaultValue = "1") @Parameter(description = "页码") Integer page,
            @RequestParam(required = false, defaultValue = "20") @Parameter(description = "每页大小") Integer size) {
        
        List<ChatMessageVO> messages = chatService.getSessionMessages(sessionId, page, size);
        return ResultVO.success("获取消息历史成功", messages);
    }
    
    /**
     * 标记消息为已读
     * @param sessionId 会话ID
     * @param userDetails 当前登录用户
     * @return 操作结果
     */
    @PostMapping("/sessions/{sessionId}/read")
    @Operation(summary = "标记会话中的消息为已读")
    public ResultVO<String> markMessagesAsRead(
            @PathVariable @Parameter(description = "会话ID") Long sessionId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        chatService.markMessagesAsRead(sessionId, Long.valueOf(userDetails.getUser().getId()));
        return ResultVO.success("消息已标记为已读");
    }
    
    /**
     * 创建会话
     * @param userId 目标用户ID
     * @param userDetails 当前登录用户
     * @return 会话详情
     */
    @PostMapping("/sessions")
    @Operation(summary = "创建聊天会话")
    public ResultVO<ChatSessionVO> createSession(
            @RequestParam @Parameter(description = "对话用户ID") Long userId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        
        // 获取当前用户ID
        Long currentUserId = Long.valueOf(userDetails.getUser().getId());
        
        // 创建会话
        chatService.getOrCreateSession(currentUserId, userId);
        
        // 获取会话详情
        ChatSessionVO session = chatService.getSessionDetail(
                chatService.getOrCreateSession(currentUserId, userId).getId(), 
                currentUserId);
        
        return ResultVO.success("创建会话成功", session);
    }
    
    /**
     * 发送消息接口 - 统一入口
     * 通过HTTP接收请求，内部使用WebSocket发送消息
     * @param messageDTO 消息内容
     * @param userDetails 当前用户
     * @return 消息发送结果
     */
    @PostMapping("/messages")
    @Operation(
        summary = "发送消息", 
        description = "发送消息给指定用户。通过HTTP接收请求，内部使用WebSocket推送到接收方。\n\n" +
                "## WebSocket集成\n" +
                "1. 接收方必须已连接WebSocket并订阅了`/user/queue/messages`\n" + 
                "2. 系统会自动将消息路由到接收方的订阅\n" +
                "3. Principal标识使用用户ID，确保接收方用户ID正确\n" +
                "4. 消息格式与返回的ChatMessageVO相同"
    )
    public ResultVO<ChatMessageVO> sendMessage(
            @RequestBody ChatMessageDTO messageDTO,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        
        // 设置发送者ID
        messageDTO.setSenderId(Long.valueOf(userDetails.getUser().getId()));
        log.info("发送消息: {}", messageDTO);
        try {
            // 保存消息到数据库
            ChatMessageVO messageVO = chatService.sendMessage(messageDTO);
            
            // 获取接收者ID作为Principal name
            String receiverUserId = String.valueOf(messageDTO.getReceiverId());
            
            // 使用WebSocket发送消息到接收者的私人队列
            log.info("发送WebSocket消息到用户ID {}: {}", receiverUserId, messageVO);
            messagingTemplate.convertAndSendToUser(
                    receiverUserId, // 直接使用用户ID字符串作为目标用户标识
                    "/queue/messages",
                    messageVO);
            
            log.info("WebSocket消息发送完成，目标:/user/{}/queue/messages", receiverUserId);
            
            return ResultVO.success("发送消息成功", messageVO);
        } catch (Exception e) {
            log.error("发送消息失败", e);
            return ResultVO.error("发送消息失败: " + e.getMessage());
        }
    }
}