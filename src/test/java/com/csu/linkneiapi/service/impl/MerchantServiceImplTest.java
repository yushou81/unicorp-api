package com.csu.linkneiapi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.csu.linkneiapi.common.exception.BusinessException;
import com.csu.linkneiapi.dto.MerchantRegisterDTO;
import com.csu.linkneiapi.entity.Merchant;
import com.csu.linkneiapi.entity.User;
import com.csu.linkneiapi.mapper.MerchantMapper;
import com.csu.linkneiapi.mapper.UserMapper;
import com.csu.linkneiapi.service.MerchantService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class MerchantServiceImplTest {
    @Autowired
    private MerchantService merchantService;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private MerchantMapper merchantMapper;

    @Test
    public void testRegisterMerchantSuccess() {
        // 1. 新建一个普通用户
        User user = new User();
        String username = "testuser" + System.currentTimeMillis();
        user.setUsername(username);
        user.setRole("USER");
        user.setPassword("123456");
        userMapper.insert(user);

        // 2. 注册商户
        MerchantRegisterDTO dto = new MerchantRegisterDTO();
        dto.setName("测试商户");
        merchantService.registerMerchant(user.getId(), dto);

        // 3. 校验商户和用户角色
        Merchant merchant = merchantMapper.selectOne(new QueryWrapper<Merchant>().eq("user_id", user.getId()));
        assertNotNull(merchant);
        User updatedUser = userMapper.selectById(user.getId());
        assertEquals("MERCHANT", updatedUser.getRole());
    }

    @Test
    public void testRegisterMerchantAlreadyMerchant() {
        // 1. 新建一个商户用户
        User user = new User();
        String username = "merchantuser" + System.currentTimeMillis();
        user.setUsername(username);
        user.setRole("MERCHANT");
        user.setPassword("123456");
        userMapper.insert(user);

        MerchantRegisterDTO dto = new MerchantRegisterDTO();
        dto.setName("已是商户");

        // 2. 再次注册应抛异常
        assertThrows(BusinessException.class, () -> {
            merchantService.registerMerchant(user.getId(), dto);
        });
    }
}
