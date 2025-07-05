package com.csu.unicorp.common.constants;

/**
 * 社区常量类
 */
public class CommunityConstants {
    
    /**
     * 内容状态
     */
    public static class Status {
        /**
         * 正常
         */
        public static final String NORMAL = "NORMAL";
        
        /**
         * 待审核
         */
        public static final String PENDING = "PENDING";
        
        /**
         * 已删除
         */
        public static final String DELETED = "DELETED";
        
        /**
         * 未解决（问题）
         */
        public static final String UNSOLVED = "UNSOLVED";
        
        /**
         * 已解决（问题）
         */
        public static final String SOLVED = "SOLVED";
        
        /**
         * 已关闭（问题）
         */
        public static final String CLOSED = "CLOSED";
    }
    
    /**
     * 内容类型
     */
    public static class ContentType {
        /**
         * 话题
         */
        public static final String TOPIC = "TOPIC";
        
        /**
         * 评论
         */
        public static final String COMMENT = "COMMENT";
        
        /**
         * 问题
         */
        public static final String QUESTION = "QUESTION";
        
        /**
         * 回答
         */
        public static final String ANSWER = "ANSWER";
        
        /**
         * 资源
         */
        public static final String RESOURCE = "RESOURCE";
    }
    
    /**
     * 通知类型
     */
    public static class NotificationType {
        /**
         * 评论
         */
        public static final String COMMENT = "COMMENT";
        
        /**
         * 点赞
         */
        public static final String LIKE = "LIKE";
        
        /**
         * 关注
         */
        public static final String FOLLOW = "FOLLOW";
        
        /**
         * 系统
         */
        public static final String SYSTEM = "SYSTEM";
        
        /**
         * 回答
         */
        public static final String ANSWER = "ANSWER";
        
        /**
         * 采纳
         */
        public static final String ACCEPT = "ACCEPT";
    }
    
    /**
     * 关系类型
     */
    public static class RelationType {
        /**
         * 关注
         */
        public static final String FOLLOW = "FOLLOW";
        
        /**
         * 拉黑
         */
        public static final String BLOCK = "BLOCK";
    }
    
    /**
     * 权限级别
     */
    public static class PermissionLevel {
        /**
         * 公开
         */
        public static final int PUBLIC = 0;
        
        /**
         * 登录可见
         */
        public static final int LOGIN = 1;
        
        /**
         * 组织成员可见
         */
        public static final int ORGANIZATION = 2;
        
        /**
         * 管理员可见
         */
        public static final int ADMIN = 3;
    }
} 