package com.csu.unicorp.service;

/**
 * 邮件服务接口
 */
public interface EmailService {
    
    /**
     * 发送纯文本邮件
     * 
     * @param to 收件人邮箱
     * @param subject 邮件主题
     * @param content 邮件内容
     */
    void sendSimpleMail(String to, String subject, String content);
    
    /**
     * 发送HTML格式邮件
     * 
     * @param to 收件人邮箱
     * @param subject 邮件主题
     * @param content HTML内容
     */
    void sendHtmlMail(String to, String subject, String content);
    
    /**
     * 发送验证码邮件
     * 
     * @param to 收件人邮箱
     * @param code 验证码
     * @return 是否发送成功
     */
    boolean sendVerificationCode(String to, String code);
} 