package com.csu.unicorp.service;

import com.csu.unicorp.dto.EnterpriseRegistrationDTO;
import com.csu.unicorp.dto.LoginCredentialsDTO;
import com.csu.unicorp.dto.OrgMemberCreationDTO;
import com.csu.unicorp.dto.StudentRegistrationDTO;
import com.csu.unicorp.entity.User;
import com.csu.unicorp.vo.TokenVO;
import com.csu.unicorp.vo.UserVO;
import org.springframework.security.core.userdetails.UserDetails;

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
     * 学生注册
     * 
     * @param registrationDto 学生注册信息
     * @return 注册成功的用户信息
     */
    UserVO registerStudent(StudentRegistrationDTO registrationDto);
    
    /**
     * 企业注册
     * 
     * @param registrationDto 企业注册信息
     * @return 注册成功的用户信息
     */
    UserVO registerEnterprise(EnterpriseRegistrationDTO registrationDto);
    
    /**
     * 获取当前登录用户信息
     * 
     * @param userDetails 当前认证用户
     * @return 用户信息
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
    List<String> getUserRoles(Integer userId);
    
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
} 