package com.csu.unicorp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.csu.unicorp.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

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
    List<String> selectRolesByUserId(@Param("userId") Integer userId);
    
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
}
