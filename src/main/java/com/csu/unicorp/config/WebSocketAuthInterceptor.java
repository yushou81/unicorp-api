package com.csu.unicorp.config;

import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import com.csu.unicorp.common.utils.JwtUtil;
import com.csu.unicorp.config.security.CustomUserDetails;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * WebSocket认证拦截器
 */
@Configuration(proxyBeanMethods=false)
@RequiredArgsConstructor
@Slf4j
public class WebSocketAuthInterceptor implements ChannelInterceptor {
    
    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;
    
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        
        if (accessor != null) {
            log.debug("处理WebSocket消息: {} - 会话ID: {}", accessor.getCommand(), accessor.getSessionId());
            
            if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                log.debug("处理CONNECT命令 - 开始认证");
                
                // 从请求头中获取token
                List<String> authorization = accessor.getNativeHeader("Authorization");
                if (authorization != null && !authorization.isEmpty()) {
                    String token = authorization.get(0);
                    log.debug("找到认证头: {}", token.substring(0, Math.min(20, token.length())) + "...");
                    
                    if (token != null && token.startsWith("Bearer ")) {
                        token = token.substring(7);
                        
                        try {
                            // 验证token
                            String username = jwtUtil.extractUsername(token);
                            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                                log.debug("从token中提取的用户名: {}", username);
                                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                                
                                if (jwtUtil.validateToken(token, userDetails)) {
                                    log.debug("JWT令牌有效，设置认证信息");
                                    
                                    // 获取用户ID作为Principal name
                                    String userId = null;
                                    if (userDetails instanceof CustomUserDetails) {
                                        userId = String.valueOf(((CustomUserDetails) userDetails).getUser().getId());
                                        log.debug("使用用户ID作为Principal name: {}", userId);
                                    }
                                    
                                    // 创建自定义认证令牌，使用用户ID作为Principal name
                                    UsernamePasswordAuthenticationToken authToken = 
                                            new UsernamePasswordAuthenticationToken(
                                                    userId != null ? userId : username, // 使用用户ID作为Principal name
                                                    null, 
                                                    userDetails.getAuthorities());
                                    
                                    accessor.setUser(authToken);
                                    log.debug("认证成功，Principal name: {}", authToken.getName());
                                } else {
                                    log.warn("JWT令牌无效");
                                }
                            }
                        } catch (Exception e) {
                            // Token验证失败
                            log.error("认证失败: {}", e.getMessage());
                            return null;
                        }
                    } else {
                        log.warn("认证头格式不正确，应该是Bearer token");
                    }
                } else {
                    log.warn("没有找到认证头");
                }
            } else if (accessor.getUser() != null) {
                log.debug("用户已认证: {}", accessor.getUser().getName());
            }
        }
        
        return message;
    }
} 