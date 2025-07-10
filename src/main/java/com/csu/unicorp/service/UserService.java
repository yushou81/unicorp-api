package com.csu.unicorp.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.csu.unicorp.dto.EnterpriseRegistrationDTO;
import com.csu.unicorp.dto.LoginCredentialsDTO;
import com.csu.unicorp.dto.OrgMemberCreationDTO;
import com.csu.unicorp.dto.OrgMemberUpdateDTO;
import com.csu.unicorp.dto.PasswordUpdateDTO;
import com.csu.unicorp.dto.RefreshTokenDTO;
import com.csu.unicorp.dto.StudentRegistrationDTO;
import com.csu.unicorp.dto.UserProfileUpdateDTO;
import com.csu.unicorp.dto.UserUpdateDTO;
import com.csu.unicorp.entity.User;
import com.csu.unicorp.vo.TokenVO;
import com.csu.unicorp.vo.UserVO;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 用户服务接口
 */
public interface UserService {
    
    /**
     * 用户登录
     * 
     * @param loginDto 登录凭证
     * @return JWT令牌
     */
    TokenVO login(LoginCredentialsDTO loginDto);
    
    /**
     * 用户登出
     * @param token JWT令牌
     * @param userDetails 用户详情
     */
    void logout(String token, UserDetails userDetails);
    
    /**
     * 刷新令牌
     * @param refreshTokenDTO 刷新令牌DTO
     * @return 新的令牌信息
     */
    TokenVO refreshToken(RefreshTokenDTO refreshTokenDTO);
    
    /**
     * 学生注册
     * 
     * @param registrationDto 学生注册信息
     * @return 注册成功的用户信息
     */
    UserVO registerStudent(StudentRegistrationDTO registrationDto);
    
    /**
     * 学生注册
     * 
     * @param registrationDto 学生注册信息
     * @param checkVerification 是否检查验证码
     * @return 注册成功的用户信息
     */
    UserVO registerStudent(StudentRegistrationDTO registrationDto, boolean checkVerification);
    
    /**
     * 企业注册
     * 
     * @param registrationDto 企业注册信息
     * @param logo 企业logo图片
     * @param businessLicense 营业执照文件
     * @return 注册成功的用户信息
     */
    UserVO registerEnterprise(EnterpriseRegistrationDTO registrationDto, MultipartFile logo, MultipartFile businessLicense);
    
    /**
     * 企业注册（带验证码检查）
     * 
     * @param registrationDto 企业注册信息
     * @param logo 企业logo图片
     * @param businessLicense 营业执照文件
     * @param checkVerification 是否检查验证码
     * @return 注册成功的用户信息
     */
    UserVO registerEnterprise(EnterpriseRegistrationDTO registrationDto, MultipartFile logo, MultipartFile businessLicense, boolean checkVerification);
    
    /**
     * 获取当前登录用户信息
     * 
     * @param userDetails 当前认证用户
     * @return 当前用户信息
     */
    UserVO getCurrentUser(UserDetails userDetails);
    
    /**
     * 根据ID查询用户
     * 
     * @param id 用户ID
     * @return 用户实体
     */
    User getById(Integer id);
    
    /**
     * 根据账号查询用户
     * 
     * @param account 账号
     * @return 用户实体
     */
    User getByAccount(String account);
    
    /**
     * 根据邮箱查询用户
     * 
     * @param email 电子邮箱
     * @return 用户实体
     */
    User getByEmail(String email);
    
    /**
     * 根据手机号查询用户
     * 
     * @param phone 手机号
     * @return 用户实体
     */
    User getByPhone(String phone);
    
    /**
     * 获取用户角色列表
     * 
     * @param userId 用户ID
     * @return 角色名称列表
     */
    String getUserRole(Integer userId);
    
    /**
     * 创建教师账号
     * 
     * @param teacherDTO 教师信息
     * @param userDetails 当前认证用户（学校管理员）
     * @return 创建成功的教师信息
     */
    UserVO createTeacher(OrgMemberCreationDTO teacherDTO, UserDetails userDetails);
    
