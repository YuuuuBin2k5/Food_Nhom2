package com.ecommerce.service;

import com.ecommerce.util.MailUtil;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service quản lý OTP cho xác thực email
 * OTP có hiệu lực 10 phút
 */
public class OtpService {
    
    // Store OTP in memory: email -> OTP data
    private static final Map<String, OtpData> otpStore = new ConcurrentHashMap<>();
    
    // OTP expiry time: 10 minutes
    private static final long OTP_EXPIRY_MS = 10 * 60 * 1000;
    
    // Cleanup expired OTPs every 5 minutes
    static {
        Thread cleanupThread = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(5 * 60 * 1000); // 5 minutes
                    cleanupExpiredOtps();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });
        cleanupThread.setDaemon(true);
        cleanupThread.start();
    }
    
    /**
     * Cleanup expired OTPs
     */
    private static void cleanupExpiredOtps() {
        long now = System.currentTimeMillis();
        otpStore.entrySet().removeIf(entry -> 
            now - entry.getValue().timestamp > OTP_EXPIRY_MS
        );
        System.out.println("=== [OTP] Cleaned up expired OTPs. Remaining: " + otpStore.size() + " ===");
    }
    
    /**
     * Generate and send OTP to email
     */
    public String generateOtp(String email) throws Exception {
        email = email.toLowerCase();
        
        // Check rate limiting - không cho gửi quá 1 OTP trong 1 phút
        OtpData existingData = otpStore.get(email);
        if (existingData != null) {
            long timeSinceLastOtp = System.currentTimeMillis() - existingData.timestamp;
            if (timeSinceLastOtp < 60 * 1000) { // 1 minute
                long secondsRemaining = (60 * 1000 - timeSinceLastOtp) / 1000;
                throw new Exception("Vui lòng đợi " + secondsRemaining + " giây trước khi gửi lại OTP");
            }
        }
        
        // Generate 6-digit OTP (đảm bảo luôn có 6 chữ số)
        String otp = String.format("%06d", new Random().nextInt(1000000));
        
        // Store OTP with timestamp
        otpStore.put(email, new OtpData(otp, System.currentTimeMillis()));
        
        // Send email
        try {
            String subject = "Mã xác thực đăng ký tài khoản - FoodRescue";
            String body = buildEmailBody(otp);
            
            MailUtil.send(email, subject, body);
            
            System.out.println("=== [OTP] Email: " + email + " | OTP: " + otp + " ===");
        } catch (Exception e) {
            System.err.println("Lỗi gửi email OTP: " + e.getMessage());
            // For development: still return OTP even if email fails
            System.out.println("=== [OTP DEV MODE] Email: " + email + " | OTP: " + otp + " ===");
        }
        
        return otp;
    }
    
    /**
     * Verify OTP for email
     */
    public boolean verifyOtp(String email, String otp) {
        OtpData data = otpStore.get(email.toLowerCase());
        
        if (data == null) {
            return false; // No OTP found
        }
        
        // Check if expired
        if (System.currentTimeMillis() - data.timestamp > OTP_EXPIRY_MS) {
            otpStore.remove(email.toLowerCase());
            return false;
        }
        
        // Check if OTP matches
        if (data.otp.equals(otp)) {
            otpStore.remove(email.toLowerCase()); // Remove after successful verification
            return true;
        }
        
        return false;
    }
    
    /**
     * Clear OTP for email
     */
    public void clearOtp(String email) {
        otpStore.remove(email.toLowerCase());
    }
    
    /**
     * Build email body with OTP
     */
    private String buildEmailBody(String otp) {
        return "Xin chào,\n\n" +
               "Cảm ơn bạn đã đăng ký tài khoản tại FoodRescue!\n\n" +
               "Mã xác thực (OTP) của bạn là:\n\n" +
               "    " + otp + "\n\n" +
               "Mã này có hiệu lực trong 10 phút.\n\n" +
               "Nếu bạn không thực hiện đăng ký này, vui lòng bỏ qua email này.\n\n" +
               "Trân trọng,\n" +
               "Đội ngũ FoodRescue";
    }
    
    /**
     * Inner class to store OTP data
     */
    private static class OtpData {
        String otp;
        long timestamp;
        
        OtpData(String otp, long timestamp) {
            this.otp = otp;
            this.timestamp = timestamp;
        }
    }
}
