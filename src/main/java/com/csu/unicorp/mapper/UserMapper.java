package com.csu.unicorp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.csu.unicorp.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * User Mapper 接口
 */
@Mapper // 告诉Spring Boot这是一个Mapper接口，需要被扫描到
public interface UserMapper extends BaseMapper<User> {
    // 继承了BaseMapper后，自动拥有了所有基础的CRUD方法
    // 例如：insert, selectById, selectList, updateById, deleteById ...
    // 如果有复杂的、自定义的SQL查询，才需要在这里添加方法并在XML中实现
    
    /**
     * 根据用户名查询用户
     * @param username 用户名
     * @return 用户实体，不存在则返回null
     */
    @Select("SELECT * FROM user WHERE username = #{username} AND is_deleted = 0")
    User selectByUsername(@Param("username") String username);
}
