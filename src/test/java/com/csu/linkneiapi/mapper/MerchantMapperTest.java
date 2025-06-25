package com.csu.linkneiapi.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csu.linkneiapi.entity.Merchant;
import com.csu.linkneiapi.vo.MerchantSummaryVO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import com.csu.linkneiapi.config.TestDataSourceConfig;
//import com.csu.linkneiapi.config.TestInitializer;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * MerchantMapper 单元测试类
 */
@SpringBootTest
@ActiveProfiles("test") // 使用测试专用配置文件
@Transactional // 测试完成后回滚事务，不影响数据库
@Import(TestDataSourceConfig.class) // 导入测试数据源配置
public class MerchantMapperTest {

    @Autowired
    private MerchantMapper merchantMapper;
    
    /**
     * 测试根据ID查询商户
     */
    @Test
    public void testSelectById() {
        // 查询测试商户
        Merchant merchant = merchantMapper.selectById(1L);
        
        // 断言
        assertNotNull(merchant);
        assertEquals("测试商户1", merchant.getName());
        assertEquals("内江市东兴区测试地址1", merchant.getAddress());
        assertEquals(new BigDecimal("4.5"), merchant.getAverageRating());
        assertEquals("OPEN", merchant.getStatus());
    }
    
    /**
     * 测试查询商户列表
     */
    @Test
    public void testSelectList() {
        // 构建查询条件：查询未删除的营业中商户
        LambdaQueryWrapper<Merchant> queryWrapper = Wrappers.<Merchant>lambdaQuery()
                .eq(Merchant::getIsDeleted, 0)
                .eq(Merchant::getStatus, "OPEN");
        
        // 执行查询
        List<Merchant> merchantList = merchantMapper.selectList(queryWrapper);
        
        // 断言
        assertNotNull(merchantList);
        assertFalse(merchantList.isEmpty());
        assertEquals(2, merchantList.size()); // 预期有2个未删除的营业中商户
    }
    
    /**
     * 测试插入商户
     */
    @Test
    public void testInsert() {
        // 创建新商户对象
        Merchant newMerchant = new Merchant();
        newMerchant.setUserId(1L);
        newMerchant.setName("测试插入商户");
        newMerchant.setAddress("内江市市中区测试插入地址");
        newMerchant.setPhone("13900139000");
        newMerchant.setDescription("这是一个测试插入的商户");
        newMerchant.setLogoUrl("https://example.com/logo_insert.jpg");
        newMerchant.setBusinessHours("08:30-21:30");
        newMerchant.setAverageRating(new BigDecimal("4.0"));
        newMerchant.setStatus("PENDING_REVIEW");
        newMerchant.setIsDeleted(0);
        newMerchant.setCreateTime(LocalDateTime.now());
        newMerchant.setUpdateTime(LocalDateTime.now());
        
        // 执行插入
        int result = merchantMapper.insert(newMerchant);
        
        // 断言
        assertEquals(1, result);
        assertNotNull(newMerchant.getId()); // 自增ID已回填
        
        // 验证插入结果
        Merchant insertedMerchant = merchantMapper.selectById(newMerchant.getId());
        assertNotNull(insertedMerchant);
        assertEquals("测试插入商户", insertedMerchant.getName());
        assertEquals("内江市市中区测试插入地址", insertedMerchant.getAddress());
    }
    
    /**
     * 测试更新商户
     */
    @Test
    public void testUpdate() {
        // 先查询现有商户
        Merchant merchant = merchantMapper.selectById(1L);
        assertNotNull(merchant);
        
        // 修改商户信息
        String newName = "更新后的商户名称";
        BigDecimal newRating = new BigDecimal("4.8");
        merchant.setName(newName);
        merchant.setAverageRating(newRating);
        merchant.setUpdateTime(LocalDateTime.now());
        
        // 执行更新
        int result = merchantMapper.updateById(merchant);
        
        // 断言
        assertEquals(1, result);
        
        // 验证更新结果
        Merchant updatedMerchant = merchantMapper.selectById(1L);
        assertNotNull(updatedMerchant);
        assertEquals(newName, updatedMerchant.getName());
        assertEquals(newRating, updatedMerchant.getAverageRating());
    }
    
    /**
     * 测试逻辑删除商户
     */
    @Test
    public void testLogicDelete() {
        // 执行逻辑删除
        int result = merchantMapper.deleteById(1L);
        
        // 断言
        assertEquals(1, result);
        
        // 验证是否已逻辑删除（查询不到或isDeleted为1）
        Merchant deletedMerchant = merchantMapper.selectById(1L);
        assertNull(deletedMerchant); // 由于配置了逻辑删除，默认查询不到已删除记录
        
        // 直接查询数据库验证逻辑删除标记
        LambdaQueryWrapper<Merchant> queryWrapper = Wrappers.<Merchant>lambdaQuery()
                .eq(Merchant::getId, 1L);
        Merchant merchant = merchantMapper.selectOne(queryWrapper);
        assertNotNull(merchant);
        assertEquals(1, merchant.getIsDeleted()); // 逻辑删除标记为1
    }
    
    /**
     * 测试分页查询商户列表摘要
     */
    @Test
    public void testSelectMerchantSummaryPage() {
        // 创建分页参数
        Page<MerchantSummaryVO> page = new Page<>(1, 10);
        
        // 执行分页查询
        IPage<MerchantSummaryVO> result = merchantMapper.selectMerchantSummaryPage(page);
        
        // 断言
        assertNotNull(result);
        assertNotNull(result.getRecords());
        assertFalse(result.getRecords().isEmpty());
        assertEquals(2, result.getTotal()); // 预期有2个营业中的商户
        
        // 验证第一条记录
        MerchantSummaryVO firstMerchant = result.getRecords().get(0);
        assertNotNull(firstMerchant);
        assertEquals(1L, firstMerchant.getId().longValue()); // 评分最高的应该排在前面
        assertEquals("测试商户1", firstMerchant.getName());
        assertEquals(new BigDecimal("4.5"), firstMerchant.getAverageRating());
    }
    
    /**
     * 测试根据ID查询商户详情（包含产品信息）
     */
    @Test
    public void testSelectMerchantDetailById() {
        // 执行查询
        Merchant merchant = merchantMapper.selectMerchantDetailById(1L);
        
        // 断言
        assertNotNull(merchant);
        assertEquals("测试商户1", merchant.getName());
        
        // 验证关联的产品列表
        assertNotNull(merchant.getProducts());
        assertFalse(merchant.getProducts().isEmpty());
        
        // 注意：由于使用了INNER JOIN并且过滤了产品状态，可能只返回部分产品
        // 如果测试数据中商户1有2个在售产品，则应该有2个产品
        assertTrue(merchant.getProducts().size() >= 2);
    }
} 