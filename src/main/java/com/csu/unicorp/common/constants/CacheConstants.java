package com.csu.unicorp.common.constants;

/**
 * 缓存常量类
 */
public class CacheConstants {

    /**
     * 话题缓存相关常量
     */
    // 话题详情缓存键前缀
    public static final String TOPIC_DETAIL_CACHE_KEY_PREFIX = "community:topic:detail:";
    // 热门话题缓存键
    public static final String HOT_TOPICS_CACHE_KEY = "community:topic:hot";
    // 最新话题缓存键
    public static final String LATEST_TOPICS_CACHE_KEY = "community:topic:latest";
    // 精华话题缓存键
    public static final String FEATURED_TOPICS_CACHE_KEY = "community:topic:featured";
    // 用户话题缓存键前缀
    public static final String USER_TOPICS_CACHE_KEY_PREFIX = "community:topic:user:";
    // 分类话题缓存键前缀
    public static final String CATEGORY_TOPICS_CACHE_KEY_PREFIX = "community:topic:category:";
    // 话题点赞状态缓存键前缀
    public static final String TOPIC_LIKED_STATUS_CACHE_KEY_PREFIX = "community:topic:liked:";
    // 话题收藏状态缓存键前缀
    public static final String TOPIC_FAVORITED_STATUS_CACHE_KEY_PREFIX = "community:topic:favorited:";

    /**
     * 标签缓存相关常量
     */
    // 标签详情缓存键前缀
    public static final String TAG_DETAIL_CACHE_KEY_PREFIX = "community:tag:detail:";
    // 热门标签缓存键
    public static final String HOT_TAGS_CACHE_KEY = "community:tag:hot";
    // 所有标签缓存键
    public static final String ALL_TAGS_CACHE_KEY = "community:tag:all";
    // 内容标签缓存键前缀
    public static final String CONTENT_TAGS_CACHE_KEY_PREFIX = "community:tag:content:";
    // 标签内容ID列表缓存键前缀
    public static final String TAG_CONTENT_IDS_CACHE_KEY_PREFIX = "community:tag:contentIds:";

    /**
     * 问题缓存相关常量
     */
    // 问题详情缓存键前缀
    public static final String QUESTION_DETAIL_CACHE_KEY_PREFIX = "community:question:detail:";
    // 热门问题缓存键
    public static final String HOT_QUESTIONS_CACHE_KEY = "community:question:hot";
    // 最新问题缓存键
    public static final String LATEST_QUESTIONS_CACHE_KEY = "community:question:latest";
    // 未解决问题缓存键
    public static final String UNSOLVED_QUESTIONS_CACHE_KEY = "community:question:unsolved";
    // 用户问题缓存键前缀
    public static final String USER_QUESTIONS_CACHE_KEY_PREFIX = "community:question:user:";
    // 分类问题缓存键前缀
    public static final String CATEGORY_QUESTIONS_CACHE_KEY_PREFIX = "community:question:category:";
    // 相关问题缓存键前缀
    public static final String RELATED_QUESTIONS_CACHE_KEY_PREFIX = "community:question:related:";

    /**
     * 回答缓存相关常量
     */
    // 回答详情缓存键前缀
    public static final String ANSWER_DETAIL_CACHE_KEY_PREFIX = "community:answer:detail:";
    // 问题回答列表缓存键前缀
    public static final String QUESTION_ANSWERS_CACHE_KEY_PREFIX = "community:answer:question:";
    // 用户回答列表缓存键前缀
    public static final String USER_ANSWERS_CACHE_KEY_PREFIX = "community:answer:user:";

    /**
     * 评论缓存相关常量
     */
    // 评论详情缓存键前缀
    public static final String COMMENT_DETAIL_CACHE_KEY_PREFIX = "community:comment:detail:";
    // 话题评论列表缓存键前缀
    public static final String TOPIC_COMMENTS_CACHE_KEY_PREFIX = "community:comment:topic:";
    // 回答评论列表缓存键前缀
    public static final String ANSWER_COMMENTS_CACHE_KEY_PREFIX = "community:comment:answer:";
    // 评论回复列表缓存键前缀
    public static final String COMMENT_REPLIES_CACHE_KEY_PREFIX = "community:comment:replies:";
    // 用户评论列表缓存键前缀
    public static final String USER_COMMENTS_CACHE_KEY_PREFIX = "community:comment:user:";

