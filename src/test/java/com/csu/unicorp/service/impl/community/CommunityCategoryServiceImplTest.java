package com.csu.unicorp.service.impl.community;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csu.unicorp.common.constants.CacheConstants;
import com.csu.unicorp.dto.community.CategoryDTO;
import com.csu.unicorp.service.CommunityCategoryService;
import com.csu.unicorp.vo.community.CategoryVO;

/**
 * CommunityCategoryService测试类
 */
public class CommunityCategoryServiceImplTest extends BaseCacheServiceTest {

    @Autowired
    private CommunityCategoryService categoryService;
    
    /**
     * 测试获取板块树形结构的缓存功能
     */
    @Test
    public void testGetCategoryTreeCache() {
        // 第一次调用，应该从数据库加载并缓存
        List<CategoryVO> tree1 = categoryService.getCategoryTree();
        assertNotNull(tree1);
        
        // 验证缓存是否存在
        String cacheKey = CacheConstants.CATEGORY_TREE_CACHE_KEY;
        assertTrue(redisTemplate.hasKey(cacheKey));
        
        // 第二次调用，应该从缓存加载
        List<CategoryVO> tree2 = categoryService.getCategoryTree();
        assertNotNull(tree2);
        
        // 验证两次结果相同
        assertEquals(tree1.size(), tree2.size());
        if (tree1.size() > 0) {
            assertEquals(tree1.get(0).getId(), tree2.get(0).getId());
            assertEquals(tree1.get(0).getName(), tree2.get(0).getName());
        }
    }
    
    /**
     * 测试获取板块详情的缓存功能
     */
    @Test
    public void testGetCategoryDetailCache() {
        // 假设数据库中存在ID为1的板块
        Long categoryId = 1L;
        
        // 第一次调用，应该从数据库加载并缓存
        CategoryVO category1 = categoryService.getCategoryDetail(categoryId);
        assertNotNull(category1);
        
        // 验证缓存是否存在
        String cacheKey = CacheConstants.CATEGORY_DETAIL_CACHE_KEY_PREFIX + categoryId;
        assertTrue(redisTemplate.hasKey(cacheKey));
        
        // 第二次调用，应该从缓存加载
        CategoryVO category2 = categoryService.getCategoryDetail(categoryId);
        assertNotNull(category2);
        
        // 验证两次结果相同
        assertEquals(category1.getId(), category2.getId());
        assertEquals(category1.getName(), category2.getName());
        assertEquals(category1.getDescription(), category2.getDescription());
    }
    
    /**
     * 测试获取用户可见板块列表的缓存功能
     */
    @Test
    public void testGetUserVisibleCategoriesCache() {
        Long userId = 1L;
        
        // 第一次调用，应该从数据库加载并缓存
        List<CategoryVO> categories1 = categoryService.getUserVisibleCategories(userId);
        assertNotNull(categories1);
        
        // 验证缓存是否存在
        String cacheKey = CacheConstants.USER_VISIBLE_CATEGORIES_CACHE_KEY_PREFIX + userId;
        assertTrue(redisTemplate.hasKey(cacheKey));
        
        // 第二次调用，应该从缓存加载
        List<CategoryVO> categories2 = categoryService.getUserVisibleCategories(userId);
        assertNotNull(categories2);
        
        // 验证两次结果相同
        assertEquals(categories1.size(), categories2.size());
        if (categories1.size() > 0) {
            assertEquals(categories1.get(0).getId(), categories2.get(0).getId());
            assertEquals(categories1.get(0).getName(), categories2.get(0).getName());
        }
    }
    
    /**
     * 测试创建板块后缓存是否被清除
     */
    @Test
    public void testCreateCategoryClearCache() {
        // 先获取板块树，使其被缓存
        categoryService.getCategoryTree();
        
        // 验证缓存是否存在
        String cacheKey = CacheConstants.CATEGORY_TREE_CACHE_KEY;
        assertTrue(redisTemplate.hasKey(cacheKey));
        
        // 创建新板块
        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setName("测试板块");
        categoryDTO.setDescription("测试板块描述");
        categoryDTO.setIcon("test-icon.png");
        categoryDTO.setSortOrder(100);
        
        Long categoryId = categoryService.createCategory(categoryDTO);
        assertNotNull(categoryId);
        
        // 验证缓存是否被清除
        assertTrue(!redisTemplate.hasKey(cacheKey));
        
        // 删除创建的测试板块
        categoryService.deleteCategory(categoryId);
    }
    
    /**
     * 测试更新板块后缓存是否被清除
     */
    @Test
    public void testUpdateCategoryClearCache() {
        // 假设数据库中存在ID为1的板块
        Long categoryId = 1L;
        
        // 先获取板块详情，使其被缓存
        CategoryVO category = categoryService.getCategoryDetail(categoryId);
        assertNotNull(category);
        
        // 验证缓存是否存在
        String detailCacheKey = CacheConstants.CATEGORY_DETAIL_CACHE_KEY_PREFIX + categoryId;
        String treeCacheKey = CacheConstants.CATEGORY_TREE_CACHE_KEY;
        
        // 再获取板块树，使其被缓存
        categoryService.getCategoryTree();
        
        assertTrue(redisTemplate.hasKey(detailCacheKey));
        assertTrue(redisTemplate.hasKey(treeCacheKey));
        
        // 更新板块
        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setName(category.getName() + " (已更新)");
        categoryDTO.setDescription(category.getDescription());
        categoryDTO.setIcon(category.getIcon());
        categoryDTO.setSortOrder(category.getSortOrder());
        categoryDTO.setParentId(category.getParentId());
        
        boolean updated = categoryService.updateCategory(categoryId, categoryDTO);
        assertTrue(updated);
        
        // 验证缓存是否被清除
        assertTrue(!redisTemplate.hasKey(detailCacheKey));
        assertTrue(!redisTemplate.hasKey(treeCacheKey));
        
        // 恢复原来的名称
        categoryDTO.setName(category.getName());
        categoryService.updateCategory(categoryId, categoryDTO);
    }
} 