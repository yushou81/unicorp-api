package com.csu.unicorp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csu.unicorp.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * 用户Mapper接口
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
    // 继承了BaseMapper后，自动拥有了所有基础的CRUD方法
    // 例如：insert, selectById, selectList, updateById, deleteById ...
    // 如果有复杂的、自定义的SQL查询，才需要在这里添加方法并在XML中实现
    
    /**
     * 根据账号查询用户
     * 
     * @param account 账号
     * @return 用户实体
     */
    @Select("SELECT * FROM users WHERE account = #{account} AND is_deleted = 0")
    User selectByAccount(@Param("account") String account);
    
    /**
     * 根据邮箱查询用户
     * 
     * @param email 电子邮箱
     * @return 用户实体
     */
    @Select("SELECT * FROM users WHERE email = #{email} AND is_deleted = 0")
    User selectByEmail(@Param("email") String email);
    
    /**
     * 根据手机号查询用户
     * 
     * @param phone 手机号
     * @return 用户实体
     */
    @Select("SELECT * FROM users WHERE phone = #{phone} AND is_deleted = 0")
    User selectByPhone(@Param("phone") String phone);

    /**
     * 根据用户ID查询角色列表
     * 
     * @param userId 用户ID
     * @return 角色名称列表
     */
    @Select("SELECT r.role_name FROM roles r " +
            "INNER JOIN user_roles ur ON r.id = ur.role_id " +
            "WHERE ur.user_id = #{userId}")
    String selectRoleByUserId(@Param("userId") Integer userId);
    
    /**
     * 根据前缀查询最大序号
     * 提取账号中的数字部分作为序号
     * 例如：csu2500001 -> 返回 1
     * 
     * @param prefix 账号前缀，如"csu25"
     * @return 当前最大序号
     */
    @Select("SELECT MAX(CAST(SUBSTRING(account, LENGTH(#{prefix}) + 1) AS UNSIGNED)) " +
            "FROM users WHERE account LIKE CONCAT(#{prefix}, '%') AND is_deleted = 0")
    Integer selectMaxSequenceByPrefix(@Param("prefix") String prefix);
    
    /**
     * 根据组织ID查询教师列表
     * 
     * @param organizationId 组织ID
     * @param page 页码
     * @param size 每页大小
     * @return 教师列表
     */
    @Select("SELECT u.* FROM users u " +
            "INNER JOIN user_roles ur ON u.id = ur.user_id " +
            "INNER JOIN roles r ON ur.role_id = r.id " +
            "WHERE u.organization_id = #{organizationId} " +
            "AND r.role_name = 'TEACHER' " +
            "AND u.is_deleted = 0")
    IPage<User> selectTeachersByOrganizationId(@Param("organizationId") Integer organizationId, 
                                              @Param("page") Page<User> page);
    
    /**
     * 根据组织ID查询企业导师列表
     * 
     * @param organizationId 组织ID
     * @param page 页码
     * @param size 每页大小
     * @return 企业导师列表
     */
    @Select("SELECT u.* FROM users u " +
            "INNER JOIN user_roles ur ON u.id = ur.user_id " +
            "INNER JOIN roles r ON ur.role_id = r.id " +
            "WHERE u.organization_id = #{organizationId} " +
            "AND r.role_name = 'EN_TEACHER' " +
            "AND u.is_deleted = 0")
    IPage<User> selectMentorsByOrganizationId(@Param("organizationId") Integer organizationId, 
                                             @Param("page") Page<User> page);
    
    /**
     * 根据用户名(账号)查询用户
     * 
     * @param username 用户名(账号)
     * @return 用户实体
     */
    @Select("SELECT * FROM users WHERE account = #{username} AND is_deleted = 0")
    User findByUsername(@Param("username") String username);
    
    /**
     * 检查用户是否拥有特定角色
     * 
     * @param userId 用户ID
     * @param roleName 角色名称
     * @return 是否拥有该角色
     */
    @Select("SELECT COUNT(*) > 0 FROM user_roles ur " +
            "JOIN roles r ON ur.role_id = r.id " +
            "WHERE ur.user_id = #{userId} AND r.role_name = #{roleName}")
    boolean hasRole(@Param("userId") Integer userId, @Param("roleName") String roleName);
    
    /**
     * 获取用户的实名认证信息和学生档案
     * 
     * @param userId 用户ID
     * @return 用户实名信息和学生档案
     */
    @Select("SELECT uv.real_name, sp.major " +
            "FROM users u " +
            "LEFT JOIN user_verifications uv ON u.id = uv.user_id " +
            "LEFT JOIN student_profiles sp ON u.id = sp.user_id " +
            "WHERE u.id = #{userId}")
    Map<String, Object> getUserVerificationAndProfile(@Param("userId") Integer userId);

    /**
     * 根据组织ID查询所有用户
     *
     * @param organizationId 组织ID
     * @param page 分页参数
     * @return 用户分页列表
     */
    @Select("SELECT * FROM users WHERE organization_id = #{organizationId}")
    Page<User> selectUsersByOrganizationId(@Param("organizationId") Integer organizationId, Page<User> page);
    
    /**
     * 根据组织ID和角色ID查询用户
     *
     * @param organizationId 组织ID
     * @param roleId 角色ID
     * @param page 分页参数
     * @return 用户分页列表
     */
    @Select("SELECT u.* FROM users u JOIN user_roles ur ON u.id = ur.user_id " +
            "WHERE u.organization_id = #{organizationId} AND ur.role_id = #{roleId}")
    Page<User> selectUsersByOrganizationIdAndRoleId(@Param("organizationId") Integer organizationId, 
                                                   @Param("roleId") Integer roleId, 
                                                   Page<User> page);
    
    /**
     * 根据角色ID查询用户（系统管理员使用）
     *
     * @param roleId 角色ID
     * @param page 分页参数
     * @return 用户分页列表
     */
    @Select("SELECT u.* FROM users u " +
            "JOIN user_roles ur ON u.id = ur.user_id " +
            "WHERE ur.role_id = #{roleId} AND u.is_deleted = 0")
    IPage<User> selectUsersByRoleId(@Param("roleId") Integer roleId, @Param("page") Page<User> page);
    
    /**
     * 查询所有用户，排除特定角色的用户（系统管理员使用）
     *
     * @param excludeRoleId 要排除的角色ID
     * @param page 分页参数
     * @return 用户分页列表
     */
    @Select("SELECT u.* FROM users u " +
            "WHERE u.id NOT IN (" +
            "  SELECT ur.user_id FROM user_roles ur WHERE ur.role_id = #{excludeRoleId}" +
            ") AND u.is_deleted = 0")
    IPage<User> selectUsersExcludeRole(@Param("excludeRoleId") Integer excludeRoleId, @Param("page") Page<User> page);
}
