package com.csu.unicorp.service.impl;

import com.csu.unicorp.common.exception.BusinessException;
import com.csu.unicorp.common.utils.AccountGenerator;
import com.csu.unicorp.common.utils.JwtUtil;
import com.csu.unicorp.dto.LoginCredentialsDTO;
import com.csu.unicorp.dto.StudentRegistrationDTO;
import com.csu.unicorp.entity.Organization;
import com.csu.unicorp.entity.User;
import com.csu.unicorp.entity.UserVerification;
import com.csu.unicorp.mapper.UserMapper;
import com.csu.unicorp.mapper.UserVerificationMapper;
import com.csu.unicorp.service.OrganizationService;
import com.csu.unicorp.service.RoleService;
import com.csu.unicorp.service.UserService;
import com.csu.unicorp.vo.TokenVO;
import com.csu.unicorp.vo.UserVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

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
    private final RoleService roleService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final AccountGenerator accountGenerator;
    
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
        
        // 使用找到的用户账号和输入的密码进行认证
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(user.getAccount(), loginDto.getPassword())
        );
        
        // 如果认证通过，获取用户详情并生成JWT令牌
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String token = jwtUtil.generateToken(userDetails);
        
        return new TokenVO(token);
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
        
        // 创建用户实体
        User user = new User();
        user.setAccount(generatedAccount);
        user.setPassword(passwordEncoder.encode(registrationDto.getPassword()));
        user.setEmail(registrationDto.getEmail());
        user.setPhone(registrationDto.getPhone());
        user.setNickname(registrationDto.getNickname() != null 
                ? registrationDto.getNickname() 
                : registrationDto.getRealName()); // 如果未提供昵称，默认使用实名
        user.setOrganizationId(registrationDto.getOrganizationId());
        user.setStatus("active"); // 学生注册直接设置为活跃状态
        
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
        roleService.assignRoleToUser(user.getId(), "学生");
        
        // 返回用户VO
        return convertToVO(user);
    }
    
    @Override
    public UserVO getCurrentUser(UserDetails userDetails) {
        String account = userDetails.getUsername();
        User user = getByAccount(account);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        return convertToVO(user);
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
    public List<String> getUserRoles(Integer userId) {
        return userMapper.selectRolesByUserId(userId);
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
        vo.setStatus(user.getStatus());
        vo.setOrganizationId(user.getOrganizationId());
        vo.setCreatedAt(user.getCreatedAt());
        
        // 获取用户角色
        List<String> roles = getUserRoles(user.getId());
        vo.setRoles(roles != null ? roles : new ArrayList<>());
        
        return vo;
    }
}