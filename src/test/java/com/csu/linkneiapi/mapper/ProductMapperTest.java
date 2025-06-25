package com.csu.linkneiapi.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.csu.linkneiapi.entity.Product;
import com.csu.linkneiapi.vo.ProductSummaryVO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ProductMapper 单元测试类
 */
@SpringBootTest
@ActiveProfiles("test") // 使用测试专用配置文件
@Transactional // 测试完成后回滚事务，不影响数据库
public class ProductMapperTest {

    @Autowired
    private ProductMapper productMapper;
    
    /**
     * 测试根据ID查询产品
     */
    @Test
    public void testSelectById() {
        // 查询测试产品
        Product product = productMapper.selectById(1L);
        
        // 断言
        assertNotNull(product);
        assertEquals("测试产品1", product.getName());
        assertEquals(new BigDecimal("29.90"), product.getPrice());
        assertEquals(1L, product.getMerchantId().longValue());
        assertEquals("ON_SALE", product.getStatus());
    }
    
    /**
     * 测试查询产品列表
     */
    @Test
    public void testSelectList() {
        // 构建查询条件：查询未删除的在售产品
        LambdaQueryWrapper<Product> queryWrapper = Wrappers.<Product>lambdaQuery()
                .eq(Product::getIsDeleted, 0)
                .eq(Product::getStatus, "ON_SALE");
        
        // 执行查询
        List<Product> productList = productMapper.selectList(queryWrapper);
        
        // 断言
        assertNotNull(productList);
        assertFalse(productList.isEmpty());
        assertEquals(4, productList.size()); // 预期有4个未删除的在售产品
    }
    
    /**
     * 测试插入产品
     */
    @Test
    public void testInsert() {
        // 创建新产品对象
        Product newProduct = new Product();
        newProduct.setMerchantId(1L);
        newProduct.setCategoryId(1L);
        newProduct.setName("测试插入产品");
        newProduct.setDescription("这是一个测试插入的产品");
        newProduct.setPrice(new BigDecimal("39.99"));
        newProduct.setImageUrl("https://example.com/product_insert.jpg");
        newProduct.setStock(50);
        newProduct.setSalesCount(0);
        newProduct.setStatus("ON_SALE");
        newProduct.setIsDeleted(0);
        newProduct.setCreateTime(LocalDateTime.now());
        newProduct.setUpdateTime(LocalDateTime.now());
        
        // 执行插入
        int result = productMapper.insert(newProduct);
        
        // 断言
        assertEquals(1, result);
        assertNotNull(newProduct.getId()); // 自增ID已回填
        
        // 验证插入结果
        Product insertedProduct = productMapper.selectById(newProduct.getId());
        assertNotNull(insertedProduct);
        assertEquals("测试插入产品", insertedProduct.getName());
        assertEquals(new BigDecimal("39.99"), insertedProduct.getPrice());
    }
    
    /**
     * 测试更新产品
     */
    @Test
    public void testUpdate() {
        // 先查询现有产品
        Product product = productMapper.selectById(1L);
        assertNotNull(product);
        
        // 修改产品信息
        String newName = "更新后的产品名称";
        BigDecimal newPrice = new BigDecimal("35.99");
        product.setName(newName);
        product.setPrice(newPrice);
        product.setUpdateTime(LocalDateTime.now());
        
        // 执行更新
        int result = productMapper.updateById(product);
        
        // 断言
        assertEquals(1, result);
        
        // 验证更新结果
        Product updatedProduct = productMapper.selectById(1L);
        assertNotNull(updatedProduct);
        assertEquals(newName, updatedProduct.getName());
        assertEquals(newPrice, updatedProduct.getPrice());
    }
    
    /**
     * 测试逻辑删除产品
     */
    @Test
    public void testLogicDelete() {
        // 执行逻辑删除
        int result = productMapper.deleteById(1L);
        
        // 断言
        assertEquals(1, result);
        
        // 验证是否已逻辑删除（查询不到或isDeleted为1）
        Product deletedProduct = productMapper.selectById(1L);
        assertNull(deletedProduct); // 由于配置了逻辑删除，默认查询不到已删除记录
        
        // 直接查询数据库验证逻辑删除标记
        LambdaQueryWrapper<Product> queryWrapper = Wrappers.<Product>lambdaQuery()
                .eq(Product::getId, 1L);
        Product product = productMapper.selectOne(queryWrapper);
        assertNotNull(product);
        assertEquals(1, product.getIsDeleted()); // 逻辑删除标记为1
    }
    
    /**
     * 测试根据商户ID查询产品列表
     */
    @Test
    public void testSelectProductsByMerchantId() {
        // 执行查询
        List<ProductSummaryVO> products = productMapper.selectProductsByMerchantId(1L);
        
        // 断言
        assertNotNull(products);
        assertFalse(products.isEmpty());
        // 商户1有2个在售产品
        assertEquals(2, products.size());
        
        // 验证第一条记录
        ProductSummaryVO firstProduct = products.get(0);
        assertNotNull(firstProduct);
        assertEquals("测试产品1", firstProduct.getName());
        assertEquals(new BigDecimal("29.90"), firstProduct.getPrice());
    }
} 