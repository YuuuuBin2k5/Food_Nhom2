package com.ecommerce.util;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.util.Properties;

public class MailUtil {

    // Lấy từ environment variables
    private static final String SMTP_HOST = System.getenv("SMTP_HOST");      // vd: smtp-relay.brevo.com
    private static final String SMTP_PORT = System.getenv("SMTP_PORT");      // 465
    private static final String USERNAME  = System.getenv("SMTP_USERNAME");  // Brevo SMTP key
    private static final String PASSWORD  = System.getenv("SMTP_PASSWORD");  // Brevo SMTP secret
    private static final String FROM_EMAIL = System.getenv("FROM_EMAIL");    // email đã verify
    private static final String FROM_NAME  = "FoodRescue";

    /**
     * Gửi email (SSL - Port 465)
     */
    public static void send(String to, String subject, String content) throws Exception {

        // ================= VALIDATE CONFIG =================
        if (isEmpty(SMTP_HOST)) throw new Exception("SMTP_HOST chưa được cấu hình");
        if (isEmpty(SMTP_PORT)) throw new Exception("SMTP_PORT chưa được cấu hình");
        if (isEmpty(USERNAME))  throw new Exception("SMTP_USERNAME chưa được cấu hình");
        if (isEmpty(PASSWORD))  throw new Exception("SMTP_PASSWORD chưa được cấu hình");
        if (isEmpty(FROM_EMAIL))throw new Exception("FROM_EMAIL chưa được cấu hình");

        System.out.println("=== [MAIL] Sending email ===");
        System.out.println("SMTP_HOST : " + SMTP_HOST);
        System.out.println("SMTP_PORT : " + SMTP_PORT);
        System.out.println("FROM      : " + FROM_EMAIL);
        System.out.println("TO        : " + to);
        System.out.println("SUBJECT   : " + subject);

        // ================= SMTP PROPERTIES (SSL 465) =================
        Properties props = new Properties();
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.port", SMTP_PORT);
        props.put("mail.smtp.auth", "true");

        // 🔥 QUAN TRỌNG: SSL cho port 465
        props.put("mail.smtp.ssl.enable", "true");
        props.put("mail.smtp.ssl.trust", SMTP_HOST);
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");

        // Timeout để tránh treo server
        props.put("mail.smtp.connectiontimeout", "10000");
        props.put("mail.smtp.timeout", "10000");
        props.put("mail.smtp.writetimeout", "10000");

        // ================= SESSION =================
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(USERNAME, PASSWORD);
            }
        });

        session.setDebug(true); // bật log SMTP

        try {
            // ================= CREATE MESSAGE =================
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(FROM_EMAIL, FROM_NAME));
            message.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse(to)
            );
            message.setSubject(subject);
            message.setText(content);

            // ================= SEND =================
            Transport.send(message);

            System.out.println("=== [MAIL] Email sent successfully ===");

        } catch (Exception e) {
            System.err.println("=== [MAIL] Failed to send email ===");
            e.printStackTrace();
            throw new Exception("Không thể gửi email", e);
        }
    }

    // ================= UTILITY =================
    private static boolean isEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }
}
