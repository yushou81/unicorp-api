package com.csu.unicorp.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csu.unicorp.common.constants.CacheConstants;
import com.csu.unicorp.common.constants.RoleConstants;
import com.csu.unicorp.common.exception.BusinessException;
import com.csu.unicorp.common.utils.AccountGenerator;
import com.csu.unicorp.common.utils.JwtUtil;
import com.csu.unicorp.dto.EnterpriseRegistrationDTO;
import com.csu.unicorp.dto.LoginCredentialsDTO;
import com.csu.unicorp.dto.OrgMemberCreationDTO;
import com.csu.unicorp.dto.OrgMemberUpdateDTO;
import com.csu.unicorp.dto.PasswordUpdateDTO;
import com.csu.unicorp.dto.RefreshTokenDTO;
import com.csu.unicorp.dto.StudentRegistrationDTO;
import com.csu.unicorp.dto.UserProfileUpdateDTO;
import com.csu.unicorp.dto.UserUpdateDTO;
import com.csu.unicorp.entity.EnterpriseDetail;
import com.csu.unicorp.entity.organization.Organization;
import com.csu.unicorp.entity.User;
import com.csu.unicorp.entity.UserVerification;
import com.csu.unicorp.mapper.UserMapper;
import com.csu.unicorp.mapper.UserVerificationMapper;
import com.csu.unicorp.service.CacheService;
import com.csu.unicorp.service.EnterpriseService;
import com.csu.unicorp.service.FileService;
import com.csu.unicorp.service.LoginAttemptService;
import com.csu.unicorp.service.OrganizationService;
import com.csu.unicorp.service.RoleService;
import com.csu.unicorp.service.TokenBlacklistService;
import com.csu.unicorp.service.UserService;
import com.csu.unicorp.vo.TokenVO;
import com.csu.unicorp.vo.UserVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * 用户服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    
    private final UserMapper userMapper;
    private final UserVerificationMapper userVerificationMapper;
    private final OrganizationService organizationService;
    private final EnterpriseService enterpriseService;
    private final RoleService roleService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final AccountGenerator accountGenerator;
    private final FileService fileService;
    private final TokenBlacklistService tokenBlacklistService;
    private final LoginAttemptService loginAttemptService;
    private final CacheService cacheService;
    private final UserDetailsService userDetailsService;
    
    @Override
    public TokenVO login(LoginCredentialsDTO loginDto) {
        // 根据登录类型查找用户
        User user = null;
        switch (loginDto.getLoginType()) {
            case "account":
                user = userMapper.selectByAccount(loginDto.getPrincipal());
                break;
            case "email":
                // 这里需要添加通过邮箱查询用户的方法
                user = userMapper.selectByEmail(loginDto.getPrincipal());
                break;
            case "phone":
                // 这里需要添加通过手机号查询用户的方法
                user = userMapper.selectByPhone(loginDto.getPrincipal());
                break;
            default:
                throw new BusinessException("不支持的登录类型");
        }
        
        if (user == null) {
            throw new BadCredentialsException("用户不存在");
        }
        
        // 检查用户是否被锁定
        if (loginAttemptService.isLocked(user.getAccount())) {
            long lockTimeRemaining = loginAttemptService.getLockTimeRemaining(user.getAccount());
            throw new LockedException("账户已被锁定，请在" + lockTimeRemaining + "分钟后重试");
        }
        
        log.info("user: {}", user);
        
        try {
            // 使用找到的用户账号和输入的密码进行认证
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getAccount(), loginDto.getPassword())
            );
            
            // 如果认证通过，获取用户详情并生成JWT令牌
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    
            // 生成访问令牌
            String token = jwtUtil.generateToken(userDetails);
            
            // 生成刷新令牌
            String refreshToken = jwtUtil.generateRefreshToken(userDetails);
            
            // 获取用户角色（只有一个角色）
            String role = getUserRole(user.getId());
            
            // 构建并返回TokenVO，包含token、nickname和role
            TokenVO tokenVO = TokenVO.builder()
                    .token(token)
                    .refreshToken(refreshToken)
                    .nickname(user.getNickname())
                    .role(role)
                    .build();
                    
            // 设置头像URL
            if (user.getAvatar() != null && !user.getAvatar().isEmpty()) {
                tokenVO.setAvatar(fileService.getFullFileUrl(user.getAvatar()));
            }
            
            // 重置登录尝试次数
            loginAttemptService.resetAttempts(user.getAccount());
            
            // 存储会话信息
            storeSessionInfo(user.getId(), token, refreshToken);
            
            return tokenVO;
        } catch (BadCredentialsException e) {
            // 记录登录失败
            int remainingAttempts = loginAttemptService.recordFailedAttempt(user.getAccount());
            throw new BadCredentialsException("密码错误，剩余尝试次数：" + remainingAttempts + 
                (remainingAttempts <= 1 ? "，失败次数过多将锁定账户" + CacheConstants.AUTH_DEFAULT_LOCK_TIME + "分钟" : ""));
        } catch (LockedException e) {
            // 直接抛出锁定异常
            throw e;
        }
    }
    
    @Override
    public void logout(String token, UserDetails userDetails) {
        if (token == null || token.isEmpty()) {
            throw new BusinessException("令牌不能为空");
        }
        
        // 从令牌中提取用户名
        String username = jwtUtil.extractUsername(token);
        
        // 验证令牌是否属于当前用户
        if (!username.equals(userDetails.getUsername())) {
            throw new BusinessException("无效的令牌");
        }
        
        // 获取用户信息
        User user = userMapper.selectByAccount(username);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        // 计算令牌剩余有效期
        long expirationTimeInSeconds = jwtUtil.getExpirationTime(token);
        if (expirationTimeInSeconds > 0) {
            // 将令牌添加到黑名单
            tokenBlacklistService.addToBlacklist(token, "用户登出", expirationTimeInSeconds);
        }
        
        // 清除会话信息
        clearSessionInfo(user.getId());
        
        log.info("用户 {} 已登出", username);
    }
    
    @Override
    public TokenVO refreshToken(RefreshTokenDTO refreshTokenDTO) {
        String refreshToken = refreshTokenDTO.getRefreshToken();
        
        if (refreshToken == null || refreshToken.isEmpty()) {
            throw new BusinessException("刷新令牌不能为空");
        }
        
        try {
            // 验证刷新令牌是否有效
            if (!jwtUtil.isRefreshToken(refreshToken)) {
                throw new BusinessException("无效的刷新令牌");
            }
            
            // 从刷新令牌中提取用户名
            String username = jwtUtil.extractUsername(refreshToken);
            
            // 获取用户信息
            User user = userMapper.selectByAccount(username);
            if (user == null) {
                throw new BusinessException("用户不存在");
            }
            
            // 验证刷新令牌是否在缓存中
            String tokenId = jwtUtil.extractTokenId(refreshToken);
            String cacheKey = CacheConstants.AUTH_REFRESH_TOKEN_PREFIX + user.getId();
            
            // 获取缓存中的令牌ID（仅使用新格式）
            Map<String, String> tokenIdMap = cacheService.get(cacheKey, Map.class);
            String cachedTokenId = tokenIdMap != null ? tokenIdMap.get("id") : null;
            
            if (cachedTokenId == null || !cachedTokenId.equals(tokenId)) {
                throw new BusinessException("刷新令牌已失效，请重新登录");
            }
            
            // 加载用户详情
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            
            // 生成新的访问令牌
            String newToken = jwtUtil.generateToken(userDetails);
            
            // 生成新的刷新令牌
            String newRefreshToken = jwtUtil.generateRefreshToken(userDetails);
            
            // 获取用户角色
            String role = getUserRole(user.getId());
            
            // 构建并返回TokenVO
            TokenVO tokenVO = TokenVO.builder()
                    .token(newToken)
                    .refreshToken(newRefreshToken)
                    .nickname(user.getNickname())
                    .role(role)
                    .build();
                    
            // 设置头像URL
            if (user.getAvatar() != null && !user.getAvatar().isEmpty()) {
                tokenVO.setAvatar(fileService.getFullFileUrl(user.getAvatar()));
            }
            
            // 更新会话信息
            storeSessionInfo(user.getId(), newToken, newRefreshToken);
            
            return tokenVO;
        } catch (Exception e) {
            throw new BusinessException("刷新令牌失败：" + e.getMessage());
        }
    }
    
    /**
     * 存储会话信息
     * 
     * @param userId 用户ID
     * @param token 访问令牌
     * @param refreshToken 刷新令牌
     */
    private void storeSessionInfo(Integer userId, String token, String refreshToken) {
        // 存储会话信息
        String sessionKey = CacheConstants.AUTH_SESSION_PREFIX + userId;
        Map<String, Object> sessionInfo = new HashMap<>();
        sessionInfo.put("token", token);
        sessionInfo.put("refreshToken", refreshToken);
        sessionInfo.put("lastLoginTime", System.currentTimeMillis());
        
        // 设置会话信息，过期时间与访问令牌相同
        cacheService.setHash(sessionKey, sessionInfo, jwtUtil.getExpirationTime(token), TimeUnit.SECONDS);
        
        // 存储刷新令牌ID，用于验证
        String refreshTokenKey = CacheConstants.AUTH_REFRESH_TOKEN_PREFIX + userId;
        String tokenId = jwtUtil.extractTokenId(refreshToken);
        // 使用JSON格式存储令牌ID，确保格式一致性
        Map<String, String> tokenIdMap = new HashMap<>();
        tokenIdMap.put("id", tokenId);
        cacheService.set(refreshTokenKey, tokenIdMap, jwtUtil.getExpirationTime(refreshToken), TimeUnit.SECONDS);
    }
    
    /**
     * 清除会话信息
     * 
     * @param userId 用户ID
     */
    private void clearSessionInfo(Integer userId) {
        String sessionKey = CacheConstants.AUTH_SESSION_PREFIX + userId;
        String refreshTokenKey = CacheConstants.AUTH_REFRESH_TOKEN_PREFIX + userId;
        
        cacheService.delete(sessionKey);
        cacheService.delete(refreshTokenKey);
    }
    
    @Override
    @Transactional
    public UserVO registerStudent(StudentRegistrationDTO registrationDto) {
        // 检查邮箱是否已存在
        User existingUserByEmail = userMapper.selectByEmail(registrationDto.getEmail());
        if (existingUserByEmail != null) {
            throw new BusinessException("该邮箱已被注册");
        }
        
        // 如果提供了手机号，检查是否已存在
        if (registrationDto.getPhone() != null && !registrationDto.getPhone().isEmpty()) {
            User existingUserByPhone = userMapper.selectByPhone(registrationDto.getPhone());
            if (existingUserByPhone != null) {
                throw new BusinessException("该手机号已被注册");
            }
        }
        
        // 检查组织是否存在
        Organization organization = organizationService.getById(registrationDto.getOrganizationId());
        if (organization == null) {
            throw new BusinessException("所选学校不存在");
        }
        
        if (!"School".equals(organization.getType())) {
            throw new BusinessException("所选组织不是学校");
        }
        
        if (!"approved".equals(organization.getStatus())) {
            throw new BusinessException("所选学校未通过审核");
        }
        
        // 生成系统账号
        String generatedAccount = accountGenerator.generateStudentAccount(organization);
        
        // 创建用户
        User user = new User();
        user.setAccount(generatedAccount);
        user.setPassword(passwordEncoder.encode(registrationDto.getPassword()));
        user.setEmail(registrationDto.getEmail());
        user.setPhone(registrationDto.getPhone());
        user.setNickname(registrationDto.getNickname() != null ? registrationDto.getNickname() : "学生-" + registrationDto.getRealName());
        user.setOrganizationId(organization.getId());
        user.setStatus("active"); // 学生账号直接设置为活跃状态
        user.setCreatedAt(LocalDateTime.now()); // 设置创建时间
        
        // 分配默认头像
        user.setAvatar(assignDefaultAvatar());
        
        // 保存用户
        userMapper.insert(user);
        
        // 保存用户实名认证信息
        UserVerification verification = new UserVerification();
        verification.setUserId(user.getId());
        verification.setRealName(registrationDto.getRealName());
        verification.setIdCard(registrationDto.getIdCard()); // 实际应用中需要加密存储
        verification.setVerificationStatus("unverified"); // 初始状态为未验证
        
        userVerificationMapper.insert(verification);
        
        // 分配学生角色
        roleService.assignRoleToUser(user.getId(), RoleConstants.DB_ROLE_STUDENT);
        
        // 返回用户VO
        return convertToVO(user);
    }
    
    @Override
    @Transactional
    public UserVO registerEnterprise(EnterpriseRegistrationDTO registrationDto) {
        // 检查邮箱是否已存在
        User existingUserByEmail = userMapper.selectByEmail(registrationDto.getAdminEmail());
        if (existingUserByEmail != null) {
            throw new BusinessException("该邮箱已被注册");
        }
        
        // 如果提供了手机号，检查是否已存在
        if (registrationDto.getAdminPhone() != null && !registrationDto.getAdminPhone().isEmpty()) {
            User existingUserByPhone = userMapper.selectByPhone(registrationDto.getAdminPhone());
            if (existingUserByPhone != null) {
                throw new BusinessException("该手机号已被注册");
            }
        }
        
        // 检查企业名称是否已存在
        Organization existingOrg = organizationService.getByName(registrationDto.getOrganizationName());
        if (existingOrg != null) {
            throw new BusinessException("该企业名称已被注册");
        }
        
        // 创建企业组织
        Organization organization = new Organization();
        organization.setOrganizationName(registrationDto.getOrganizationName());
        organization.setDescription(registrationDto.getDescription());
        organization.setAddress(registrationDto.getAddress());
        organization.setWebsite(registrationDto.getWebsite());
        organization.setType("Enterprise");
        organization.setStatus("pending"); // 企业注册初始状态为待审核
        
        // 创建企业详情
        EnterpriseDetail enterpriseDetail = new EnterpriseDetail();
        enterpriseDetail.setIndustry(registrationDto.getIndustry());
        enterpriseDetail.setCompanySize(registrationDto.getCompanySize());
        enterpriseDetail.setBusinessLicenseUrl(registrationDto.getBusinessLicenseUrl());
        
        // 保存企业信息
        Integer organizationId = enterpriseService.createEnterprise(organization, enterpriseDetail);
        
        // 生成企业管理员账号
        String generatedAccount = "ent_" + accountGenerator.generateStudentAccount(organization).substring(0, 8);
        
        // 创建企业管理员账号
        User user = new User();
        user.setAccount(generatedAccount);
        user.setPassword(passwordEncoder.encode(registrationDto.getAdminPassword()));
        user.setEmail(registrationDto.getAdminEmail());
        user.setPhone(registrationDto.getAdminPhone());
        user.setNickname(registrationDto.getAdminNickname() != null 
                ? registrationDto.getAdminNickname() 
                : "管理员-" + registrationDto.getOrganizationName()); // 如果未提供昵称，使用默认值
        user.setOrganizationId(organizationId);
        user.setStatus("pending_approval"); // 企业管理员初始状态为待审核
        user.setCreatedAt(LocalDateTime.now()); // 设置创建时间
        
        // 分配默认头像
        user.setAvatar(assignDefaultAvatar());
        
        // 保存用户
        userMapper.insert(user);
        
        // 分配企业管理员角色
        roleService.assignRoleToUser(user.getId(), RoleConstants.DB_ROLE_ENTERPRISE_ADMIN);
        
        // 返回用户VO
        return convertToVO(user);
    }
    
    @Override
    public UserVO getCurrentUser(UserDetails userDetails) {
        User user = userMapper.selectByAccount(userDetails.getUsername());
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        UserVO userVO = convertToVO(user);
        
        // 处理头像URL，将相对路径转换为完整URL
        if (userVO.getAvatar() != null && !userVO.getAvatar().isEmpty()) {
            userVO.setAvatar(fileService.getFullFileUrl(userVO.getAvatar()));
        }
        
        return userVO;
    }
    
    @Override
    public User getById(Integer id) {
        return userMapper.selectById(id);
    }
    
    @Override
    public User getByAccount(String account) {
        return userMapper.selectByAccount(account);
    }
    
    @Override
    public User getByEmail(String email) {
        return userMapper.selectByEmail(email);
    }
    
    @Override
    public User getByPhone(String phone) {
        return userMapper.selectByPhone(phone);
    }
    
    @Override
    public String getUserRole(Integer userId) {
        return userMapper.selectRoleByUserId(userId);
    }
    
    @Override
    @Transactional
    public UserVO createTeacher(OrgMemberCreationDTO teacherDTO, UserDetails userDetails) {
        // 获取当前登录的学校管理员
        User currentAdmin = userMapper.selectByAccount(userDetails.getUsername());
        if (currentAdmin == null) {
            throw new BusinessException("用户不存在");
        }
        
        // 验证当前用户是否为学校管理员
        String role = getUserRole(currentAdmin.getId());
        if (!RoleConstants.DB_ROLE_SCHOOL_ADMIN.equals(role)) {
            throw new BusinessException("权限不足，只有学校管理员可以创建教师账号");
        }
        
        // 获取管理员所属的学校
        Organization school = organizationService.getById(currentAdmin.getOrganizationId());
        if (school == null || !"School".equals(school.getType())) {
            throw new BusinessException("管理员不属于有效的学校组织");
        }
        
        // 检查邮箱是否已存在
        User existingUserByEmail = userMapper.selectByEmail(teacherDTO.getEmail());
        if (existingUserByEmail != null) {
            throw new BusinessException("该邮箱已被注册");
        }
        
        // 如果提供了手机号，检查是否已存在
        if (teacherDTO.getPhone() != null && !teacherDTO.getPhone().isEmpty()) {
            User existingUserByPhone = userMapper.selectByPhone(teacherDTO.getPhone());
            if (existingUserByPhone != null) {
                throw new BusinessException("该手机号已被注册");
            }
        }
        
        // 生成教师账号
        String generatedAccount = accountGenerator.generateTeacherAccount(school);
        
        // 创建教师用户
        User teacher = new User();
        teacher.setAccount(generatedAccount);
        teacher.setPassword(passwordEncoder.encode(teacherDTO.getPassword()));
        teacher.setEmail(teacherDTO.getEmail());
        teacher.setPhone(teacherDTO.getPhone());
        teacher.setNickname(teacherDTO.getNickname() != null ? teacherDTO.getNickname() : "教师-" + school.getOrganizationName());
        teacher.setOrganizationId(school.getId());
        teacher.setStatus("active"); // 教师账号直接设置为活跃状态
        teacher.setCreatedAt(LocalDateTime.now()); // 设置创建时间
        
        // 保存用户
        userMapper.insert(teacher);
        
        // 分配教师角色
        roleService.assignRoleToUser(teacher.getId(), RoleConstants.DB_ROLE_TEACHER);
        
        // 返回用户VO
        return convertToVO(teacher);
    }
    
    @Override
    @Transactional
    public UserVO createMentor(OrgMemberCreationDTO mentorDTO, UserDetails userDetails) {
        // 获取当前登录的企业管理员
        User currentAdmin = userMapper.selectByAccount(userDetails.getUsername());
        if (currentAdmin == null) {
            throw new BusinessException("用户不存在");
        }
        
        // 验证当前用户是否为企业管理员
        String role = getUserRole(currentAdmin.getId());
        if (!RoleConstants.DB_ROLE_ENTERPRISE_ADMIN.equals(role)) {
            throw new BusinessException("权限不足，只有企业管理员可以创建导师账号");
        }
        
        // 获取管理员所属的企业
        Organization enterprise = organizationService.getById(currentAdmin.getOrganizationId());
        if (enterprise == null || !"Enterprise".equals(enterprise.getType()) || !"approved".equals(enterprise.getStatus())) {
            throw new BusinessException("管理员不属于有效的已审核企业组织");
        }
        
        // 检查邮箱是否已存在
        User existingUserByEmail = userMapper.selectByEmail(mentorDTO.getEmail());
        if (existingUserByEmail != null) {
            throw new BusinessException("该邮箱已被注册");
        }
        
        // 如果提供了手机号，检查是否已存在
        if (mentorDTO.getPhone() != null && !mentorDTO.getPhone().isEmpty()) {
            User existingUserByPhone = userMapper.selectByPhone(mentorDTO.getPhone());
            if (existingUserByPhone != null) {
                throw new BusinessException("该手机号已被注册");
            }
        }
        
        // 生成导师账号
        String generatedAccount = accountGenerator.generateMentorAccount(enterprise);
        
        // 创建导师用户
        User mentor = new User();
        mentor.setAccount(generatedAccount);
        mentor.setPassword(passwordEncoder.encode(mentorDTO.getPassword()));
        mentor.setEmail(mentorDTO.getEmail());
        mentor.setPhone(mentorDTO.getPhone());
        mentor.setNickname(mentorDTO.getNickname() != null ? mentorDTO.getNickname() : "导师-" + enterprise.getOrganizationName());
        mentor.setOrganizationId(enterprise.getId());
        mentor.setStatus("active"); // 导师账号直接设置为活跃状态
        mentor.setCreatedAt(LocalDateTime.now()); // 设置创建时间
        
        // 保存用户
        userMapper.insert(mentor);
        
        // 分配导师角色
        roleService.assignRoleToUser(mentor.getId(), RoleConstants.DB_ROLE_ENTERPRISE_MENTOR);
        
        // 返回用户VO
        return convertToVO(mentor);
    }
    
    @Override
    @Transactional
    public UserVO updateUserProfile(UserProfileUpdateDTO profileUpdateDTO, UserDetails userDetails) {
        // 获取当前登录用户
        User currentUser = userMapper.selectByAccount(userDetails.getUsername());
        if (currentUser == null) {
            throw new BusinessException("用户不存在");
        }
        
        // 检查邮箱是否被其他用户使用
        if (profileUpdateDTO.getEmail() != null && !profileUpdateDTO.getEmail().equals(currentUser.getEmail())) {
            User existingUserByEmail = userMapper.selectByEmail(profileUpdateDTO.getEmail());
            if (existingUserByEmail != null && !Objects.equals(existingUserByEmail.getId(), currentUser.getId())) {
                throw new BusinessException("该邮箱已被其他用户使用");
            }
            currentUser.setEmail(profileUpdateDTO.getEmail());
        }
        
        // 检查手机号是否被其他用户使用
        if (profileUpdateDTO.getPhone() != null && !profileUpdateDTO.getPhone().equals(currentUser.getPhone())) {
            User existingUserByPhone = userMapper.selectByPhone(profileUpdateDTO.getPhone());
            if (existingUserByPhone != null && !Objects.equals(existingUserByPhone.getId(), currentUser.getId())) {
                throw new BusinessException("该手机号已被其他用户使用");
            }
            currentUser.setPhone(profileUpdateDTO.getPhone());
        }
        
        // 更新昵称
        if (profileUpdateDTO.getNickname() != null) {
            currentUser.setNickname(profileUpdateDTO.getNickname());
        }
        
        // 更新时间
        currentUser.setUpdatedAt(LocalDateTime.now());
        
        // 保存更新
        userMapper.updateById(currentUser);
        
        // 返回更新后的用户信息
        return convertToVO(currentUser);
    }
    
    @Override
    @Transactional
    public void updatePassword(PasswordUpdateDTO passwordUpdateDTO, UserDetails userDetails) {
        // 获取当前登录用户
        User currentUser = userMapper.selectByAccount(userDetails.getUsername());
        if (currentUser == null) {
            throw new BusinessException("用户不存在");
        }
        
        // 验证原密码是否正确
        if (!passwordEncoder.matches(passwordUpdateDTO.getOldPassword(), currentUser.getPassword())) {
            throw new BusinessException("原密码不正确");
        }
        
        // 验证新密码和确认密码是否一致
        if (!passwordUpdateDTO.getNewPassword().equals(passwordUpdateDTO.getConfirmPassword())) {
            throw new BusinessException("新密码和确认密码不一致");
        }
        
        // 更新密码
        currentUser.setPassword(passwordEncoder.encode(passwordUpdateDTO.getNewPassword()));
        
        // 更新时间
        currentUser.setUpdatedAt(LocalDateTime.now());
        
        // 保存更新
        userMapper.updateById(currentUser);
    }
    
    /**
     * 将User实体转换为UserVO
     * 
     * @param user 用户实体
     * @return 用户VO
     */
    private UserVO convertToVO(User user) {
        if (user == null) {
            return null;
        }
        
        UserVO vo = new UserVO();
        vo.setId(user.getId());
        vo.setAccount(user.getAccount());
        vo.setNickname(user.getNickname());
        vo.setEmail(user.getEmail());
        vo.setPhone(user.getPhone());
        vo.setAvatar(user.getAvatar());
        vo.setStatus(user.getStatus());
        vo.setOrganizationId(user.getOrganizationId());
        vo.setCreatedAt(user.getCreatedAt());
        
        // 获取用户角色
        String role = getUserRole(user.getId());
        vo.setRole(role);
        
        // 获取组织名称
        if (user.getOrganizationId() != null) {
            Organization organization = organizationService.getById(user.getOrganizationId());
            log.info("organization: {}", organization);
            if (organization != null) {
                vo.setOrganizationName(organization.getOrganizationName());
            }
        }
        
        return vo;
    }
    
    @Override
    public IPage<UserVO> getTeachers(int page, int size, UserDetails userDetails) {
        // 获取当前登录的学校管理员
        User currentAdmin = userMapper.selectByAccount(userDetails.getUsername());
        if (currentAdmin == null) {
            throw new BusinessException("用户不存在");
        }
        
        // 验证当前用户是否为学校管理员
        String role = getUserRole(currentAdmin.getId());
        if (!RoleConstants.DB_ROLE_SCHOOL_ADMIN.equals(role)) {
            throw new BusinessException("权限不足，只有学校管理员可以查看教师列表");
        }
        
        // 获取管理员所属的学校ID
        Integer schoolId = currentAdmin.getOrganizationId();
        
        // 创建分页对象
        Page<User> pageParam = new Page<>(page, size);
        
        // 查询该学校的所有教师
        IPage<User> teacherPage = userMapper.selectTeachersByOrganizationId(schoolId, pageParam);
        
        // 转换为VO
        return teacherPage.convert(this::convertToVO);
    }
    
    @Override
    public UserVO updateTeacher(Integer id, OrgMemberUpdateDTO updateDTO, UserDetails userDetails) {
        // 获取当前登录的学校管理员
        User currentAdmin = userMapper.selectByAccount(userDetails.getUsername());
        if (currentAdmin == null) {
            throw new BusinessException("用户不存在");
        }
        
        // 验证当前用户是否为学校管理员
        String role = getUserRole(currentAdmin.getId());
        if (!RoleConstants.DB_ROLE_SCHOOL_ADMIN.equals(role)) {
            throw new BusinessException("权限不足，只有学校管理员可以更新教师信息");
        }
        
        // 获取要更新的教师
        User teacher = userMapper.selectById(id);
        if (teacher == null) {
            throw new BusinessException("教师不存在");
        }
        
        // 验证教师是否属于该学校
        if (!teacher.getOrganizationId().equals(currentAdmin.getOrganizationId())) {
            throw new BusinessException("权限不足，只能更新本校教师信息");
        }
        
        // 验证是否为教师角色
        String teacherRole = getUserRole(teacher.getId());
        if (!RoleConstants.DB_ROLE_TEACHER.equals(teacherRole)) {
            throw new BusinessException("该用户不是教师");
        }
        
        // 更新教师信息
        if (updateDTO.getNickname() != null) {
            teacher.setNickname(updateDTO.getNickname());
        }
        if (updateDTO.getPhone() != null) {
            teacher.setPhone(updateDTO.getPhone());
        }
        
        userMapper.updateById(teacher);
        
        return convertToVO(teacher);
    }
    
    @Override
    public void disableTeacher(Integer id, UserDetails userDetails) {
        // 获取当前登录的学校管理员
        User currentAdmin = userMapper.selectByAccount(userDetails.getUsername());
        if (currentAdmin == null) {
            throw new BusinessException("用户不存在");
        }
        
        // 验证当前用户是否为学校管理员
        String role = getUserRole(currentAdmin.getId());
        if (!RoleConstants.DB_ROLE_SCHOOL_ADMIN.equals(role)) {
            throw new BusinessException("权限不足，只有学校管理员可以禁用教师账号");
        }
        
        // 获取要禁用的教师
        User teacher = userMapper.selectById(id);
        if (teacher == null) {
            throw new BusinessException("教师不存在");
        }
        
        // 验证教师是否属于该学校
        if (!teacher.getOrganizationId().equals(currentAdmin.getOrganizationId())) {
            throw new BusinessException("权限不足，只能禁用本校教师账号");
        }
        
        // 验证是否为教师角色
        String teacherRole = getUserRole(teacher.getId());
        if (!RoleConstants.DB_ROLE_TEACHER.equals(teacherRole)) {
            throw new BusinessException("该用户不是教师");
        }
        
        // 禁用教师账号
        teacher.setStatus("inactive");
        userMapper.updateById(teacher);
    }
    
    @Override
    public IPage<UserVO> getMentors(int page, int size, UserDetails userDetails) {
        // 获取当前登录的企业管理员
        User currentAdmin = userMapper.selectByAccount(userDetails.getUsername());
        if (currentAdmin == null) {
            throw new BusinessException("用户不存在");
        }
        
        // 验证当前用户是否为企业管理员
        String role = getUserRole(currentAdmin.getId());
        if (!RoleConstants.DB_ROLE_ENTERPRISE_ADMIN.equals(role)) {
            throw new BusinessException("权限不足，只有企业管理员可以查看导师列表");
        }
        
        // 获取管理员所属的企业ID
        Integer enterpriseId = currentAdmin.getOrganizationId();
        
        // 创建分页对象
        Page<User> pageParam = new Page<>(page, size);
        
        // 查询该企业的所有导师
        IPage<User> mentorPage = userMapper.selectMentorsByOrganizationId(enterpriseId, pageParam);
        
        // 转换为VO
        return mentorPage.convert(this::convertToVO);
    }
    
    @Override
    public UserVO updateMentor(Integer id, OrgMemberUpdateDTO updateDTO, UserDetails userDetails) {
        // 获取当前登录的企业管理员
        User currentAdmin = userMapper.selectByAccount(userDetails.getUsername());
        if (currentAdmin == null) {
            throw new BusinessException("用户不存在");
        }
        
        // 验证当前用户是否为企业管理员
        String role = getUserRole(currentAdmin.getId());
        if (!RoleConstants.DB_ROLE_ENTERPRISE_ADMIN.equals(role)) {
            throw new BusinessException("权限不足，只有企业管理员可以更新导师信息");
        }
        
        // 获取要更新的导师
        User mentor = userMapper.selectById(id);
        if (mentor == null) {
            throw new BusinessException("导师不存在");
        }
        
        // 验证导师是否属于该企业
        if (!mentor.getOrganizationId().equals(currentAdmin.getOrganizationId())) {
            throw new BusinessException("权限不足，只能更新本企业导师信息");
        }
        
        // 验证是否为导师角色
        String mentorRole = getUserRole(mentor.getId());
        if (!RoleConstants.DB_ROLE_ENTERPRISE_MENTOR.equals(mentorRole)) {
            throw new BusinessException("该用户不是企业导师");
        }
        
        // 更新导师信息
        if (updateDTO.getNickname() != null) {
            mentor.setNickname(updateDTO.getNickname());
        }
        if (updateDTO.getPhone() != null) {
            mentor.setPhone(updateDTO.getPhone());
        }
        
        userMapper.updateById(mentor);
        
        return convertToVO(mentor);
    }
    
    @Override
    public void disableMentor(Integer id, UserDetails userDetails) {
        // 获取当前登录的企业管理员
        User currentAdmin = userMapper.selectByAccount(userDetails.getUsername());
        if (currentAdmin == null) {
            throw new BusinessException("用户不存在");
        }
        
        // 验证当前用户是否为企业管理员
        String role = getUserRole(currentAdmin.getId());
        if (!RoleConstants.DB_ROLE_ENTERPRISE_ADMIN.equals(role)) {
            throw new BusinessException("权限不足，只有企业管理员可以禁用导师账号");
        }
        
        // 获取要禁用的导师
        User mentor = userMapper.selectById(id);
        if (mentor == null) {
            throw new BusinessException("导师不存在");
        }
        
        // 验证导师是否属于该企业
        if (!mentor.getOrganizationId().equals(currentAdmin.getOrganizationId())) {
            throw new BusinessException("权限不足，只能禁用本企业导师账号");
        }
        
        // 验证是否为导师角色
        String mentorRole = getUserRole(mentor.getId());
        if (!RoleConstants.DB_ROLE_ENTERPRISE_MENTOR.equals(mentorRole)) {
            throw new BusinessException("该用户不是企业导师");
        }
        
        // 禁用导师账号
        mentor.setStatus("inactive");
        userMapper.updateById(mentor);
    }
    
    @Override
    public IPage<UserVO> getUsersByRole(int page, int size, String role) {
        // 创建分页对象，注意页码从1开始，需要转换为从0开始
        Page<User> pageParam = new Page<>(page, size);
        
        // 查询用户列表
        IPage<User> userPage;
        if (role != null && !role.isEmpty()) {
            // 根据角色查询用户
            Integer roleId = roleService.getByRoleName(role).getId();
            userPage = userMapper.selectUsersByRoleId(roleId, pageParam);
        } else {
            // 查询所有用户，但排除系统管理员
            Integer sysAdminRoleId = roleService.getByRoleName(RoleConstants.DB_ROLE_SYSTEM_ADMIN).getId();
            userPage = userMapper.selectUsersExcludeRole(sysAdminRoleId, pageParam);
        }
        
        // 转换为VO
        return userPage.convert(this::convertToVO);
    }
    
    @Override
    @Transactional
    public UserVO updateUserStatus(Integer id, String status) {
        // 获取用户
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        // 检查是否为系统管理员
        String role = getUserRole(user.getId());
        if (RoleConstants.DB_ROLE_SYSTEM_ADMIN.equals(role)) {
            throw new BusinessException("不能修改系统管理员状态");
        }
        
        // 更新状态
        user.setStatus(status);
        user.setUpdatedAt(LocalDateTime.now());
        userMapper.updateById(user);
        
        return convertToVO(user);
    }
    
    @Override
    @Transactional
    public UserVO updateUserByAdmin(Integer id, UserUpdateDTO userUpdateDTO) {
        // 获取用户
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        // 检查是否为系统管理员
        String role = getUserRole(user.getId());
        if (RoleConstants.DB_ROLE_SYSTEM_ADMIN.equals(role)) {
            throw new BusinessException("不能修改系统管理员信息");
        }
        
        // 检查邮箱是否被其他用户使用
        if (userUpdateDTO.getEmail() != null && !userUpdateDTO.getEmail().equals(user.getEmail())) {
            User existingUserByEmail = userMapper.selectByEmail(userUpdateDTO.getEmail());
            if (existingUserByEmail != null && !Objects.equals(existingUserByEmail.getId(), user.getId())) {
                throw new BusinessException("该邮箱已被其他用户使用");
            }
            user.setEmail(userUpdateDTO.getEmail());
        }
        
        // 检查手机号是否被其他用户使用
        if (userUpdateDTO.getPhone() != null && !userUpdateDTO.getPhone().equals(user.getPhone())) {
            User existingUserByPhone = userMapper.selectByPhone(userUpdateDTO.getPhone());
            if (existingUserByPhone != null && !Objects.equals(existingUserByPhone.getId(), user.getId())) {
                throw new BusinessException("该手机号已被其他用户使用");
            }
            user.setPhone(userUpdateDTO.getPhone());
        }
        
        // 更新昵称
        if (userUpdateDTO.getNickname() != null) {
            user.setNickname(userUpdateDTO.getNickname());
        }
        
        // 更新时间
        user.setUpdatedAt(LocalDateTime.now());
        
        // 保存更新
        userMapper.updateById(user);
        
        // 返回更新后的用户信息
        return convertToVO(user);
    }

    @Override
    public String assignDefaultAvatar() {
        return fileService.getRandomDefaultAvatarPath();
    }

    @Override
    @Transactional
    public UserVO updateAvatar(MultipartFile file, UserDetails userDetails) {
        User user = userMapper.selectByAccount(userDetails.getUsername());
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        // 上传新头像
        String avatarPath = fileService.uploadFile(file, "avatars");
        
        // 更新用户头像
        user.setAvatar(avatarPath);
        userMapper.updateById(user);
        
        // 转换为VO并返回
        UserVO userVO = convertToVO(user);
        if (userVO.getAvatar() != null && !userVO.getAvatar().isEmpty()) {
            userVO.setAvatar(fileService.getFullFileUrl(userVO.getAvatar()));
        }
        
        return userVO;
    }

    @Override
    public UserVO searchUserByPhoneOrEmail(String keyword) {
        User user = null;
        
        // 尝试通过手机号查找
        user = userMapper.selectByPhone(keyword);
        
        // 如果通过手机号未找到，尝试通过邮箱查找
        if (user == null) {
            user = userMapper.selectByEmail(keyword);
        }
        
        // 如果找不到用户，抛出异常
        if (user == null) {
            throw new BusinessException("未找到匹配的用户");
        }
        
        // 转换为VO并返回
        return convertToVO(user);
    }
}