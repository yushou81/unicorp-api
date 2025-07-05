package com.csu.unicorp.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.csu.unicorp.dto.community.CommentDTO;
import com.csu.unicorp.entity.community.CommunityComment;
import com.csu.unicorp.vo.community.CommentVO;

/**
 * 社区评论Service接口
 */
public interface CommunityCommentService extends IService<CommunityComment> {
    
    /**
     * 创建评论
     * @param userId 用户ID
     * @param commentDTO 评论DTO
     * @return 评论ID
     */
    Long createComment(Long userId, CommentDTO commentDTO);
    
    /**
     * 删除评论
     * @param userId 用户ID
     * @param commentId 评论ID
     * @return 是否成功
     */
    boolean deleteComment(Long userId, Long commentId);
    
    /**
     * 获取话题评论列表（分页）
     * @param topicId 话题ID
     * @param page 页码
     * @param size 每页大小
     * @param userId 当前用户ID（可选）
     * @return 评论列表
     */
    Page<CommentVO> getTopicComments(Long topicId, int page, int size, Long userId);
    
    /**
     * 获取评论回复列表
     * @param commentId 评论ID
     * @param userId 当前用户ID（可选）
     * @return 回复列表
     */
    List<CommentVO> getCommentReplies(Long commentId, Long userId);
    
    /**
     * 获取用户评论列表（分页）
     * @param userId 用户ID
     * @param page 页码
     * @param size 每页大小
     * @return 评论列表
     */
    Page<CommentVO> getUserComments(Long userId, int page, int size);
    
    /**
     * 点赞评论
     * @param userId 用户ID
     * @param commentId 评论ID
     * @return 是否成功
     */
    boolean likeComment(Long userId, Long commentId);
    
    /**
     * 取消点赞评论
     * @param userId 用户ID
     * @param commentId 评论ID
     * @return 是否成功
     */
    boolean unlikeComment(Long userId, Long commentId);
    
    /**
     * 检查用户是否已点赞评论
     * @param userId 用户ID
     * @param commentId 评论ID
     * @return 是否已点赞
     */
    boolean checkUserLikedComment(Long userId, Long commentId);
    
    /**
     * 检查用户是否有权限删除评论
     * @param userId 用户ID
     * @param commentId 评论ID
     * @return 是否有权限
     */
    boolean checkCommentDeletePermission(Long userId, Long commentId);
    
    /**
     * 检查用户是否有权限编辑评论
     * @param userId 用户ID
     * @param commentId 评论ID
     * @return 是否有权限
     */
    boolean checkCommentEditPermission(Long userId, Long commentId);
    
    /**
     * 获取评论详情
     * @param commentId 评论ID
     * @return 评论详情
     */
    CommentVO getCommentDetail(Long commentId);
    
    /**
     * 获取回答的评论列表
     * @param answerId 回答ID
     * @param page 页码
     * @param size 每页大小
     * @return 评论列表
     */
    Page<CommentVO> getAnswerComments(Long answerId, int page, int size);
} 