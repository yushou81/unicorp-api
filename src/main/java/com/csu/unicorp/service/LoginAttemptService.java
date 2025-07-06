package com.csu.unicorp.service;

/**
 * 登录尝试服务接口
 * 用于管理用户登录尝试次数和账户锁定
 */
public interface LoginAttemptService {

    /**
     * 记录登录失败
     *
     * @param username 用户名
     * @return 剩余尝试次数
     */
    int recordFailedAttempt(String username);

    /**
     * 重置登录尝试次数
     *
     * @param username 用户名
     */
    void resetAttempts(String username);

    /**
     * 检查用户是否被锁定
     *
     * @param username 用户名
     * @return 是否被锁定
     */
    boolean isLocked(String username);

    /**
     * 锁定用户账户
     *
     * @param username 用户名
     * @param reason 锁定原因
     * @param lockDurationInMinutes 锁定时长（分钟）
     */
    void lockAccount(String username, String reason, long lockDurationInMinutes);

    /**
     * 解锁用户账户
     *
     * @param username 用户名
     * @return 是否成功解锁
     */
    boolean unlockAccount(String username);

    /**
     * 获取剩余尝试次数
     *
     * @param username 用户名
     * @return 剩余尝试次数
     */
    int getRemainingAttempts(String username);

    /**
     * 获取锁定剩余时间（分钟）
     *
     * @param username 用户名
     * @return 锁定剩余时间（分钟），如果未锁定则返回0
     */
    long getLockTimeRemaining(String username);
} 