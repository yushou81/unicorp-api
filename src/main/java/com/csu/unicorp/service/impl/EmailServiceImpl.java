package com.csu.unicorp.service.impl;

import com.csu.unicorp.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

/**
 * 邮件服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
    
    private final JavaMailSender mailSender;
    
    @Value("${spring.mail.username}")
    private String sender;
    
    @Override
    public void sendSimpleMail(String to, String subject, String content) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(sender);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(content);
            mailSender.send(message);
            log.info("简单邮件已发送至: {}", to);
        } catch (Exception e) {
            log.error("发送简单邮件时发生错误: {}", e.getMessage(), e);
            throw new RuntimeException("邮件发送失败", e);
        }
    }
    
    @Override
    public void sendHtmlMail(String to, String subject, String content) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(sender);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true);
            mailSender.send(message);
            log.info("HTML邮件已发送至: {}", to);
        } catch (MessagingException e) {
            log.error("发送HTML邮件时发生错误: {}", e.getMessage(), e);
            throw new RuntimeException("邮件发送失败", e);
        }
    }
    
    @Override
    public boolean sendVerificationCode(String to, String code) {
        try {
            String subject = "UniCorp平台 - 邮箱验证码";
            String content = buildVerificationEmailContent(code);
            sendHtmlMail(to, subject, content);
            return true;
        } catch (Exception e) {
            log.error("发送验证码邮件时发生错误: {}", e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * 构建验证码邮件内容
     * 
     * @param code 验证码
     * @return HTML格式的邮件内容
     */
    private String buildVerificationEmailContent(String code) {
        return "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <title>邮箱验证码</title>\n" +
                "    <style>\n" +
                "        .container {\n" +
                "            width: 600px;\n" +
                "            margin: 0 auto;\n" +
                "            padding: 20px;\n" +
                "            font-family: 'Microsoft YaHei', Arial, sans-serif;\n" +
                "            line-height: 1.6;\n" +
                "            color: #333;\n" +
                "        }\n" +
                "        .header {\n" +
                "            background-color: #4A90E2;\n" +
                "            color: white;\n" +
                "            padding: 10px 20px;\n" +
                "            text-align: center;\n" +
                "            border-radius: 5px 5px 0 0;\n" +
                "        }\n" +
                "        .content {\n" +
                "            padding: 20px;\n" +
                "            background-color: #f7f7f7;\n" +
                "            border: 1px solid #eee;\n" +
                "            border-radius: 0 0 5px 5px;\n" +
                "        }\n" +
                "        .code {\n" +
                "            font-size: 24px;\n" +
                "            font-weight: bold;\n" +
                "            color: #4A90E2;\n" +
                "            letter-spacing: 5px;\n" +
                "            padding: 10px 20px;\n" +
                "            background-color: #e9f0fa;\n" +
                "            border-radius: 5px;\n" +
                "            display: inline-block;\n" +
                "            margin: 15px 0;\n" +
                "        }\n" +
                "        .footer {\n" +
                "            margin-top: 20px;\n" +
                "            font-size: 12px;\n" +
                "            color: #999;\n" +
                "            text-align: center;\n" +
                "        }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <div class=\"container\">\n" +
                "        <div class=\"header\">\n" +
                "            <h2>UniCorp平台 - 邮箱验证码</h2>\n" +
                "        </div>\n" +
                "        <div class=\"content\">\n" +
                "            <p>亲爱的用户：</p>\n" +
                "            <p>您好！感谢您使用UniCorp平台。您的邮箱验证码为：</p>\n" +
                "            <div style=\"text-align: center;\">\n" +
                "                <span class=\"code\">" + code + "</span>\n" +
                "            </div>\n" +
                "            <p>该验证码有效期为5分钟，请及时使用。如非本人操作，请忽略此邮件。</p>\n" +
                "            <p>祝您使用愉快！</p>\n" +
                "            <p>UniCorp平台团队</p>\n" +
                "        </div>\n" +
                "        <div class=\"footer\">\n" +
                "            <p>此邮件由系统自动发送，请勿直接回复。</p>\n" +
                "            <p>© " + java.time.Year.now().getValue() + " UniCorp平台 版权所有</p>\n" +
                "        </div>\n" +
                "    </div>\n" +
                "</body>\n" +
                "</html>";
    }
} 