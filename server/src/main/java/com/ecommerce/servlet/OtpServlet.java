package com.ecommerce.servlet;

import com.ecommerce.service.OtpService;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * OTP operations servlet (returns JSON for AJAX)
 * Endpoints:
 * - POST /api/otp/send - Gửi OTP
 * - POST /api/otp/verify - Xác thực OTP
 */
@WebServlet("/api/otp/*")
public class OtpServlet extends HttpServlet {
    
    private final OtpService otpService = new OtpService();
    private final Gson gson = new Gson();
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        String pathInfo = request.getPathInfo();
        
        if ("/send".equals(pathInfo)) {
            sendOtp(request, response);
        } else if ("/verify".equals(pathInfo)) {
            verifyOtp(request, response);
        } else {
            sendJsonResponse(response, false, "Endpoint không hợp lệ", 404);
        }
    }
    
    /**
     * Send OTP to email
     */
    private void sendOtp(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        try {
            JsonObject jsonRequest = gson.fromJson(request.getReader(), JsonObject.class);
            String email = jsonRequest.get("email").getAsString();
            
            // Validate email
            if (email == null || email.trim().isEmpty()) {
                sendJsonResponse(response, false, "Email không hợp lệ", 400);
                return;
            }
            
            // Validate email format
            email = email.trim().toLowerCase();
            if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
                sendJsonResponse(response, false, "Định dạng email không hợp lệ", 400);
                return;
            }
            
            // Generate and send OTP
            String otp = otpService.generateOtp(email);
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "Mã OTP đã được gửi đến email " + email);
            // For development only - remove in production
            result.put("otp", otp);
            
            response.setStatus(200);
            response.getWriter().write(gson.toJson(result));
            
        } catch (Exception e) {
            e.printStackTrace();
            sendJsonResponse(response, false, "Lỗi gửi OTP: " + e.getMessage(), 500);
        }
    }
    
    /**
     * Verify OTP
     */
    private void verifyOtp(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        try {
            JsonObject jsonRequest = gson.fromJson(request.getReader(), JsonObject.class);
            String email = jsonRequest.get("email").getAsString();
            String otp = jsonRequest.get("otp").getAsString();
            
            // Validate inputs
            if (email == null || email.trim().isEmpty() || 
                otp == null || otp.trim().isEmpty()) {
                sendJsonResponse(response, false, "Thông tin không hợp lệ", 400);
                return;
            }
            
            email = email.trim().toLowerCase();
            
            // Verify OTP
            boolean isValid = otpService.verifyOtp(email, otp);
            
            if (isValid) {
                sendJsonResponse(response, true, "Xác thực thành công", 200);
            } else {
                sendJsonResponse(response, false, "Mã OTP không đúng hoặc đã hết hạn", 400);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            sendJsonResponse(response, false, "Lỗi xác thực OTP: " + e.getMessage(), 500);
        }
    }
    
    private void sendJsonResponse(HttpServletResponse response, boolean success, String message, int status) 
            throws IOException {
        response.setStatus(status);
        Map<String, Object> result = new HashMap<>();
        result.put("success", success);
        result.put("message", message);
        response.getWriter().write(gson.toJson(result));
    }
}
