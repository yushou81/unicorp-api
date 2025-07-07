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
        if (authentication instanceof OAuth2AuthenticationToken) {
            OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
            OAuth2User oAuth2User = oauthToken.getPrincipal();
            String registrationId = oauthToken.getAuthorizedClientRegistrationId(); // 获取OAuth2提供商ID (github)
            
            log.info("OAuth2 login success, provider: {}", registrationId);
            
            if ("github".equals(registrationId)) {
                handleGitHubLogin(oAuth2User, response);
                return;
            }
        }
        
        // 如果不是OAuth2登录或不是GitHub，使用默认处理
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
        // 从GitHub获取用户信息
        Map<String, Object> attributes = oAuth2User.getAttributes();
        String githubId = attributes.get("id").toString();
        String login = (String) attributes.get("login");
        String email = (String) attributes.get("email");
        String name = (String) attributes.get("name");
        String avatarUrl = (String) attributes.get("avatar_url");
        
        // 检查用户是否存在（通过GitHub ID）
        User user = getUserService().getByGithubId(githubId);
        
        if (user == null) {
            // 用户不存在，创建新用户
            user = createGitHubUser(githubId, login, email, name, avatarUrl);
        }
        
        // 生成JWT令牌
        UserDetails userDetails = new CustomUserDetails(user, getUserService().getUserRole(user.getId()));
        String token = jwtUtil.generateToken(userDetails);
        String refreshToken = jwtUtil.generateRefreshToken(userDetails);
        
        // 构建TokenVO
        TokenVO tokenVO = TokenVO.builder()
                .token(token)
                .refreshToken(refreshToken)
                .nickname(user.getNickname())
                .role(getUserService().getUserRole(user.getId()))
                .build();
                
        // 设置头像URL
        if (user.getAvatar() != null && !user.getAvatar().isEmpty()) {
            tokenVO.setAvatar(getFileService().getFullFileUrl(user.getAvatar()));
        }
        
        // 重定向到前端，带上token参数
        String redirectUrl = "http://localhost:8082/login-success" +
                "?token=" + token +
                "&refreshToken=" + refreshToken +
                "&nickname=" + user.getNickname() +
                "&role=" + getUserService().getUserRole(user.getId());
        
        if (user.getAvatar() != null && !user.getAvatar().isEmpty()) {
            redirectUrl += "&avatar=" + getFileService().getFullFileUrl(user.getAvatar());
        }
        
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
        User user = new User();
        
        // 使用AccountGenerator生成账号
        String generatedAccount = accountGenerator.generateStudentAccount(null);
        user.setAccount(generatedAccount);
        
        user.setNickname(name != null && !name.isEmpty() ? name : login);
        user.setEmail(email);
        user.setStatus("active");
        user.setCreatedAt(LocalDateTime.now());
        
        // 设置GitHub用户ID
        user.setGithubId(githubId);
        
        // 使用GitHub头像或默认头像
        if (avatarUrl != null && !avatarUrl.isEmpty()) {
            // 这里可以选择下载GitHub头像到本地，或者直接使用GitHub的URL
            // 为简单起见，我们先使用默认头像
            user.setAvatar(getUserService().assignDefaultAvatar());
        } else {
            user.setAvatar(getUserService().assignDefaultAvatar());
        }
        
        // 保存用户
        getUserService().saveUser(user);
        
        // 分配学生角色（或者其他默认角色）
        getRoleService().assignRoleToUser(user.getId(), RoleConstants.DB_ROLE_STUDENT);
        
        return user;
    }
} 