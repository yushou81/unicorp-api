package com.csu.unicorp.config.security;

import com.csu.unicorp.common.constants.RoleConstants;
import com.csu.unicorp.common.utils.AccountGenerator;
import com.csu.unicorp.common.utils.JwtUtil;
import com.csu.unicorp.entity.User;
import com.csu.unicorp.service.FileService;
import com.csu.unicorp.service.RoleService;
import com.csu.unicorp.service.UserService;
import com.csu.unicorp.vo.TokenVO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * OAuth2登录成功处理器
 * 处理GitHub登录成功后的逻辑，包括用户注册或登录
 */
@Slf4j
@Component
public class OAuth2LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;
    private final ApplicationContext applicationContext;
    private final AccountGenerator accountGenerator;
    
    // 使用懒加载方式获取服务，避免循环依赖
    private UserService userService;
    private RoleService roleService;
    private FileService fileService;

    @Autowired
    public OAuth2LoginSuccessHandler(JwtUtil jwtUtil, ApplicationContext applicationContext, AccountGenerator accountGenerator) {
        this.jwtUtil = jwtUtil;
        this.applicationContext = applicationContext;
        this.accountGenerator = accountGenerator;
    }
    
    // 懒加载获取UserService
    private UserService getUserService() {
        if (userService == null) {
            userService = applicationContext.getBean(UserService.class);
        }
        return userService;
    }
    
    // 懒加载获取RoleService
    private RoleService getRoleService() {
        if (roleService == null) {
            roleService = applicationContext.getBean(RoleService.class);
        }
        return roleService;
    }
    
    // 懒加载获取FileService
    private FileService getFileService() {
        if (fileService == null) {
            fileService = applicationContext.getBean(FileService.class);
        }
        return fileService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        log.info("=== OAuth2 登录成功处理开始 ===");
        log.info("请求URI: {}", request.getRequestURI());
        log.info("请求URL: {}", request.getRequestURL());
        log.info("查询参数: {}", request.getQueryString());
        log.info("认证类型: {}", authentication.getClass().getName());
        
        if (authentication instanceof OAuth2AuthenticationToken) {
            OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
            OAuth2User oAuth2User = oauthToken.getPrincipal();
            String registrationId = oauthToken.getAuthorizedClientRegistrationId(); // 获取OAuth2提供商ID (github)
            
            log.info("OAuth2 login success, provider: {}", registrationId);
            log.info("OAuth2 user name: {}", oAuth2User.getName());
            log.info("OAuth2 user attributes: {}", oAuth2User.getAttributes());
            
            if ("github".equals(registrationId)) {
                try {
                    handleGitHubLogin(oAuth2User, response);
                } catch (Exception e) {
                    log.error("处理GitHub登录时发生错误: {}", e.getMessage(), e);
                    response.sendRedirect("http://192.168.58.96:8087/login-error?reason=" + URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8));
                }
                return;
            }
        } else {
            log.warn("非OAuth2认证类型: {}", authentication.getClass().getName());
        }
        
        // 如果不是OAuth2登录或不是GitHub，使用默认处理
        log.info("使用默认认证成功处理器");
        super.onAuthenticationSuccess(request, response, authentication);
    }
    
    /**
     * 处理GitHub登录
     * 
     * @param oAuth2User GitHub用户信息
     * @param response HTTP响应对象
     * @throws IOException IO异常
     */
    private void handleGitHubLogin(OAuth2User oAuth2User, HttpServletResponse response) throws IOException {
        log.info("=== 处理GitHub登录开始 ===");
        
        // 从GitHub获取用户信息
        Map<String, Object> attributes = oAuth2User.getAttributes();
        log.info("GitHub用户属性: {}", attributes);
        
        String githubId = attributes.get("id").toString();
        String login = (String) attributes.get("login");
        String email = (String) attributes.get("email");
        String name = (String) attributes.get("name");
        String avatarUrl = (String) attributes.get("avatar_url");
        
        log.info("GitHub ID: {}", githubId);
        log.info("GitHub 登录名: {}", login);
        log.info("GitHub 邮箱: {}", email);
        log.info("GitHub 姓名: {}", name);
        
        // 检查用户是否存在（通过GitHub ID）
        User user = getUserService().getByGithubId(githubId);
        
        if (user == null) {
            log.info("用户不存在，创建新用户");
            // 用户不存在，创建新用户
            user = createGitHubUser(githubId, login, email, name, avatarUrl);
            log.info("创建的新用户ID: {}, 账号: {}", user.getId(), user.getAccount());
        } else {
            log.info("找到已存在用户: ID={}, 账号={}", user.getId(), user.getAccount());
        }
        
        // 生成JWT令牌
        UserDetails userDetails = new CustomUserDetails(user, getUserService().getUserRole(user.getId()));
        String token = jwtUtil.generateToken(userDetails);
        String refreshToken = jwtUtil.generateRefreshToken(userDetails);
        
        log.info("已生成JWT令牌");
        
        // 构建TokenVO
        TokenVO tokenVO = TokenVO.builder()
                .token(token)
                .refreshToken(refreshToken)
                .nickname(user.getNickname())
                .role(getUserService().getUserRole(user.getId()))
                .build();
                
        // 设置头像URL
        if (user.getAvatar() != null && !user.getAvatar().isEmpty()) {
            String avatarFullUrl = getFileService().getFullFileUrl(user.getAvatar());
            tokenVO.setAvatar(avatarFullUrl);
            log.info("用户头像URL: {}", avatarFullUrl);
        }
        
        // 重定向到前端，带上token参数
        String redirectUrl = "http://localhost:8087/login-success" +
                "?token=" + URLEncoder.encode(token, StandardCharsets.UTF_8) +
                "&refreshToken=" + URLEncoder.encode(refreshToken, StandardCharsets.UTF_8) +
                "&nickname=" + URLEncoder.encode(user.getNickname(), StandardCharsets.UTF_8) +
                "&role=" + URLEncoder.encode(getUserService().getUserRole(user.getId()), StandardCharsets.UTF_8);
        
        if (user.getAvatar() != null && !user.getAvatar().isEmpty()) {
            redirectUrl += "&avatar=" + URLEncoder.encode(getFileService().getFullFileUrl(user.getAvatar()), StandardCharsets.UTF_8);
        }
        
        log.info("重定向URL: {}", redirectUrl);
        log.info("=== 处理GitHub登录结束，准备重定向 ===");
        
        response.sendRedirect(redirectUrl);
    }
    
    /**
     * 创建GitHub用户
     * 
     * @param githubId GitHub用户ID
     * @param login GitHub登录名
     * @param email 邮箱
     * @param name 姓名
     * @param avatarUrl 头像URL
     * @return 创建的用户
     */
    private User createGitHubUser(String githubId, String login, String email, String name, String avatarUrl) {
        log.info("=== 创建GitHub用户开始 ===");
        User user = new User();
        
        // 使用AccountGenerator生成账号
        String generatedAccount = accountGenerator.generateStudentAccount(null);
        log.info("为GitHub用户生成账号: {}", generatedAccount);
        user.setAccount(generatedAccount);
        
        String nickname = name != null && !name.isEmpty() ? name : login;
        log.info("设置用户昵称: {}", nickname);
        user.setNickname(nickname);

        // 处理邮箱可能为null的情况
        if (email != null && !email.isEmpty()) {
            log.info("设置用户邮箱: {}", email);
            user.setEmail(email);
        } else {
            log.info("GitHub未提供邮箱，设置为null");
            user.setEmail(null);
        }
        
        // GitHub OAuth2登录的用户不设置密码，因为我们已经在数据库中将password字段设为可为NULL
        log.info("GitHub OAuth2登录用户，不设置密码");
        user.setPassword(null);
        
        user.setStatus("active");
        user.setCreatedAt(LocalDateTime.now());
        
        // 设置GitHub用户ID
        log.info("关联GitHub ID: {}", githubId);
        user.setGithubId(githubId);
        
        // 使用GitHub头像或默认头像
        String avatarPath;
        if (avatarUrl != null && !avatarUrl.isEmpty()) {
            // 这里可以选择下载GitHub头像到本地，或者直接使用GitHub的URL
            // 为简单起见，我们先使用默认头像
            log.info("GitHub头像URL: {}, 但使用默认头像", avatarUrl);
            avatarPath = getUserService().assignDefaultAvatar();
        } else {
            log.info("GitHub没有提供头像，使用默认头像");
            avatarPath = getUserService().assignDefaultAvatar();
        }
        log.info("设置头像路径: {}", avatarPath);
        user.setAvatar(avatarPath);
        
        // 保存用户
        log.info("保存用户到数据库");
        getUserService().saveUser(user);
        log.info("用户已保存，ID: {}", user.getId());
        
        // 分配学生角色（或者其他默认角色）
        log.info("为用户分配角色: {}", RoleConstants.DB_ROLE_STUDENT);
        getRoleService().assignRoleToUser(user.getId(), RoleConstants.DB_ROLE_STUDENT);
        
        log.info("=== 创建GitHub用户完成 ===");
        return user;
    }
} 