    /**
     * 创建企业导师账号
     * 
     * @param mentorDTO 企业导师信息
     * @param userDetails 当前认证用户（企业管理员）
     * @return 创建成功的企业导师信息
     */
    UserVO createMentor(OrgMemberCreationDTO mentorDTO, UserDetails userDetails);
    
    /**
     * 获取学校教师列表
     *
     * @param page 页码
     * @param size 每页大小
     * @param userDetails 当前认证用户（学校管理员）
     * @return 教师列表（分页）
     */
    IPage<UserVO> getTeachers(int page, int size, UserDetails userDetails);
    
    /**
     * 更新教师信息
     *
     * @param id 教师ID
     * @param updateDTO 更新信息
     * @param userDetails 当前认证用户（学校管理员）
     * @return 更新后的教师信息
     */
    UserVO updateTeacher(Integer id, OrgMemberUpdateDTO updateDTO, UserDetails userDetails);
    
    /**
     * 禁用教师账号
     *
     * @param id 教师ID
     * @param userDetails 当前认证用户（学校管理员）
     */
    void disableTeacher(Integer id, UserDetails userDetails);
    
    /**
     * 获取企业导师列表
     *
     * @param page 页码
     * @param size 每页大小
     * @param userDetails 当前认证用户（企业管理员）
     * @return 导师列表（分页）
     */
    IPage<UserVO> getMentors(int page, int size, UserDetails userDetails);
    
    /**
     * 更新导师信息
     *
     * @param id 导师ID
     * @param updateDTO 更新信息
     * @param userDetails 当前认证用户（企业管理员）
     * @return 更新后的导师信息
     */
    UserVO updateMentor(Integer id, OrgMemberUpdateDTO updateDTO, UserDetails userDetails);
    
    /**
     * 禁用导师账号
     *
     * @param id 导师ID
     * @param userDetails 当前认证用户（企业管理员）
     */
    void disableMentor(Integer id, UserDetails userDetails);
    
    /**
     * 更新用户个人信息
     * 
     * @param profileUpdateDTO 个人信息更新DTO
     * @param userDetails 当前认证用户
     * @return 更新后的用户信息
     */
    UserVO updateUserProfile(UserProfileUpdateDTO profileUpdateDTO, UserDetails userDetails);
    
    /**
     * 修改用户密码
     * 
     * @param passwordUpdateDTO 密码更新DTO
     * @param userDetails 当前认证用户
     */
    void updatePassword(PasswordUpdateDTO passwordUpdateDTO, UserDetails userDetails);
    
    /**
     * 根据角色获取用户列表（系统管理员使用）
     * 
     * @param page 页码
     * @param size 每页大小
     * @param role 角色名称（可选）
     * @return 用户列表（分页）
     */
    IPage<UserVO> getUsersByRole(int page, int size, String role);
    
    /**
     * 更新用户状态（系统管理员使用）
     * 
     * @param id 用户ID
     * @param status 新状态
     * @return 更新后的用户信息
     */
    UserVO updateUserStatus(Integer id, String status);
    
    /**
     * 更新用户基本信息（系统管理员使用）
     * 
     * @param id 用户ID
     * @param userUpdateDTO 用户更新信息
     * @return 更新后的用户信息
     */
    UserVO updateUserByAdmin(Integer id, UserUpdateDTO userUpdateDTO);
    
    /**
     * 更新用户头像
     * 
     * @param file 头像文件
     * @param userDetails 当前认证用户
     * @return 更新后的用户信息
     */
    UserVO updateAvatar(MultipartFile file, UserDetails userDetails);
    
    /**
     * 为新注册用户分配默认头像
     * 
     * @return 默认头像的相对路径
     */
    String assignDefaultAvatar();
    
    /**
     * 通过电话号码或邮箱搜索用户
     * 
     * @param keyword 搜索关键词（电话号码或邮箱）
     * @return 用户信息
     */
    UserVO searchUserByPhoneOrEmail(String keyword);
    
    /**
     * 根据GitHub用户ID查询用户
     * 
     * @param githubId GitHub用户ID
     * @return 用户实体
     */
    User getByGithubId(String githubId);
    
    /**
     * 保存用户
     * 
     * @param user 用户实体
     * @return 保存后的用户实体
     */
    User saveUser(User user);
} 