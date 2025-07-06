package com.csu.unicorp.service.impl.community;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csu.unicorp.common.constants.CacheConstants;
import com.csu.unicorp.dto.community.QuestionDTO;
import com.csu.unicorp.service.CommunityQuestionService;
import com.csu.unicorp.vo.community.QuestionVO;

/**
 * CommunityQuestionService测试类
 */
public class CommunityQuestionServiceImplTest extends BaseCacheServiceTest {

    @Autowired
    private CommunityQuestionService questionService;
    
    /**
     * 测试获取问题详情的缓存功能
     */
    @Test
    public void testGetQuestionDetailCache() {
        // 假设数据库中存在ID为1的问题
        Long questionId = 1L;
        
        // 第一次调用，应该从数据库加载并缓存
        QuestionVO question1 = questionService.getQuestionDetail(questionId, null);
        assertNotNull(question1);
        
        // 验证缓存是否存在
        String cacheKey = CacheConstants.QUESTION_DETAIL_CACHE_KEY_PREFIX + questionId;
        assertTrue(redisTemplate.hasKey(cacheKey));
        
        // 第二次调用，应该从缓存加载
        QuestionVO question2 = questionService.getQuestionDetail(questionId, null);
        assertNotNull(question2);
        
        // 验证两次结果相同
        assertEquals(question1.getId(), question2.getId());
        assertEquals(question1.getTitle(), question2.getTitle());
        assertEquals(question1.getContent(), question2.getContent());
    }
    
    /**
     * 测试获取热门问题的缓存功能
     */
    @Test
    public void testGetHotQuestionsCache() {
        // 第一次调用，应该从数据库加载并缓存
        Page<QuestionVO> page1 = questionService.getHotQuestions(1, 10, null);
        assertNotNull(page1);
        
        // 验证缓存是否存在
        String cacheKey = CacheConstants.HOT_QUESTIONS_CACHE_KEY + ":10";
        assertTrue(redisTemplate.hasKey(cacheKey));
        
        // 第二次调用，应该从缓存加载
        Page<QuestionVO> page2 = questionService.getHotQuestions(1, 10, null);
        assertNotNull(page2);
        
        // 验证两次结果相同
        assertEquals(page1.getTotal(), page2.getTotal());
        assertEquals(page1.getRecords().size(), page2.getRecords().size());
        if (page1.getRecords().size() > 0) {
            assertEquals(page1.getRecords().get(0).getId(), page2.getRecords().get(0).getId());
        }
    }
    
    /**
     * 测试创建问题后缓存是否被清除
     */
    @Test
    public void testCreateQuestionClearCache() {
        // 先获取热门问题和最新问题，使其被缓存
        questionService.getHotQuestions(1, 10, null);
        questionService.getLatestQuestions(1, 10, null);
        
        // 验证缓存是否存在
        String hotCacheKey = CacheConstants.HOT_QUESTIONS_CACHE_KEY + ":10";
        String latestCacheKey = CacheConstants.LATEST_QUESTIONS_CACHE_KEY + ":10";
        assertTrue(redisTemplate.hasKey(hotCacheKey));
        assertTrue(redisTemplate.hasKey(latestCacheKey));
        
        // 创建新问题
        QuestionDTO questionDTO = new QuestionDTO();
        questionDTO.setTitle("测试问题标题");
        questionDTO.setContent("测试问题内容");
        questionDTO.setCategoryId(1L);
        
        Long questionId = questionService.createQuestion(1L, questionDTO);
        assertNotNull(questionId);
        
        // 验证最新问题缓存是否被清除
        assertTrue(!redisTemplate.hasKey(latestCacheKey));
        
        // 删除创建的测试问题
        questionService.deleteQuestion(1L, questionId);
    }
    
    /**
     * 测试更新问题后缓存是否被清除
     */
    @Test
    public void testUpdateQuestionClearCache() {
        // 假设数据库中存在ID为1的问题
        Long questionId = 1L;
        
        // 先获取问题详情，使其被缓存
        QuestionVO question = questionService.getQuestionDetail(questionId, null);
        assertNotNull(question);
        
        // 验证缓存是否存在
        String cacheKey = CacheConstants.QUESTION_DETAIL_CACHE_KEY_PREFIX + questionId;
        assertTrue(redisTemplate.hasKey(cacheKey));
        
        // 更新问题
        QuestionDTO questionDTO = new QuestionDTO();
        questionDTO.setTitle(question.getTitle() + " (已更新)");
        questionDTO.setContent(question.getContent());
        questionDTO.setCategoryId(question.getCategoryId());
        
        boolean updated = questionService.updateQuestion(question.getUserId(), questionId, questionDTO);
        assertTrue(updated);
        
        // 验证缓存是否被清除
        assertTrue(!redisTemplate.hasKey(cacheKey));
    }
} 