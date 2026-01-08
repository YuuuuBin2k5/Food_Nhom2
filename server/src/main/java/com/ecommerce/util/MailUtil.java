package com.ecommerce.util;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.util.Properties;

public class MailUtil {

    // Lấy từ environment variables
    private static final String SMTP_HOST = System.getenv("SMTP_HOST");
    private static final String SMTP_PORT = System.getenv("SMTP_PORT");
    private static final String USERNAME = System.getenv("SMTP_USERNAME");
    private static final String PASSWORD = System.getenv("SMTP_PASSWORD");
    private static final String FROM_EMAIL = System.getenv("FROM_EMAIL");
    private static final String FROM_NAME = "FoodRescue";
    
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
        
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.starttls.required", "true");
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.port", SMTP_PORT);
        props.put("mail.smtp.ssl.trust", SMTP_HOST);
        props.put("mail.smtp.ssl.protocols", "TLSv1.2 TLSv1.3");
        
        // Explicitly set user for authentication
        props.put("mail.smtp.user", USERNAME);
        props.put("mail.user", USERNAME);

        Session session = Session.getInstance(
                props,
                new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        System.out.println("=== [MAIL] Authenticator called with USERNAME: " + USERNAME + " ===");
                        return new PasswordAuthentication(USERNAME, PASSWORD);
                    }
                }
        );
        
        // Enable debug mode
        session.setDebug(true);

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(FROM_EMAIL, FROM_NAME));
            message.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse(to)
            );
            message.setSubject(subject);
            message.setText(content);

            // Use Transport with explicit authentication
            Transport transport = session.getTransport("smtp");
            System.out.println("=== [MAIL] Connecting with USERNAME: " + USERNAME + " ===");
            transport.connect(SMTP_HOST, Integer.parseInt(SMTP_PORT), USERNAME, PASSWORD);
            transport.sendMessage(message, message.getAllRecipients());
            transport.close();
            
            System.out.println("=== [MAIL] Email sent successfully! ===");

        } catch (Exception e) {
            System.err.println("=== [MAIL] Error sending email: " + e.getMessage() + " ===");
            e.printStackTrace();
            throw new Exception("Không thể gửi email: " + e.getMessage(), e);
        }
    }
}
