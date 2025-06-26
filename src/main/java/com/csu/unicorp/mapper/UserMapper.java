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
     * 根据用户ID查询角色列表
     * 
     * @param userId 用户ID
     * @return 角色名称列表
     */
    @Select("SELECT r.role_name FROM roles r " +
            "INNER JOIN user_roles ur ON r.id = ur.role_id " +
            "WHERE ur.user_id = #{userId}")
    List<String> selectRolesByUserId(@Param("userId") Integer userId);
}
