package com.csu.linkneiapi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.csu.linkneiapi.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * User Mapper 接口
 */
@Mapper // 告诉Spring Boot这是一个Mapper接口，需要被扫描到
public interface UserMapper extends BaseMapper<User> {
    // 继承了BaseMapper后，自动拥有了所有基础的CRUD方法
    // 例如：insert, selectById, selectList, updateById, deleteById ...
    // 如果有复杂的、自定义的SQL查询，才需要在这里添加方法并在XML中实现
}
