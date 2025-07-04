package com.csu.unicorp.mapper.community;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csu.unicorp.entity.community.CommunityNotification;

/**
 * 社区通知Mapper接口
 */
public interface CommunityNotificationMapper extends BaseMapper<CommunityNotification> {
    
    /**
     * 获取用户未读通知数量
     * @param userId 用户ID
     * @return 未读通知数量
     */
    @Select("SELECT COUNT(*) FROM community_notification WHERE user_id = #{userId} AND is_read = 0")
    int countUnreadNotifications(@Param("userId") Long userId);
    
    /**
     * 获取用户通知列表
     * @param page 分页参数
     * @param userId 用户ID
     * @return 通知列表
     */
    @Select("SELECT * FROM community_notification WHERE user_id = #{userId} ORDER BY created_at DESC")
    Page<CommunityNotification> selectNotificationsByUserId(Page<CommunityNotification> page, @Param("userId") Long userId);
    
    /**
     * 标记用户所有通知为已读
     * @param userId 用户ID
     * @return 影响行数
     */
    @Update("UPDATE community_notification SET is_read = 1 WHERE user_id = #{userId} AND is_read = 0")
    int markAllAsRead(@Param("userId") Long userId);
    
    /**
     * 标记指定通知为已读
     * @param notificationId 通知ID
     * @return 影响行数
     */
    @Update("UPDATE community_notification SET is_read = 1 WHERE id = #{notificationId}")
    int markAsRead(@Param("notificationId") Long notificationId);
    
    /**
     * 删除用户所有通知
     * @param userId 用户ID
     * @return 影响行数
     */
    @Update("DELETE FROM community_notification WHERE user_id = #{userId}")
    int deleteAllByUserId(@Param("userId") Long userId);
    
    /**
     * 获取用户特定类型的通知列表
     * @param page 分页参数
     * @param userId 用户ID
     * @param notificationType 通知类型
     * @return 通知列表
     */
    @Select("SELECT * FROM community_notification WHERE user_id = #{userId} AND notification_type = #{notificationType} ORDER BY created_at DESC")
    Page<CommunityNotification> selectNotificationsByType(Page<CommunityNotification> page, @Param("userId") Long userId, @Param("notificationType") String notificationType);
} 