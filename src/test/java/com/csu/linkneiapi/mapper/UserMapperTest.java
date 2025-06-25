package com.csu.linkneiapi.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.csu.linkneiapi.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import com.csu.linkneiapi.config.TestDataSourceConfig;
//import com.csu.linkneiapi.config.TestInitializer;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * UserMapper 单元测试类
 */
@SpringBootTest
@ActiveProfiles("test") // 使用测试专用配置文件
@Transactional // 测试完成后回滚事务，不影响数据库
@Import(TestDataSourceConfig.class) // 导入测试数据源配置
public class UserMapperTest {

    @Autowired
    private UserMapper userMapper;
    
    /**
     * 测试根据ID查询用户
     */
    @Test
    public void testSelectById() {
        // 查询测试用户
        User user = userMapper.selectById(1L);
        
        // 断言
        assertNotNull(user);
        assertEquals("testuser1", user.getUsername());
        assertEquals("测试用户1", user.getNickname());
        assertEquals("USER", user.getRole());
        assertEquals(0, user.getStatus());
    }
    
    /**
     * 测试查询用户列表
     */
    @Test
    public void testSelectList() {
        // 构建查询条件：查询未删除的用户
        LambdaQueryWrapper<User> queryWrapper = Wrappers.<User>lambdaQuery()
                .eq(User::getIsDeleted, 0);
        
        // 执行查询
        List<User> userList = userMapper.selectList(queryWrapper);
        
        // 断言
        assertNotNull(userList);
        assertFalse(userList.isEmpty());
        assertEquals(3, userList.size()); // 预期有3个未删除的用户
    }
    
    /**
     * 测试插入用户
     */
    @Test
    public void testInsert() {
        // 创建新用户对象
        User newUser = new User();
        newUser.setUsername("testInsertUser");
        newUser.setPassword("$2a$10$1sDFTz6rpeF3z4RdYFJZ0.xVMiY0U2PJNCRsHMG.Jeh0MQ1t.jdKe");
        newUser.setNickname("测试插入用户");
        newUser.setRole("USER");
        newUser.setStatus(0);
        newUser.setIsDeleted(0);
        newUser.setCreateTime(LocalDateTime.now());
        newUser.setUpdateTime(LocalDateTime.now());
        
        // 执行插入
        int result = userMapper.insert(newUser);
        
        // 断言
        assertEquals(1, result);
        assertNotNull(newUser.getId()); // 自增ID已回填
        
        // 验证插入结果
        User insertedUser = userMapper.selectById(newUser.getId());
        assertNotNull(insertedUser);
        assertEquals("testInsertUser", insertedUser.getUsername());
        assertEquals("测试插入用户", insertedUser.getNickname());
    }
    
    /**
     * 测试更新用户
     */
    @Test
    public void testUpdate() {
        // 先查询现有用户
        User user = userMapper.selectById(1L);
        assertNotNull(user);
        
        // 修改用户信息
        String newNickname = "更新后的昵称";
        user.setNickname(newNickname);
        user.setUpdateTime(LocalDateTime.now());
        
        // 执行更新
        int result = userMapper.updateById(user);
        
        // 断言
        assertEquals(1, result);
        
        // 验证更新结果
        User updatedUser = userMapper.selectById(1L);
        assertNotNull(updatedUser);
        assertEquals(newNickname, updatedUser.getNickname());
    }
    
    /**
     * 测试逻辑删除用户
     */
    @Test
    public void testLogicDelete() {
        // 执行逻辑删除
        int result = userMapper.deleteById(1L);
        
        // 断言
        assertEquals(1, result);
        
        // 验证是否已逻辑删除（查询不到或isDeleted为1）
        User deletedUser = userMapper.selectById(1L);
        assertNull(deletedUser); // 由于配置了逻辑删除，默认查询不到已删除记录
        
        // 直接查询数据库验证逻辑删除标记
        LambdaQueryWrapper<User> queryWrapper = Wrappers.<User>lambdaQuery()
                .eq(User::getId, 1L);
        // 注意：此处没有设置逻辑删除条件，以查询出所有记录
        User user = userMapper.selectOne(queryWrapper);
        assertNotNull(user);
        assertEquals(1, user.getIsDeleted()); // 逻辑删除标记为1
    }
} 