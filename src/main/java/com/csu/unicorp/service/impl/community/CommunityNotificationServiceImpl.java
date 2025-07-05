package com.csu.unicorp.service.impl.community;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.csu.unicorp.entity.community.CommunityNotification;
import com.csu.unicorp.mapper.community.CommunityNotificationMapper;
import com.csu.unicorp.service.CommunityNotificationService;
import com.csu.unicorp.vo.community.NotificationVO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 社区通知服务实现类
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CommunityNotificationServiceImpl extends ServiceImpl<CommunityNotificationMapper, CommunityNotification>
        implements CommunityNotificationService {

    private final CommunityNotificationMapper notificationMapper;

    @Override
    public Page<NotificationVO> getUserNotifications(Long userId, Integer page, Integer size, String type, Boolean isRead) {
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
        
        return voPage;
    }

    @Override
    public NotificationVO getNotificationDetail(Long notificationId, Long userId) {
        CommunityNotification notification = getOne(new LambdaQueryWrapper<CommunityNotification>()
                .eq(CommunityNotification::getId, notificationId)
                .eq(CommunityNotification::getUserId, userId));
        
        return notification != null ? convertToVO(notification) : null;
    }

    @Override
    @Transactional
    public boolean markAsRead(Long notificationId) {
        return notificationMapper.markAsRead(notificationId) > 0;
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
    }

    @Override
    @Transactional
    public boolean deleteNotification(Long notificationId, Long userId) {
        return remove(new LambdaQueryWrapper<CommunityNotification>()
                .eq(CommunityNotification::getId, notificationId)
                .eq(CommunityNotification::getUserId, userId));
    }

    @Override
    @Transactional
    public void deleteAllNotifications(Long userId) {
        remove(new LambdaQueryWrapper<CommunityNotification>()
                .eq(CommunityNotification::getUserId, userId));
    }

    @Override
    public Integer getUnreadCount(Long userId) {
        return notificationMapper.countUnreadNotifications(userId);
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