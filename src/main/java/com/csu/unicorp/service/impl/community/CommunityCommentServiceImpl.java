package com.csu.unicorp.service.impl.community;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.csu.unicorp.common.constants.CacheConstants;
import com.csu.unicorp.common.constants.RoleConstants;
import com.csu.unicorp.dto.community.CommentDTO;
import com.csu.unicorp.entity.User;
import com.csu.unicorp.entity.community.CommunityComment;
import com.csu.unicorp.mapper.community.CommunityCommentMapper;
import com.csu.unicorp.service.CacheService;
import com.csu.unicorp.service.CommunityCommentService;
import com.csu.unicorp.service.CommunityLikeService;
import com.csu.unicorp.service.CommunityNotificationService;
import com.csu.unicorp.service.FileService;
import com.csu.unicorp.service.UserService;
import com.csu.unicorp.vo.community.CommentVO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 社区评论Service实现类
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CommunityCommentServiceImpl extends ServiceImpl<CommunityCommentMapper, CommunityComment> 
        implements CommunityCommentService {

    private final CommunityCommentMapper commentMapper;
    private final CommunityLikeService likeService;
    private final UserService userService;
    private final FileService fileService;
    private final CommunityNotificationService notificationService;
    private final CacheService cacheService;

    @Override
    @Transactional
    public Long createComment(Long userId, CommentDTO commentDTO) {
        // 创建评论实体
        CommunityComment comment = CommunityComment.builder()
                .content(commentDTO.getContent())
                .userId(userId)
                .topicId(commentDTO.getTopicId())
                .parentId(commentDTO.getParentId())
                .likeCount(0)
                .status("NORMAL")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        // 保存评论
        save(comment);
        
        // 清除相关缓存
        if (comment.getParentId() != null) {
            // 如果是回复评论，清除父评论的回复列表缓存
            cacheService.deleteByPattern(CacheConstants.COMMENT_REPLIES_CACHE_KEY_PREFIX + comment.getParentId() + "*");
        } else {
            // 如果是评论话题，清除话题评论列表缓存
            cacheService.deleteByPattern(CacheConstants.TOPIC_COMMENTS_CACHE_KEY_PREFIX + comment.getTopicId() + "*");
        }
        
        // 清除用户评论列表缓存
        cacheService.deleteByPattern(CacheConstants.USER_COMMENTS_CACHE_KEY_PREFIX + userId + "*");
        
        // 发送通知
        User user = userService.getById(userId.intValue());
        String userName = user != null ? user.getNickname() : "用户" + userId;
        
        if (comment.getParentId() != null) {
            // 如果是回复评论，通知被回复的评论作者
            CommunityComment parentComment = getById(comment.getParentId());
            if (parentComment != null && !parentComment.getUserId().equals(userId)) {
                String content = userName + " 回复了你的评论：" + comment.getContent();
                notificationService.createNotification(parentComment.getUserId(), content, "COMMENT_REPLY", comment.getId());
            }
        } else {
            // 如果是评论话题，通知话题作者
            // 这里需要获取话题作者ID，假设有方法获取话题作者ID
            // 实际实现中，可能需要注入TopicService或者通过Mapper查询
            // 这里简化处理，假设topicId可能是话题ID或回答ID
            Long authorId = getContentAuthorId(comment.getTopicId());
            if (authorId != null && !authorId.equals(userId)) {
                String content = userName + " 评论了你的内容：" + comment.getContent();
                notificationService.createNotification(authorId, content, "COMMENT", comment.getId());
            }
        }
        
        // 返回评论ID
        return comment.getId();
    }

    @Override
    @Transactional
    public boolean deleteComment(Long userId, Long commentId) {
        // 检查评论是否存在
        CommunityComment comment = getById(commentId);
        if (comment == null) {
            return false;
        }
        
        // 检查是否有权限删除
        if (!checkCommentDeletePermission(userId, commentId)) {
            return false;
        }
        
        // 逻辑删除评论（更新状态为DELETED）
        comment.setStatus("DELETED");
        comment.setUpdatedAt(LocalDateTime.now());
        
        boolean result = updateById(comment);
        
        // 清除相关缓存
        if (result) {
            // 清除评论详情缓存
            cacheService.delete(CacheConstants.COMMENT_DETAIL_CACHE_KEY_PREFIX + commentId);
            
            if (comment.getParentId() != null) {
                // 如果是回复评论，清除父评论的回复列表缓存
                cacheService.deleteByPattern(CacheConstants.COMMENT_REPLIES_CACHE_KEY_PREFIX + comment.getParentId() + "*");
            } else {
                // 如果是评论话题，清除话题评论列表缓存
                cacheService.deleteByPattern(CacheConstants.TOPIC_COMMENTS_CACHE_KEY_PREFIX + comment.getTopicId() + "*");
            }
            
            // 清除用户评论列表缓存
            cacheService.deleteByPattern(CacheConstants.USER_COMMENTS_CACHE_KEY_PREFIX + comment.getUserId() + "*");
        }
        
        return result;
    }

    @Override
    public Page<CommentVO> getTopicComments(Long topicId, int page, int size, Long userId) {
        // 对于分页数据，只缓存第一页
        if (page == 1) {
            String cacheKey = CacheConstants.TOPIC_COMMENTS_CACHE_KEY_PREFIX + topicId + ":" + size;
            Page<CommentVO> cachedPage = cacheService.get(cacheKey, Page.class);
            if (cachedPage != null) {
                log.debug("从缓存获取话题评论列表: {}", topicId);
                // 更新用户交互状态
                if (userId != null && cachedPage.getRecords() != null) {
                    for (CommentVO comment : cachedPage.getRecords()) {
                        comment.setLiked(likeService.checkLike(userId, comment.getId(), "COMMENT"));
                        // 递归更新回复的点赞状态
                        if (comment.getReplies() != null) {
                            updateRepliesLikeStatus(comment.getReplies(), userId);
                        }
                    }
                }
                return cachedPage;
            }
        }
        
        // 创建分页对象
        Page<CommunityComment> pageParam = new Page<>(page, size);
        
        // 查询条件：属于指定话题且状态为正常的一级评论（没有父评论）
        LambdaQueryWrapper<CommunityComment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CommunityComment::getTopicId, topicId)
                   .isNull(CommunityComment::getParentId)
                   .eq(CommunityComment::getStatus, "NORMAL")
                   .orderByDesc(CommunityComment::getCreatedAt);
        
        // 执行分页查询
        Page<CommunityComment> commentPage = page(pageParam, queryWrapper);
        
        // 转换为VO并加载回复
        Page<CommentVO> voPage = new Page<>(commentPage.getCurrent(), commentPage.getSize(), commentPage.getTotal());
        List<CommentVO> voList = commentPage.getRecords().stream()
                .map(comment -> {
                    CommentVO vo = convertToVO(comment, userId);
                    // 获取评论的回复
                    List<CommentVO> replies = getCommentReplies(comment.getId(), userId);
                    vo.setReplies(replies);
                    return vo;
                })
                .collect(Collectors.toList());
        
        voPage.setRecords(voList);
        
        // 缓存第一页数据
        if (page == 1) {
            String cacheKey = CacheConstants.TOPIC_COMMENTS_CACHE_KEY_PREFIX + topicId + ":" + size;
            // 创建一个不包含用户交互状态的副本用于缓存
            Page<CommentVO> cachePage = new Page<>(voPage.getCurrent(), voPage.getSize(), voPage.getTotal());
            List<CommentVO> cacheList = voPage.getRecords().stream()
                    .map(vo -> {
                        CommentVO cacheVo = cloneCommentVO(vo);
                        cacheVo.setLiked(false);
                        if (cacheVo.getReplies() != null) {
                            cacheVo.setReplies(cacheVo.getReplies().stream()
                                    .map(reply -> {
                                        CommentVO cacheReply = cloneCommentVO(reply);
                                        cacheReply.setLiked(false);
                                        return cacheReply;
                                    })
                                    .collect(Collectors.toList()));
                        }
                        return cacheVo;
                    })
                    .collect(Collectors.toList());
            cachePage.setRecords(cacheList);
            cacheService.set(cacheKey, cachePage, CacheConstants.COMMENT_CACHE_EXPIRE_TIME, TimeUnit.SECONDS);
        }
        
        return voPage;
    }

    @Override
    public List<CommentVO> getCommentReplies(Long commentId, Long userId) {
        // 尝试从缓存获取
        String cacheKey = CacheConstants.COMMENT_REPLIES_CACHE_KEY_PREFIX + commentId;
        List<CommentVO> cachedReplies = cacheService.getList(cacheKey, CommentVO.class);
        if (cachedReplies != null && !cachedReplies.isEmpty()) {
            log.debug("从缓存获取评论回复列表: {}", commentId);
            // 更新用户交互状态
            if (userId != null) {
                updateRepliesLikeStatus(cachedReplies, userId);
            }
            return cachedReplies;
        }
        
        // 查询条件：父评论ID等于指定评论ID且状态为正常
        LambdaQueryWrapper<CommunityComment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CommunityComment::getParentId, commentId)
                   .eq(CommunityComment::getStatus, "NORMAL")
                   .orderByAsc(CommunityComment::getCreatedAt);
        
        // 执行查询
        List<CommunityComment> replyList = list(queryWrapper);
        
        // 转换为VO
        List<CommentVO> result = replyList.stream()
                .map(reply -> convertToVO(reply, userId))
                .collect(Collectors.toList());
        
        // 缓存结果（不包含用户交互状态）
        List<CommentVO> cacheList = replyList.stream()
                .map(reply -> {
                    CommentVO vo = convertToVO(reply, null);
                    vo.setLiked(false);
                    return vo;
                })
                .collect(Collectors.toList());
        cacheService.setList(cacheKey, cacheList, CacheConstants.COMMENT_CACHE_EXPIRE_TIME, TimeUnit.SECONDS);
        
        return result;
    }

    @Override
    public Page<CommentVO> getUserComments(Long userId, int page, int size) {
        // 对于分页数据，只缓存第一页
        if (page == 1) {
            String cacheKey = CacheConstants.USER_COMMENTS_CACHE_KEY_PREFIX + userId + ":" + size;
            Page<CommentVO> cachedPage = cacheService.get(cacheKey, Page.class);
            if (cachedPage != null) {
                log.debug("从缓存获取用户评论列表: {}", userId);
                return cachedPage;
            }
        }
        
        // 创建分页对象
        Page<CommunityComment> pageParam = new Page<>(page, size);
        
        // 查询条件：用户ID等于指定用户ID且状态为正常
        LambdaQueryWrapper<CommunityComment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CommunityComment::getUserId, userId)
                   .eq(CommunityComment::getStatus, "NORMAL")
                   .orderByDesc(CommunityComment::getCreatedAt);
        
        // 执行分页查询
        Page<CommunityComment> commentPage = page(pageParam, queryWrapper);
        
        // 转换为VO
        Page<CommentVO> voPage = new Page<>(commentPage.getCurrent(), commentPage.getSize(), commentPage.getTotal());
        List<CommentVO> voList = commentPage.getRecords().stream()
                .map(comment -> convertToVO(comment, userId))
                .collect(Collectors.toList());
        
        voPage.setRecords(voList);
        
        // 缓存第一页数据
        if (page == 1) {
            String cacheKey = CacheConstants.USER_COMMENTS_CACHE_KEY_PREFIX + userId + ":" + size;
            cacheService.set(cacheKey, voPage, CacheConstants.USER_CACHE_EXPIRE_TIME, TimeUnit.SECONDS);
        }
        
        return voPage;
    }

    @Override
    @Transactional
    public boolean likeComment(Long userId, Long commentId) {
        // 检查评论是否存在
        CommunityComment comment = getById(commentId);
        if (comment == null || !"NORMAL".equals(comment.getStatus())) {
            return false;
        }
        
        // 使用点赞服务添加点赞记录
        boolean liked = likeService.like(userId, commentId, "COMMENT");
        
        if (liked) {
            // 增加评论点赞数
            commentMapper.incrementLikeCount(commentId);
            
            // 清除评论详情缓存
            cacheService.delete(CacheConstants.COMMENT_DETAIL_CACHE_KEY_PREFIX + commentId);
            
            // 如果是回复，清除父评论的回复列表缓存
            if (comment.getParentId() != null) {
                cacheService.deleteByPattern(CacheConstants.COMMENT_REPLIES_CACHE_KEY_PREFIX + comment.getParentId() + "*");
            } else {
                // 如果是一级评论，清除话题评论列表缓存
                cacheService.deleteByPattern(CacheConstants.TOPIC_COMMENTS_CACHE_KEY_PREFIX + comment.getTopicId() + "*");
            }
        }
        
        return liked;
    }

    @Override
    @Transactional
    public boolean unlikeComment(Long userId, Long commentId) {
        // 检查评论是否存在
        CommunityComment comment = getById(commentId);
        if (comment == null) {
            return false;
        }
        
        // 使用点赞服务删除点赞记录
        likeService.unlike(userId, commentId, "COMMENT");
        
        // 减少评论点赞数
        commentMapper.decrementLikeCount(commentId);
        
        // 清除评论详情缓存
        cacheService.delete(CacheConstants.COMMENT_DETAIL_CACHE_KEY_PREFIX + commentId);
        
        // 如果是回复，清除父评论的回复列表缓存
        if (comment.getParentId() != null) {
            cacheService.deleteByPattern(CacheConstants.COMMENT_REPLIES_CACHE_KEY_PREFIX + comment.getParentId() + "*");
        } else {
            // 如果是一级评论，清除话题评论列表缓存
            cacheService.deleteByPattern(CacheConstants.TOPIC_COMMENTS_CACHE_KEY_PREFIX + comment.getTopicId() + "*");
        }
        
        return true;
    }

    @Override
    public boolean checkUserLikedComment(Long userId, Long commentId) {
        return likeService.checkLike(userId, commentId, "COMMENT");
    }

    @Override
    public boolean checkCommentDeletePermission(Long userId, Long commentId) {
        CommunityComment comment = getById(commentId);
        if (comment == null) {
            return false;
        }
        
        // 评论作者可以删除自己的评论
        if (comment.getUserId().equals(userId)) {
            return true;
        }
        
        // 检查用户是否是管理员
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) principal;
            // 系统管理员有所有权限
            if (userDetails.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_" + RoleConstants.ROLE_SYSTEM_ADMIN))) {
                return true;
            }
        }
        
        return false;
    }

    @Override
    public boolean checkCommentEditPermission(Long userId, Long commentId) {
        // 评论不支持编辑，只能删除后重新创建
        // 但为了接口一致性，保留此方法，调用删除权限检查
        return checkCommentDeletePermission(userId, commentId);
    }

    @Override
    public CommentVO getCommentDetail(Long commentId) {
        // 尝试从缓存获取
        String cacheKey = CacheConstants.COMMENT_DETAIL_CACHE_KEY_PREFIX + commentId;
        CommentVO cachedComment = cacheService.get(cacheKey, CommentVO.class);
        if (cachedComment != null) {
            log.debug("从缓存获取评论详情: {}", commentId);
            return cachedComment;
        }
        
        CommunityComment comment = getById(commentId);
        if (comment == null || !"NORMAL".equals(comment.getStatus())) {
            return null;
        }
        
        CommentVO commentVO = convertToVO(comment, null);
        
        // 缓存结果
        cacheService.set(cacheKey, commentVO, CacheConstants.COMMENT_CACHE_EXPIRE_TIME, TimeUnit.SECONDS);
        
        return commentVO;
    }

    @Override
    public Page<CommentVO> getAnswerComments(Long answerId, int page, int size) {
        // 对于分页数据，只缓存第一页
        if (page == 1) {
            String cacheKey = CacheConstants.ANSWER_COMMENTS_CACHE_KEY_PREFIX + answerId + ":" + size;
            Page<CommentVO> cachedPage = cacheService.get(cacheKey, Page.class);
            if (cachedPage != null) {
                log.debug("从缓存获取回答评论列表: {}", answerId);
                return cachedPage;
            }
        }
        
        // 创建分页对象
        Page<CommunityComment> pageParam = new Page<>(page, size);
        
        // 查询条件：话题ID等于指定回答ID且状态为正常
        // 注意：这里使用topicId字段存储answerId，因为评论可能关联到话题或回答
        LambdaQueryWrapper<CommunityComment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CommunityComment::getTopicId, answerId)
                   .eq(CommunityComment::getStatus, "NORMAL")
                   .orderByDesc(CommunityComment::getCreatedAt);
        
        // 执行分页查询
        Page<CommunityComment> commentPage = page(pageParam, queryWrapper);
        
        // 转换为VO
        Page<CommentVO> voPage = new Page<>(commentPage.getCurrent(), commentPage.getSize(), commentPage.getTotal());
        List<CommentVO> voList = commentPage.getRecords().stream()
                .map(comment -> convertToVO(comment, null))
                .collect(Collectors.toList());
        
        voPage.setRecords(voList);
        
        // 缓存第一页数据
        if (page == 1) {
            String cacheKey = CacheConstants.ANSWER_COMMENTS_CACHE_KEY_PREFIX + answerId + ":" + size;
            cacheService.set(cacheKey, voPage, CacheConstants.COMMENT_CACHE_EXPIRE_TIME, TimeUnit.SECONDS);
        }
        
        return voPage;
    }
    
    /**
     * 将评论实体转换为VO
     * @param comment 评论实体
     * @param userId 当前用户ID（可选）
     * @return 评论VO
     */
    private CommentVO convertToVO(CommunityComment comment, Long userId) {
        if (comment == null) {
            return null;
        }
        
        // 获取评论用户信息
        User user = userService.getById(comment.getUserId().intValue());
        String userName = "用户" + comment.getUserId();
        String userAvatar = "/avatars/default/avatar.jpg";
        
        if (user != null) {
            userName = user.getNickname();
            if (user.getAvatar() != null && !user.getAvatar().isEmpty()) {
                userAvatar = fileService.getFullFileUrl(user.getAvatar());
            }
        }
        
        // 构建评论VO
        CommentVO vo = CommentVO.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .userId(comment.getUserId())
                .userName(userName)
                .userAvatar(userAvatar)
                .topicId(comment.getTopicId())
                .parentId(comment.getParentId())
                .likeCount(comment.getLikeCount())
                .status(comment.getStatus())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .build();
        
        // 如果有父评论，获取父评论用户名
        if (comment.getParentId() != null) {
            CommunityComment parentComment = getById(comment.getParentId());
            if (parentComment != null) {
                User parentUser = userService.getById(parentComment.getUserId().intValue());
                if (parentUser != null) {
                    vo.setParentUserName(parentUser.getNickname());
                } else {
                    vo.setParentUserName("用户" + parentComment.getUserId());
                }
            }
        }
        
        // 设置当前用户是否已点赞
        if (userId != null) {
            vo.setLiked(checkUserLikedComment(userId, comment.getId()));
        } else {
            vo.setLiked(false);
        }
        
        return vo;
    }

    /**
     * 获取内容作者ID
     * @param contentId 内容ID
     * @return 作者ID
     */
    private Long getContentAuthorId(Long contentId) {
        if (contentId == null) {
            return null;
        }
        
        try {
            // 使用mapper查询内容作者ID
            return commentMapper.selectContentAuthorId(contentId);
        } catch (Exception e) {
            log.error("获取内容作者ID失败", e);
            return null;
        }
    }
    
    /**
     * 更新回复列表中的点赞状态
     * @param replies 回复列表
     * @param userId 用户ID
     */
    private void updateRepliesLikeStatus(List<CommentVO> replies, Long userId) {
        if (replies == null || userId == null) {
            return;
        }
        
        for (CommentVO reply : replies) {
            reply.setLiked(likeService.checkLike(userId, reply.getId(), "COMMENT"));
        }
    }
    
    /**
     * 克隆评论VO对象（浅拷贝）
     * @param source 源对象
     * @return 克隆后的对象
     */
    private CommentVO cloneCommentVO(CommentVO source) {
        if (source == null) {
            return null;
        }
        
        return CommentVO.builder()
                .id(source.getId())
                .content(source.getContent())
                .userId(source.getUserId())
                .userName(source.getUserName())
                .userAvatar(source.getUserAvatar())
                .topicId(source.getTopicId())
                .parentId(source.getParentId())
                .parentUserName(source.getParentUserName())
                .likeCount(source.getLikeCount())
                .status(source.getStatus())
                .createdAt(source.getCreatedAt())
                .updatedAt(source.getUpdatedAt())
                .replies(source.getReplies())
                .build();
    }
} 