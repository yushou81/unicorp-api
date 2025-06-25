package com.csu.linkneiapi.config;

import com.csu.linkneiapi.common.utils.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT认证过滤器
 * 用于验证请求中的JWT令牌
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        // 从请求头中获取"Authorization"
        final String authorizationHeader = request.getHeader("Authorization");
        System.out.println("用户访问API："+ request.getRequestURI());
        String username = null;
        String jwtToken = null;
        
        // 检查Authorization头是否存在且以"Bearer "开头
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            // 提取JWT令牌
            jwtToken = authorizationHeader.substring(7);
            try {
                // 从令牌中获取用户名
                username = jwtUtils.getUsernameFromToken(jwtToken);
            } catch (Exception e) {
                logger.warn("JWT令牌无效: " + e.getMessage());
            }
        } else {
            logger.debug("JWT令牌不存在或格式不正确");
        }
        
        // 如果成功获取用户名且SecurityContext中尚未设置认证信息
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // 加载用户详细信息
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
            
            // 验证令牌有效性
            if (jwtUtils.validateToken(jwtToken, userDetails)) {
                // 构建认证信息
                UsernamePasswordAuthenticationToken authenticationToken = 
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                
                // 设置认证信息到SecurityContext
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                
                logger.debug("用户已认证: " + username);
            }
        }
        
        // 继续处理过滤器链
        filterChain.doFilter(request, response);
    }
} 