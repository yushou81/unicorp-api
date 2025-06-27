package com.csu.unicorp.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csu.unicorp.common.constants.RoleConstants;
import com.csu.unicorp.common.exception.BusinessException;
import com.csu.unicorp.common.utils.AccountGenerator;
import com.csu.unicorp.common.utils.JwtUtil;
import com.csu.unicorp.dto.EnterpriseRegistrationDTO;
import com.csu.unicorp.dto.LoginCredentialsDTO;
import com.csu.unicorp.dto.OrgMemberCreationDTO;
import com.csu.unicorp.dto.OrgMemberUpdateDTO;
import com.csu.unicorp.dto.StudentRegistrationDTO;
import com.csu.unicorp.entity.EnterpriseDetail;
import com.csu.unicorp.entity.Organization;
import com.csu.unicorp.entity.User;
import com.csu.unicorp.entity.UserVerification;
import com.csu.unicorp.mapper.UserMapper;
import com.csu.unicorp.mapper.UserVerificationMapper;
import com.csu.unicorp.service.EnterpriseService;
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

import java.time.LocalDateTime;
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
    private final EnterpriseService enterpriseService;
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
        
        // 获取用户角色（只有一个角色）
        String role = getUserRoles(user.getId()).get(0);
        
        // 构建并返回TokenVO，包含token、nickname和role
        return TokenVO.builder()
                .token(token)
                .nickname(user.getNickname())
                .role(role)
                .build();
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
        
        // 保存用户
        userMapper.insert(user);
        
        // 分配企业管理员角色
        roleService.assignRoleToUser(user.getId(), RoleConstants.DB_ROLE_ENTERPRISE_ADMIN);
        
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
    
    @Override
    @Transactional
    public UserVO createTeacher(OrgMemberCreationDTO teacherDTO, UserDetails userDetails) {
        // 获取当前登录的学校管理员
        User currentAdmin = userMapper.selectByAccount(userDetails.getUsername());
        if (currentAdmin == null) {
            throw new BusinessException("用户不存在");
        }
        
        // 验证当前用户是否为学校管理员
        List<String> roles = getUserRoles(currentAdmin.getId());
        if (!roles.contains(RoleConstants.DB_ROLE_SCHOOL_ADMIN)) {
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
        List<String> roles = getUserRoles(currentAdmin.getId());
        if (!roles.contains(RoleConstants.DB_ROLE_ENTERPRISE_ADMIN)) {
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
    
    @Override
    public IPage<UserVO> getTeachers(int page, int size, UserDetails userDetails) {
        // 获取当前登录的学校管理员
        User currentAdmin = userMapper.selectByAccount(userDetails.getUsername());
        if (currentAdmin == null) {
            throw new BusinessException("用户不存在");
        }
        
        // 验证当前用户是否为学校管理员
        List<String> roles = getUserRoles(currentAdmin.getId());
        if (!roles.contains(RoleConstants.DB_ROLE_SCHOOL_ADMIN)) {
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
        List<String> roles = getUserRoles(currentAdmin.getId());
        if (!roles.contains(RoleConstants.DB_ROLE_SCHOOL_ADMIN)) {
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
        List<String> teacherRoles = getUserRoles(teacher.getId());
        if (!teacherRoles.contains(RoleConstants.DB_ROLE_TEACHER)) {
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
        List<String> roles = getUserRoles(currentAdmin.getId());
        if (!roles.contains(RoleConstants.DB_ROLE_SCHOOL_ADMIN)) {
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
        List<String> teacherRoles = getUserRoles(teacher.getId());
        if (!teacherRoles.contains(RoleConstants.DB_ROLE_TEACHER)) {
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
        List<String> roles = getUserRoles(currentAdmin.getId());
        if (!roles.contains(RoleConstants.DB_ROLE_ENTERPRISE_ADMIN)) {
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
        List<String> roles = getUserRoles(currentAdmin.getId());
        if (!roles.contains(RoleConstants.DB_ROLE_ENTERPRISE_ADMIN)) {
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
        List<String> mentorRoles = getUserRoles(mentor.getId());
        if (!mentorRoles.contains(RoleConstants.DB_ROLE_ENTERPRISE_MENTOR)) {
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
        List<String> roles = getUserRoles(currentAdmin.getId());
        if (!roles.contains(RoleConstants.DB_ROLE_ENTERPRISE_ADMIN)) {
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
        List<String> mentorRoles = getUserRoles(mentor.getId());
        if (!mentorRoles.contains(RoleConstants.DB_ROLE_ENTERPRISE_MENTOR)) {
            throw new BusinessException("该用户不是企业导师");
        }
        
        // 禁用导师账号
        mentor.setStatus("inactive");
        userMapper.updateById(mentor);
    }
}