package com.csu.unicorp.service.impl.community;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.csu.unicorp.common.constants.CacheConstants;
import com.csu.unicorp.entity.community.CommunityNotification;
import com.csu.unicorp.mapper.community.CommunityNotificationMapper;
import com.csu.unicorp.service.CacheService;
import com.csu.unicorp.service.CommunityNotificationService;
import com.csu.unicorp.vo.community.NotificationVO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 社区通知服务实现类
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CommunityNotificationServiceImpl extends ServiceImpl<CommunityNotificationMapper, CommunityNotification>
        implements CommunityNotificationService {

    private final CommunityNotificationMapper notificationMapper;
    private final CacheService cacheService;

    @Override
    public Page<NotificationVO> getUserNotifications(Long userId, Integer page, Integer size, String type, Boolean isRead) {
        // 对于分页数据，只缓存第一页且无过滤条件的情况
        if (page == 1 && type == null && isRead == null) {
            String cacheKey = CacheConstants.USER_NOTIFICATIONS_CACHE_KEY_PREFIX + userId + ":" + size;
            Page<NotificationVO> cachedPage = cacheService.get(cacheKey, Page.class);
            if (cachedPage != null) {
                log.debug("从缓存获取用户通知列表: {}", userId);
                return cachedPage;
            }
        }
        
        Page<CommunityNotification> pageParam = new Page<>(page, size);
        
        // 构建查询条件
        LambdaQueryWrapper<CommunityNotification> queryWrapper = new LambdaQueryWrapper<CommunityNotification>()
                .eq(CommunityNotification::getUserId, userId)
                .eq(type != null, CommunityNotification::getNotificationType, type)
                .eq(isRead != null, CommunityNotification::getIsRead, isRead)
                .orderByDesc(CommunityNotification::getCreatedAt);
        
        // 执行查询
        Page<CommunityNotification> notificationPage = page(pageParam, queryWrapper);
        
        // 转换为VO
        Page<NotificationVO> voPage = new Page<>(notificationPage.getCurrent(), notificationPage.getSize(), notificationPage.getTotal());
        voPage.setRecords(notificationPage.getRecords().stream()
                .map(this::convertToVO)
                .toList());
        
        // 缓存第一页且无过滤条件的结果
        if (page == 1 && type == null && isRead == null) {
            String cacheKey = CacheConstants.USER_NOTIFICATIONS_CACHE_KEY_PREFIX + userId + ":" + size;
            cacheService.set(cacheKey, voPage, CacheConstants.NOTIFICATION_CACHE_EXPIRE_TIME, TimeUnit.SECONDS);
        }
        
        return voPage;
    }

    @Override
    public NotificationVO getNotificationDetail(Long notificationId, Long userId) {
        // 尝试从缓存获取
        String cacheKey = CacheConstants.COMMENT_DETAIL_CACHE_KEY_PREFIX + notificationId;
        NotificationVO cachedNotification = cacheService.get(cacheKey, NotificationVO.class);
        if (cachedNotification != null && cachedNotification.getUserId().equals(userId)) {
            log.debug("从缓存获取通知详情: {}", notificationId);
            return cachedNotification;
        }
        
        CommunityNotification notification = getOne(new LambdaQueryWrapper<CommunityNotification>()
                .eq(CommunityNotification::getId, notificationId)
                .eq(CommunityNotification::getUserId, userId));
        
        if (notification == null) {
            return null;
        }
        
        NotificationVO notificationVO = convertToVO(notification);
        
        // 缓存通知详情
        cacheService.set(cacheKey, notificationVO, CacheConstants.NOTIFICATION_CACHE_EXPIRE_TIME, TimeUnit.SECONDS);
        
        return notificationVO;
    }

    @Override
    @Transactional
    public boolean markAsRead(Long notificationId) {
        boolean result = notificationMapper.markAsRead(notificationId) > 0;
        
        if (result) {
            // 清除相关缓存
            cacheService.delete(CacheConstants.COMMENT_DETAIL_CACHE_KEY_PREFIX + notificationId);
            
            // 获取通知对象，清除用户未读通知数缓存
            CommunityNotification notification = getById(notificationId);
            if (notification != null) {
                cacheService.delete(CacheConstants.UNREAD_NOTIFICATION_COUNT_CACHE_KEY_PREFIX + notification.getUserId());
                cacheService.delete(CacheConstants.USER_NOTIFICATIONS_CACHE_KEY_PREFIX + notification.getUserId() + ":*");
            }
        }
        
        return result;
    }

    @Override
    @Transactional
    public boolean markAsReadWithPermissionCheck(Long notificationId, Long userId) {
        CommunityNotification notification = getOne(new LambdaQueryWrapper<CommunityNotification>()
                .eq(CommunityNotification::getId, notificationId)
                .eq(CommunityNotification::getUserId, userId));
        
        if (notification == null) {
            return false;
        }
        
        return markAsRead(notificationId);
    }

    @Override
    @Transactional
    public void markAllAsRead(Long userId) {
        notificationMapper.markAllAsRead(userId);
        
        // 清除相关缓存
        cacheService.delete(CacheConstants.UNREAD_NOTIFICATION_COUNT_CACHE_KEY_PREFIX + userId);
        cacheService.delete(CacheConstants.USER_NOTIFICATIONS_CACHE_KEY_PREFIX + userId + ":*");
    }

    @Override
    @Transactional
    public boolean deleteNotification(Long notificationId, Long userId) {
        boolean result = remove(new LambdaQueryWrapper<CommunityNotification>()
                .eq(CommunityNotification::getId, notificationId)
                .eq(CommunityNotification::getUserId, userId));
        
        if (result) {
            // 清除相关缓存
            cacheService.delete(CacheConstants.COMMENT_DETAIL_CACHE_KEY_PREFIX + notificationId);
            cacheService.delete(CacheConstants.USER_NOTIFICATIONS_CACHE_KEY_PREFIX + userId + ":*");
            cacheService.delete(CacheConstants.UNREAD_NOTIFICATION_COUNT_CACHE_KEY_PREFIX + userId);
        }
        
        return result;
    }

    @Override
    @Transactional
    public void deleteAllNotifications(Long userId) {
        remove(new LambdaQueryWrapper<CommunityNotification>()
                .eq(CommunityNotification::getUserId, userId));
        
        // 清除相关缓存
        cacheService.delete(CacheConstants.USER_NOTIFICATIONS_CACHE_KEY_PREFIX + userId + ":*");
        cacheService.delete(CacheConstants.UNREAD_NOTIFICATION_COUNT_CACHE_KEY_PREFIX + userId);
    }

    @Override
    public Integer getUnreadCount(Long userId) {
        // 尝试从缓存获取
        String cacheKey = CacheConstants.UNREAD_NOTIFICATION_COUNT_CACHE_KEY_PREFIX + userId;
        Integer cachedCount = cacheService.get(cacheKey, Integer.class);
        if (cachedCount != null) {
            log.debug("从缓存获取未读通知数: {}", userId);
            return cachedCount;
        }
        
        Integer count = notificationMapper.countUnreadNotifications(userId);
        
        // 缓存未读通知数
        cacheService.set(cacheKey, count, CacheConstants.NOTIFICATION_CACHE_EXPIRE_TIME, TimeUnit.SECONDS);
        
        return count;
    }
    
    /**
     * 创建通知
     * @param userId 接收用户ID
     * @param content 通知内容
     * @param notificationType 通知类型
     * @param relatedId 相关内容ID
     * @return 通知ID
     */
    @Override
    @Transactional
    public Long createNotification(Long userId, String content, String notificationType, Long relatedId) {
        CommunityNotification notification = new CommunityNotification();
        notification.setUserId(userId);
        notification.setContent(content);
        notification.setNotificationType(notificationType);
        notification.setRelatedId(relatedId);
        notification.setIsRead(false);
        notification.setCreatedAt(LocalDateTime.now());
        
        save(notification);
        
        // 清除相关缓存
        cacheService.delete(CacheConstants.UNREAD_NOTIFICATION_COUNT_CACHE_KEY_PREFIX + userId);
        cacheService.delete(CacheConstants.USER_NOTIFICATIONS_CACHE_KEY_PREFIX + userId + ":*");
        
        return notification.getId();
    }
    
    /**
     * 批量创建通知
     * @param userIds 接收用户ID列表
     * @param content 通知内容
     * @param notificationType 通知类型
     * @param relatedId 相关内容ID
     */
    @Override
    @Transactional
    public void batchCreateNotification(List<Long> userIds, String content, String notificationType, Long relatedId) {
        if (userIds == null || userIds.isEmpty()) {
            return;
        }
        
        List<CommunityNotification> notifications = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        
        for (Long userId : userIds) {
            CommunityNotification notification = new CommunityNotification();
            notification.setUserId(userId);
            notification.setContent(content);
            notification.setNotificationType(notificationType);
            notification.setRelatedId(relatedId);
            notification.setIsRead(false);
            notification.setCreatedAt(now);
            notifications.add(notification);
        }
        
        saveBatch(notifications);
        
        // 清除相关缓存
        for (Long userId : userIds) {
            cacheService.delete(CacheConstants.UNREAD_NOTIFICATION_COUNT_CACHE_KEY_PREFIX + userId);
            cacheService.delete(CacheConstants.USER_NOTIFICATIONS_CACHE_KEY_PREFIX + userId + ":*");
        }
    }
    
    /**
     * 将实体转换为VO
     * @param notification 通知实体
     * @return 通知VO
     */
    private NotificationVO convertToVO(CommunityNotification notification) {
        if (notification == null) {
            return null;
        }
        
        NotificationVO vo = new NotificationVO();
        vo.setId(notification.getId());
        vo.setUserId(notification.getUserId());
        vo.setContent(notification.getContent());
        vo.setType(notification.getNotificationType());
        vo.setTargetId(notification.getRelatedId());
        vo.setIsRead(notification.getIsRead());
        vo.setCreateTime(notification.getCreatedAt());
        
        return vo;
    }
} 