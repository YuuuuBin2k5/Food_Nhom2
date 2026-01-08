package com.ecommerce.util;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.util.Properties;

public class MailUtil {

    // Lấy từ environment variables (fallback to system properties for IDE)
    private static final String SMTP_HOST = getConfig("SMTP_HOST");
    private static final String SMTP_PORT = getConfig("SMTP_PORT");
    private static final String USERNAME = getConfig("SMTP_USERNAME");
    private static final String PASSWORD = getConfig("SMTP_PASSWORD");
    private static final String FROM_EMAIL = getConfig("FROM_EMAIL");
    private static final String FROM_NAME = "FoodRescue";
    
    /**
     * Get config from environment variable or system property
     */
    private static String getConfig(String key) {
        String value = System.getenv(key);
        if (value == null || value.isEmpty()) {
            value = System.getProperty(key);
        }
        return value;
    }
    
    /**
     * Gửi email
     * @throws Exception nếu có lỗi trong quá trình gửi
     */
    public static void send(String to, String subject, String content) throws Exception {
        // Kiểm tra config
        if (SMTP_HOST == null || SMTP_HOST.isEmpty()) {
            throw new Exception("SMTP_HOST chưa được cấu hình. Vui lòng set environment variable.");
        }
        if (SMTP_PORT == null || SMTP_PORT.isEmpty()) {
            throw new Exception("SMTP_PORT chưa được cấu hình. Vui lòng set environment variable.");
        }
        if (USERNAME == null || USERNAME.isEmpty()) {
            throw new Exception("SMTP_USERNAME chưa được cấu hình. Vui lòng set environment variable.");
        }
        if (PASSWORD == null || PASSWORD.isEmpty()) {
            throw new Exception("SMTP_PASSWORD chưa được cấu hình. Vui lòng set environment variable.");
        }
        if (FROM_EMAIL == null || FROM_EMAIL.isEmpty()) {
            throw new Exception("FROM_EMAIL chưa được cấu hình. Vui lòng set environment variable.");
        }
        
        System.out.println("=== [MAIL] Sending email ===");
        System.out.println("SMTP_HOST: " + SMTP_HOST);
        System.out.println("SMTP_PORT: " + SMTP_PORT);
        System.out.println("USERNAME: " + USERNAME);
        System.out.println("FROM_EMAIL: " + FROM_EMAIL);
        System.out.println("TO: " + to);
        System.out.println("SUBJECT: " + subject);
        
        // QUAN TRỌNG: Dùng 'smtp' thay vì 'smtps' cho cổng 2525/587
        Properties props = new Properties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.port", SMTP_PORT);
        props.put("mail.smtp.auth", "true");
        
        // BẮT BUỘC: Phải bật STARTTLS cho cổng 587/2525
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.starttls.required", "true");
        
        // Trust chứng chỉ để tránh lỗi xác thực trên môi trường Cloud
        props.put("mail.smtp.ssl.trust", SMTP_HOST);

        Session session = Session.getInstance(props, null);
        
        // Enable debug mode
        session.setDebug(true);

        try {
            // Tạo message
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(FROM_EMAIL, FROM_NAME));
            message.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse(to)
            );
            message.setSubject(subject);
            message.setText(content);

            // Gửi mail với explicit authentication (theo cách của bạn)
            try (Transport transport = session.getTransport("smtp")) {
                System.out.println("=== [MAIL] Connecting to SMTP server... ===");
                transport.connect(SMTP_HOST, USERNAME, PASSWORD);
                System.out.println("=== [MAIL] Connected! Sending message... ===");
                transport.sendMessage(message, message.getAllRecipients());
                System.out.println("=== [MAIL] Email sent successfully! ===");
            }

        } catch (Exception e) {
            System.err.println("=== [MAIL] Error sending email: " + e.getMessage() + " ===");
            e.printStackTrace();
            throw new Exception("Không thể gửi email: " + e.getMessage(), e);
        }
    }
}
