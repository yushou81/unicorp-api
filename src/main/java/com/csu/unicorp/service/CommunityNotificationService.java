package com.csu.unicorp.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csu.unicorp.vo.community.NotificationVO;

import java.util.List;

/**
 * 社区通知服务接口
 */
public interface CommunityNotificationService {
    
    /**
     * 获取用户通知列表
     * @param userId 用户ID
     * @param page 页码
     * @param size 每页大小
     * @param type 通知类型
     * @param isRead 是否已读
     * @return 通知列表
     */
    Page<NotificationVO> getUserNotifications(Long userId, Integer page, Integer size, String type, Boolean isRead);
    
    /**
     * 获取通知详情
     * @param notificationId 通知ID
     * @param userId 用户ID
     * @return 通知详情
     */
    NotificationVO getNotificationDetail(Long notificationId, Long userId);
    
    /**
     * 标记通知为已读
     * @param notificationId 通知ID
     * @return 是否成功
     */
    boolean markAsRead(Long notificationId);
    
    /**
     * 带权限检查的标记通知为已读
     * @param notificationId 通知ID
     * @param userId 用户ID
     * @return 是否成功
     */
    boolean markAsReadWithPermissionCheck(Long notificationId, Long userId);
    
    /**
     * 标记所有通知为已读
     * @param userId 用户ID
     */
    void markAllAsRead(Long userId);
    
    /**
     * 删除通知
     * @param notificationId 通知ID
     * @param userId 用户ID
     * @return 是否成功
     */
    boolean deleteNotification(Long notificationId, Long userId);
    
    /**
     * 删除所有通知
     * @param userId 用户ID
     */
    void deleteAllNotifications(Long userId);
    
    /**
     * 获取未读通知数量
     * @param userId 用户ID
     * @return 未读通知数量
     */
    Integer getUnreadCount(Long userId);
    
    /**
     * 创建通知
     * @param userId 接收用户ID
     * @param content 通知内容
     * @param notificationType 通知类型
     * @param relatedId 相关内容ID
     * @return 通知ID
     */
    Long createNotification(Long userId, String content, String notificationType, Long relatedId);
    
    /**
     * 批量创建通知
     * @param userIds 接收用户ID列表
     * @param content 通知内容
     * @param notificationType 通知类型
     * @param relatedId 相关内容ID
     */
    void batchCreateNotification(List<Long> userIds, String content, String notificationType, Long relatedId);
} 