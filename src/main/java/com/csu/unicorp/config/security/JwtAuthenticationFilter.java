package com.csu.unicorp.config.security;

import com.csu.unicorp.common.utils.JwtUtil;
import com.csu.unicorp.service.TokenBlacklistService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.stream.Collectors;

/**
 * JWT认证过滤器，用于从请求中提取JWT并验证用户
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;
    private final TokenBlacklistService tokenBlacklistService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        
        // 如果不是Bearer token或为空，则检查格式
        if (authHeader == null) {
            filterChain.doFilter(request, response);
            return;
        }
        
        // 检查令牌格式
        if (!authHeader.startsWith("Bearer ")) {
            log.warn("令牌格式错误: {}", authHeader);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":401,\"message\":\"令牌格式错误，请使用 'Bearer token' 格式\",\"data\":null}");
            return;
        }

        // 提取JWT令牌（去除"Bearer "前缀）
        final String jwt = authHeader.substring(7);
        
        try {
            // 检查令牌是否在黑名单中
            if (tokenBlacklistService.isBlacklisted(jwt)) {
                log.warn("令牌已被撤销: {}", jwt);
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"code\":401,\"message\":\"令牌已被撤销，请重新登录\",\"data\":null}");
                return;
            }
            
            // 从JWT中提取账号
            final String userAccount = jwtUtil.extractUsername(jwt);
            
            log.info("userToken: {}", jwt);
            log.info("userAccount: {}", userAccount);

            // 如果用户名不为空，且当前SecurityContext中没有认证信息
            if (userAccount != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userAccount);
                
                // 验证JWT是否有效
                if (jwtUtil.validateToken(jwt, userDetails)) {
                    // 创建认证令牌并设置到SecurityContext中
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    
                    // 输出用户角色信息，用于调试
                    String roles = userDetails.getAuthorities().stream()
                            .map(auth -> auth.getAuthority())
                            .collect(Collectors.joining(", "));
                    System.out.println("用户 " + userAccount + " 认证成功，角色: " + roles + "，访问路径: " + request.getRequestURI());
                    log.info("用户 {} 认证成功，角色: {}, 访问路径: {}", userAccount, roles, request.getRequestURI());
                }
            }
        } catch (Exception e) {
            log.error("JWT认证失败: {}", e.getMessage());
        }

        filterChain.doFilter(request, response);
    }
} 