    /**
     * 通知缓存相关常量
     */
    // 用户通知列表缓存键前缀
    public static final String USER_NOTIFICATIONS_CACHE_KEY_PREFIX = "community:notification:user:";
    // 未读通知数量缓存键前缀
    public static final String UNREAD_NOTIFICATION_COUNT_CACHE_KEY_PREFIX = "community:notification:unread:";

    /**
     * 点赞缓存相关常量
     */
    // 内容点赞数缓存键前缀
    public static final String CONTENT_LIKE_COUNT_CACHE_KEY_PREFIX = "community:like:count:";
    // 用户点赞内容ID列表缓存键前缀
    public static final String USER_LIKED_CONTENT_IDS_CACHE_KEY_PREFIX = "community:like:user:";
    // 用户点赞状态缓存键前缀
    public static final String USER_LIKE_STATUS_CACHE_KEY_PREFIX = "community:like:status:";

    /**
     * 收藏缓存相关常量
     */
    // 内容收藏数缓存键前缀
    public static final String CONTENT_FAVORITE_COUNT_CACHE_KEY_PREFIX = "community:favorite:count:";
    // 用户收藏内容ID列表缓存键前缀
    public static final String USER_FAVORITED_CONTENT_IDS_CACHE_KEY_PREFIX = "community:favorite:user:";
    // 用户收藏状态缓存键前缀
    public static final String USER_FAVORITE_STATUS_CACHE_KEY_PREFIX = "community:favorite:status:";
    // 用户收藏话题列表缓存键前缀
    public static final String USER_FAVORITE_TOPICS_CACHE_KEY_PREFIX = "community:favorite:topics:";
    // 用户收藏问题列表缓存键前缀
    public static final String USER_FAVORITE_QUESTIONS_CACHE_KEY_PREFIX = "community:favorite:questions:";

    /**
     * 分类缓存相关常量
     */
    // 分类详情缓存键前缀
    public static final String CATEGORY_DETAIL_CACHE_KEY_PREFIX = "community:category:detail:";
    // 分类树缓存键
    public static final String CATEGORY_TREE_CACHE_KEY = "community:category:tree";
    // 用户可见分类缓存键前缀
    public static final String USER_VISIBLE_CATEGORIES_CACHE_KEY_PREFIX = "community:category:visible:";
    // 所有分类缓存键
    public static final String ALL_CATEGORIES_CACHE_KEY = "community:category:all";

    /**
     * 缓存过期时间常量（单位：秒）
     */
    // 话题缓存过期时间：1小时
    public static final long TOPIC_CACHE_EXPIRE_TIME = 60 * 60;
    // 热门话题缓存过期时间：30分钟
    public static final long HOT_TOPICS_CACHE_EXPIRE_TIME = 30 * 60;
    // 热门问题缓存过期时间：30分钟
    public static final long HOT_QUESTIONS_CACHE_EXPIRE_TIME = 30 * 60;
    // 标签缓存过期时间：2小时
    public static final long TAG_CACHE_EXPIRE_TIME = 2 * 60 * 60;
    // 热门标签缓存过期时间：1小时
    public static final long HOT_TAGS_CACHE_EXPIRE_TIME = 60 * 60;
    // 问题缓存过期时间：1小时
    public static final long QUESTION_CACHE_EXPIRE_TIME = 60 * 60;
    // 回答缓存过期时间：1小时
    public static final long ANSWER_CACHE_EXPIRE_TIME = 60 * 60;
    // 评论缓存过期时间：30分钟
    public static final long COMMENT_CACHE_EXPIRE_TIME = 30 * 60;
    // 通知缓存过期时间：5分钟
    public static final long NOTIFICATION_CACHE_EXPIRE_TIME = 5 * 60;
    // 点赞状态缓存过期时间：30分钟
    public static final long LIKE_STATUS_CACHE_EXPIRE_TIME = 30 * 60;
    // 收藏状态缓存过期时间：30分钟
    public static final long FAVORITE_STATUS_CACHE_EXPIRE_TIME = 30 * 60;
    // 分类缓存过期时间：6小时
    public static final long CATEGORY_CACHE_EXPIRE_TIME = 6 * 60 * 60;
    // 用户相关缓存过期时间：15分钟
    public static final long USER_CACHE_EXPIRE_TIME = 15 * 60;
} 