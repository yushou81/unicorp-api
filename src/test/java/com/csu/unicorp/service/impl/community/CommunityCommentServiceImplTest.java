package com.csu.unicorp.service.impl.community;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csu.unicorp.common.constants.CacheConstants;
import com.csu.unicorp.dto.community.CommentDTO;
import com.csu.unicorp.service.CommunityCommentService;
import com.csu.unicorp.vo.community.CommentVO;

/**
 * CommunityCommentService测试类
 */
public class CommunityCommentServiceImplTest extends BaseCacheServiceTest {

    @Autowired
    private CommunityCommentService commentService;
    
    /**
     * 测试获取评论详情的缓存功能
     */
    @Test
    public void testGetCommentDetailCache() {
        // 假设数据库中存在ID为1的评论
        Long commentId = 1L;
        
        // 第一次调用，应该从数据库加载并缓存
        CommentVO comment1 = commentService.getCommentDetail(commentId);
        assertNotNull(comment1);
        
        // 验证缓存是否存在
        String cacheKey = CacheConstants.COMMENT_DETAIL_CACHE_KEY_PREFIX + commentId;
        assertTrue(redisTemplate.hasKey(cacheKey));
        
        // 第二次调用，应该从缓存加载
        CommentVO comment2 = commentService.getCommentDetail(commentId);
        assertNotNull(comment2);
        
        // 验证两次结果相同
        assertEquals(comment1.getId(), comment2.getId());
        assertEquals(comment1.getContent(), comment2.getContent());
        assertEquals(comment1.getUserId(), comment2.getUserId());
    }
    
    /**
     * 测试获取话题评论列表的缓存功能
     */
    @Test
    public void testGetTopicCommentsCache() {
        // 假设数据库中存在ID为1的话题
        Long topicId = 1L;
        int size = 10;
        
        // 第一次调用，应该从数据库加载并缓存
        Page<CommentVO> page1 = commentService.getTopicComments(topicId, 1, size, null);
        assertNotNull(page1);
        
        // 验证缓存是否存在
        String cacheKey = CacheConstants.TOPIC_COMMENTS_CACHE_KEY_PREFIX + topicId + ":" + size;
        assertTrue(redisTemplate.hasKey(cacheKey));
        
        // 第二次调用，应该从缓存加载
        Page<CommentVO> page2 = commentService.getTopicComments(topicId, 1, size, null);
        assertNotNull(page2);
        
        // 验证两次结果相同
        assertEquals(page1.getTotal(), page2.getTotal());
        assertEquals(page1.getRecords().size(), page2.getRecords().size());
        if (page1.getRecords().size() > 0) {
            assertEquals(page1.getRecords().get(0).getId(), page2.getRecords().get(0).getId());
            assertEquals(page1.getRecords().get(0).getContent(), page2.getRecords().get(0).getContent());
        }
    }
    
    /**
     * 测试获取评论回复列表的缓存功能
     */
    @Test
    public void testGetCommentRepliesCache() {
        // 假设数据库中存在ID为1的评论，且有回复
        Long commentId = 1L;
        
        // 第一次调用，应该从数据库加载并缓存
        List<CommentVO> replies1 = commentService.getCommentReplies(commentId, null);
        assertNotNull(replies1);
        
        // 验证缓存是否存在
        String cacheKey = CacheConstants.COMMENT_REPLIES_CACHE_KEY_PREFIX + commentId;
        assertTrue(redisTemplate.hasKey(cacheKey));
        
        // 第二次调用，应该从缓存加载
        List<CommentVO> replies2 = commentService.getCommentReplies(commentId, null);
        assertNotNull(replies2);
        
        // 验证两次结果相同
        assertEquals(replies1.size(), replies2.size());
        if (replies1.size() > 0) {
            assertEquals(replies1.get(0).getId(), replies2.get(0).getId());
            assertEquals(replies1.get(0).getContent(), replies2.get(0).getContent());
        }
    }
    
    /**
     * 测试创建评论后缓存是否被清除
     */
    @Test
    public void testCreateCommentClearCache() {
        // 假设数据库中存在ID为1的话题
        Long topicId = 1L;
        int size = 10;
        
        // 先获取话题评论列表，使其被缓存
        commentService.getTopicComments(topicId, 1, size, null);
        
        // 验证缓存是否存在
        String cacheKey = CacheConstants.TOPIC_COMMENTS_CACHE_KEY_PREFIX + topicId + ":" + size;
        assertTrue(redisTemplate.hasKey(cacheKey));
        
        // 创建新评论
        CommentDTO commentDTO = new CommentDTO();
        commentDTO.setContent("测试评论内容");
        commentDTO.setTopicId(topicId);
        
        Long commentId = commentService.createComment(1L, commentDTO);
        assertNotNull(commentId);
        
        // 验证缓存是否被清除
        assertTrue(!redisTemplate.hasKey(cacheKey));
        
        // 删除创建的测试评论
        commentService.deleteComment(1L, commentId);
    }
    
    /**
     * 测试点赞评论后缓存是否被清除
     */
    @Test
    public void testLikeCommentClearCache() {
        // 假设数据库中存在ID为1的评论
        Long commentId = 1L;
        Long userId = 1L;
        
        // 先获取评论详情，使其被缓存
        CommentVO comment = commentService.getCommentDetail(commentId);
        assertNotNull(comment);
        
        // 验证缓存是否存在
        String cacheKey = CacheConstants.COMMENT_DETAIL_CACHE_KEY_PREFIX + commentId;
        assertTrue(redisTemplate.hasKey(cacheKey));
        
        // 点赞评论
        boolean liked = commentService.likeComment(userId, commentId);
        
        // 验证缓存是否被清除
        assertTrue(!redisTemplate.hasKey(cacheKey));
        
        // 如果点赞成功，取消点赞
        if (liked) {
            commentService.unlikeComment(userId, commentId);
        }
    }
} 