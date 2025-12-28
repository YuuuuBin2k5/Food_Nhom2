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
    
    /**
     * Generate and send OTP to email
     */
    public String generateOtp(String email) throws Exception {
        // Generate 6-digit OTP
        String otp = String.format("%06d", new Random().nextInt(999999));
        
        // Store OTP with timestamp
        otpStore.put(email.toLowerCase(), new OtpData(otp, System.currentTimeMillis()));
        
        // Send email
        try {
            String subject = "Mã xác thực đăng ký tài khoản - FreshSave";
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
               "Cảm ơn bạn đã đăng ký tài khoản tại FreshSave!\n\n" +
               "Mã xác thực (OTP) của bạn là:\n\n" +
               "    " + otp + "\n\n" +
               "Mã này có hiệu lực trong 10 phút.\n\n" +
               "Nếu bạn không thực hiện đăng ký này, vui lòng bỏ qua email này.\n\n" +
               "Trân trọng,\n" +
               "Đội ngũ FreshSave";
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
