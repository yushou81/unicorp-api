package com.csu.unicorp.service.impl;

import com.csu.unicorp.common.constants.RoleConstants;
import com.csu.unicorp.common.exception.BusinessException;
import com.csu.unicorp.common.utils.AccountGenerator;
import com.csu.unicorp.common.utils.JwtUtil;
import com.csu.unicorp.dto.EnterpriseRegistrationDTO;
import com.csu.unicorp.dto.LoginCredentialsDTO;
import com.csu.unicorp.dto.OrgMemberCreationDTO;
import com.csu.unicorp.dto.StudentRegistrationDTO;
import com.csu.unicorp.entity.organization.EnterpriseDetail;
import com.csu.unicorp.entity.organization.Organization;
import com.csu.unicorp.entity.user.User;
import com.csu.unicorp.mapper.UserMapper;
import com.csu.unicorp.mapper.UserRoleMapper;
import com.csu.unicorp.mapper.UserVerificationMapper;
import com.csu.unicorp.service.EnterpriseService;
import com.csu.unicorp.service.FileService;
import com.csu.unicorp.service.OrganizationService;
import com.csu.unicorp.service.RoleService;
import com.csu.unicorp.service.impl.user.UserServiceImpl;
import com.csu.unicorp.vo.TokenVO;
import com.csu.unicorp.vo.UserVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserMapper userMapper;

    @Mock
    private UserVerificationMapper userVerificationMapper;

    @Mock
    private OrganizationService organizationService;

    @Mock
    private EnterpriseService enterpriseService;

    @Mock
    private RoleService roleService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AccountGenerator accountGenerator;

    @Mock
    private FileService fileService;

    @Mock
    private UserRoleMapper userRoleMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;
    private Organization testSchool;
    private Organization testEnterprise;
    private UserDetails testUserDetails;

    @BeforeEach
    void setUp() {
        // 创建测试用户
        testUser = new User();
        testUser.setId(1);
        testUser.setAccount("test_account");
        testUser.setPassword("encoded_password");
        testUser.setEmail("test@example.com");
        testUser.setPhone("13800138000");
        testUser.setNickname("测试用户");
        testUser.setOrganizationId(1);
        testUser.setStatus("active");
        testUser.setCreatedAt(LocalDateTime.now());
        testUser.setUpdatedAt(LocalDateTime.now());
        testUser.setIsDeleted(false);
        testUser.setAvatar("avatars/default.jpg");

        // 创建测试学校
        testSchool = new Organization();
        testSchool.setId(1);
        testSchool.setOrganizationName("测试学校");
        testSchool.setType("School");
        testSchool.setStatus("approved");

        // 创建测试企业
        testEnterprise = new Organization();
        testEnterprise.setId(2);
        testEnterprise.setOrganizationName("测试企业");
        testEnterprise.setType("Enterprise");
        testEnterprise.setStatus("approved");

        // 创建测试UserDetails
        testUserDetails = new org.springframework.security.core.userdetails.User(
                testUser.getAccount(),
                testUser.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_STUDENT"))
        );
    }

    @Test
    void testLogin_WithValidCredentials_ShouldReturnToken() {
        // 准备测试数据
        LoginCredentialsDTO loginDto = new LoginCredentialsDTO();
        loginDto.setLoginType("account");
        loginDto.setPrincipal("test_account");
        loginDto.setPassword("password123");

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(testUserDetails);

        // 配置Mock行为
        when(userMapper.selectByAccount(loginDto.getPrincipal())).thenReturn(testUser);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(jwtUtil.generateToken(testUserDetails)).thenReturn("test.jwt.token");
        when(userMapper.selectRoleByUserId(testUser.getId())).thenReturn(RoleConstants.DB_ROLE_STUDENT);
        when(fileService.getFullFileUrl(testUser.getAvatar())).thenReturn("http://example.com/avatars/default.jpg");

        // 执行测试
        TokenVO result = userService.login(loginDto);

        // 验证结果
        assertNotNull(result);
        assertEquals("test.jwt.token", result.getToken());
        assertEquals(testUser.getNickname(), result.getNickname());
        assertEquals("ROLE_STUDENT", result.getRole());
        assertEquals("http://example.com/avatars/default.jpg", result.getAvatar());

        // 验证交互
        verify(userMapper).selectByAccount(loginDto.getPrincipal());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtUtil).generateToken(testUserDetails);
        verify(userMapper).selectRoleByUserId(testUser.getId());
    }

    @Test
    void testRegisterStudent_WithValidData_ShouldCreateStudent() {
        // 准备测试数据
        StudentRegistrationDTO registrationDto = new StudentRegistrationDTO();
        registrationDto.setEmail("student@example.com");
        registrationDto.setPassword("password123");
        registrationDto.setPhone("13900139000");
        registrationDto.setNickname("学生昵称");
        registrationDto.setRealName("真实姓名");
        registrationDto.setIdCard("123456789012345678");
        registrationDto.setOrganizationId(1);

        // 配置Mock行为
        when(userMapper.selectByEmail(registrationDto.getEmail())).thenReturn(null);
        when(userMapper.selectByPhone(registrationDto.getPhone())).thenReturn(null);
        when(organizationService.getById(registrationDto.getOrganizationId())).thenReturn(testSchool);
        when(accountGenerator.generateStudentAccount(any(Organization.class))).thenReturn("S12345678");
        when(passwordEncoder.encode(registrationDto.getPassword())).thenReturn("encoded_password");
        when(fileService.getRandomDefaultAvatarPath()).thenReturn("avatars/default.jpg");
        doNothing().when(userMapper).insert(any(User.class));
        doNothing().when(userVerificationMapper).insert(any());
        doNothing().when(roleService).assignRoleToUser(anyInt(), eq(RoleConstants.DB_ROLE_STUDENT));

        // 执行测试
        UserVO result = userService.registerStudent(registrationDto);

        // 验证结果
        assertNotNull(result);
        assertEquals("S12345678", result.getAccount());
        assertEquals(registrationDto.getNickname(), result.getNickname());
        assertEquals(registrationDto.getEmail(), result.getEmail());
        assertEquals(registrationDto.getPhone(), result.getPhone());
        assertEquals("active", result.getStatus());
        assertEquals(registrationDto.getOrganizationId(), result.getOrganizationId());

        // 验证交互
        verify(userMapper).selectByEmail(registrationDto.getEmail());
        verify(userMapper).selectByPhone(registrationDto.getPhone());
        verify(organizationService).getById(registrationDto.getOrganizationId());
        verify(accountGenerator).generateStudentAccount(any(Organization.class));
        verify(passwordEncoder).encode(registrationDto.getPassword());
        verify(userMapper).insert(any(User.class));
        verify(userVerificationMapper).insert(any());
        verify(roleService).assignRoleToUser(anyInt(), eq(RoleConstants.DB_ROLE_STUDENT));
    }

    @Test
    void testRegisterEnterprise_WithValidData_ShouldCreateEnterpriseAndAdmin() {
        // 准备测试数据
        EnterpriseRegistrationDTO registrationDto = new EnterpriseRegistrationDTO();
        registrationDto.setOrganizationName("新企业");
        registrationDto.setDescription("企业描述");
        registrationDto.setAddress("企业地址");
        registrationDto.setWebsite("http://example.com");
        registrationDto.setIndustry("IT");
        registrationDto.setCompanySize("100-500");
        registrationDto.setBusinessLicenseUrl("licenses/license.jpg");
        registrationDto.setAdminEmail("admin@example.com");
        registrationDto.setAdminPhone("13800138001");
        registrationDto.setAdminPassword("admin123");
        registrationDto.setAdminNickname("企业管理员");

        // 配置Mock行为
        when(userMapper.selectByEmail(registrationDto.getAdminEmail())).thenReturn(null);
        when(userMapper.selectByPhone(registrationDto.getAdminPhone())).thenReturn(null);
        when(organizationService.getByName(registrationDto.getOrganizationName())).thenReturn(null);
        when(enterpriseService.createEnterprise(any(Organization.class), any(EnterpriseDetail.class))).thenReturn(2);
        when(accountGenerator.generateStudentAccount(any(Organization.class))).thenReturn("12345678");
        when(passwordEncoder.encode(registrationDto.getAdminPassword())).thenReturn("encoded_password");
        when(fileService.getRandomDefaultAvatarPath()).thenReturn("avatars/default.jpg");
        doNothing().when(userMapper).insert(any(User.class));
        doNothing().when(roleService).assignRoleToUser(anyInt(), eq(RoleConstants.DB_ROLE_ENTERPRISE_ADMIN));

        // 执行测试
        UserVO result = userService.registerEnterprise(registrationDto);

        // 验证结果
        assertNotNull(result);
        assertTrue(result.getAccount().startsWith("ent_"));
        assertEquals(registrationDto.getAdminNickname(), result.getNickname());
        assertEquals(registrationDto.getAdminEmail(), result.getEmail());
        assertEquals(registrationDto.getAdminPhone(), result.getPhone());
        assertEquals("pending_approval", result.getStatus());
        assertEquals(2, result.getOrganizationId());

        // 验证交互
        verify(userMapper).selectByEmail(registrationDto.getAdminEmail());
        verify(userMapper).selectByPhone(registrationDto.getAdminPhone());
        verify(organizationService).getByName(registrationDto.getOrganizationName());
        verify(enterpriseService).createEnterprise(any(Organization.class), any(EnterpriseDetail.class));
        verify(passwordEncoder).encode(registrationDto.getAdminPassword());
        verify(userMapper).insert(any(User.class));
        verify(roleService).assignRoleToUser(anyInt(), eq(RoleConstants.DB_ROLE_ENTERPRISE_ADMIN));
    }

    @Test
    void testCreateTeacher_WithValidData_ShouldCreateTeacher() {
        // 准备测试数据
        OrgMemberCreationDTO teacherDTO = new OrgMemberCreationDTO();
        teacherDTO.setEmail("teacher@example.com");
        teacherDTO.setPhone("13800138002");
        teacherDTO.setPassword("teacher123");
        teacherDTO.setNickname("教师昵称");

        User admin = new User();
        admin.setId(2);
        admin.setAccount("admin");
        admin.setOrganizationId(1);

        UserDetails adminDetails = new org.springframework.security.core.userdetails.User(
                admin.getAccount(),
                "password",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_SCH_ADMIN"))
        );

        // 配置Mock行为
        when(userMapper.selectByAccount(adminDetails.getUsername())).thenReturn(admin);
        when(userMapper.selectRoleByUserId(admin.getId())).thenReturn(RoleConstants.DB_ROLE_SCHOOL_ADMIN);
        when(organizationService.getById(admin.getOrganizationId())).thenReturn(testSchool);
        when(userMapper.selectByEmail(teacherDTO.getEmail())).thenReturn(null);
        when(userMapper.selectByPhone(teacherDTO.getPhone())).thenReturn(null);
        when(accountGenerator.generateTeacherAccount(any(Organization.class))).thenReturn("T12345678");
        when(passwordEncoder.encode(teacherDTO.getPassword())).thenReturn("encoded_password");
        doNothing().when(userMapper).insert(any(User.class));
        doNothing().when(roleService).assignRoleToUser(anyInt(), eq(RoleConstants.DB_ROLE_TEACHER));

        // 执行测试
        UserVO result = userService.createTeacher(teacherDTO, adminDetails);

        // 验证结果
        assertNotNull(result);
        assertEquals("T12345678", result.getAccount());
        assertEquals(teacherDTO.getNickname(), result.getNickname());
        assertEquals(teacherDTO.getEmail(), result.getEmail());
        assertEquals(teacherDTO.getPhone(), result.getPhone());
        assertEquals("active", result.getStatus());
        assertEquals(admin.getOrganizationId(), result.getOrganizationId());

        // 验证交互
        verify(userMapper).selectByAccount(adminDetails.getUsername());
        verify(userMapper).selectRoleByUserId(admin.getId());
        verify(organizationService).getById(admin.getOrganizationId());
        verify(userMapper).selectByEmail(teacherDTO.getEmail());
        verify(userMapper).selectByPhone(teacherDTO.getPhone());
        verify(accountGenerator).generateTeacherAccount(any(Organization.class));
        verify(passwordEncoder).encode(teacherDTO.getPassword());
        verify(userMapper).insert(any(User.class));
        verify(roleService).assignRoleToUser(anyInt(), eq(RoleConstants.DB_ROLE_TEACHER));
    }

    @Test
    void testCreateMentor_WithValidData_ShouldCreateMentor() {
        // 准备测试数据
        OrgMemberCreationDTO mentorDTO = new OrgMemberCreationDTO();
        mentorDTO.setEmail("mentor@example.com");
        mentorDTO.setPhone("13800138003");
        mentorDTO.setPassword("mentor123");
        mentorDTO.setNickname("导师昵称");

        User admin = new User();
        admin.setId(3);
        admin.setAccount("ent_admin");
        admin.setOrganizationId(2);

        UserDetails adminDetails = new org.springframework.security.core.userdetails.User(
                admin.getAccount(),
                "password",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_ENT_ADMIN"))
        );

        // 配置Mock行为
        when(userMapper.selectByAccount(adminDetails.getUsername())).thenReturn(admin);
        when(userMapper.selectRoleByUserId(admin.getId())).thenReturn(RoleConstants.DB_ROLE_ENTERPRISE_ADMIN);
        when(organizationService.getById(admin.getOrganizationId())).thenReturn(testEnterprise);
        when(userMapper.selectByEmail(mentorDTO.getEmail())).thenReturn(null);
        when(userMapper.selectByPhone(mentorDTO.getPhone())).thenReturn(null);
        when(accountGenerator.generateMentorAccount(any(Organization.class))).thenReturn("M12345678");
        when(passwordEncoder.encode(mentorDTO.getPassword())).thenReturn("encoded_password");
        doNothing().when(userMapper).insert(any(User.class));
        doNothing().when(roleService).assignRoleToUser(anyInt(), eq(RoleConstants.DB_ROLE_ENTERPRISE_MENTOR));

        // 执行测试
        UserVO result = userService.createMentor(mentorDTO, adminDetails);

        // 验证结果
        assertNotNull(result);
        assertEquals("M12345678", result.getAccount());
        assertEquals(mentorDTO.getNickname(), result.getNickname());
        assertEquals(mentorDTO.getEmail(), result.getEmail());
        assertEquals(mentorDTO.getPhone(), result.getPhone());
        assertEquals("active", result.getStatus());
        assertEquals(admin.getOrganizationId(), result.getOrganizationId());

        // 验证交互
        verify(userMapper).selectByAccount(adminDetails.getUsername());
        verify(userMapper).selectRoleByUserId(admin.getId());
        verify(organizationService).getById(admin.getOrganizationId());
        verify(userMapper).selectByEmail(mentorDTO.getEmail());
        verify(userMapper).selectByPhone(mentorDTO.getPhone());
        verify(accountGenerator).generateMentorAccount(any(Organization.class));
        verify(passwordEncoder).encode(mentorDTO.getPassword());
        verify(userMapper).insert(any(User.class));
        verify(roleService).assignRoleToUser(anyInt(), eq(RoleConstants.DB_ROLE_ENTERPRISE_MENTOR));
    }

    @Test
    void testGetCurrentUser_WithValidUserDetails_ShouldReturnUserVO() {
        // 配置Mock行为
        when(userMapper.selectByAccount(testUserDetails.getUsername())).thenReturn(testUser);
        when(userMapper.selectRoleByUserId(testUser.getId())).thenReturn(RoleConstants.DB_ROLE_STUDENT);
        when(organizationService.getById(testUser.getOrganizationId())).thenReturn(testSchool);
        when(fileService.getFullFileUrl(testUser.getAvatar())).thenReturn("http://example.com/avatars/default.jpg");

        // 执行测试
        UserVO result = userService.getCurrentUser(testUserDetails);

        // 验证结果
        assertNotNull(result);
        assertEquals(testUser.getId(), result.getId());
        assertEquals(testUser.getAccount(), result.getAccount());
        assertEquals(testUser.getNickname(), result.getNickname());
        assertEquals(testUser.getEmail(), result.getEmail());
        assertEquals(testUser.getPhone(), result.getPhone());
        assertEquals("http://example.com/avatars/default.jpg", result.getAvatar());
        assertEquals(testUser.getStatus(), result.getStatus());
        assertEquals(testUser.getOrganizationId(), result.getOrganizationId());
        assertEquals(testSchool.getOrganizationName(), result.getOrganizationName());
        assertEquals("ROLE_STUDENT", result.getRole());

        // 验证交互
        verify(userMapper).selectByAccount(testUserDetails.getUsername());
        verify(userMapper).selectRoleByUserId(testUser.getId());
        verify(organizationService).getById(testUser.getOrganizationId());
        verify(fileService).getFullFileUrl(testUser.getAvatar());
    }

    @Test
    void testGetUserRole_WithValidUserId_ShouldReturnRole() {
        // 配置Mock行为
        when(userMapper.selectRoleByUserId(testUser.getId())).thenReturn(RoleConstants.DB_ROLE_STUDENT);

        // 执行测试
        String result = userService.getUserRole(testUser.getId());

        // 验证结果
        assertEquals(RoleConstants.DB_ROLE_STUDENT, result);

        // 验证交互
        verify(userMapper).selectRoleByUserId(testUser.getId());
    }

    @Test
    void testRegisterStudent_WithExistingEmail_ShouldThrowException() {
        // 准备测试数据
        StudentRegistrationDTO registrationDto = new StudentRegistrationDTO();
        registrationDto.setEmail("existing@example.com");
        registrationDto.setPassword("password123");
        registrationDto.setOrganizationId(1);

        // 配置Mock行为
        when(userMapper.selectByEmail(registrationDto.getEmail())).thenReturn(testUser);

        // 执行测试并验证异常
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            userService.registerStudent(registrationDto);
        });

        assertEquals("该邮箱已被注册", exception.getMessage());

        // 验证交互
        verify(userMapper).selectByEmail(registrationDto.getEmail());
        verifyNoMoreInteractions(userMapper, organizationService, accountGenerator, passwordEncoder, userVerificationMapper, roleService);
    }
} 