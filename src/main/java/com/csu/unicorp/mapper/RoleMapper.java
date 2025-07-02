package com.csu.unicorp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.csu.unicorp.entity.user.Role;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 角色Mapper接口
 */
@Mapper
public interface RoleMapper extends BaseMapper<Role> {
    
    /**
     * 根据角色名称查询角色
     * 
     * @param roleName 角色名称
     * @return 角色实体
     */
    @Select("SELECT * FROM roles WHERE role_name = #{roleName}")
    Role selectByRoleName(@Param("roleName") String roleName);
} 