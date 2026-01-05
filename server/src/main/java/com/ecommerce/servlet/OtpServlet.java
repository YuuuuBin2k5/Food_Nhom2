package com.ecommerce.servlet;

import com.ecommerce.service.OtpService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

/**
 * OTP Servlet - Xử lý gửi OTP (Form-based, không dùng AJAX)
 * 
 * POST /otp/send - Gửi OTP đến email
 */
@WebServlet("/otp/send")
public class OtpServlet extends HttpServlet {
    
    private final OtpService otpService = new OtpService();
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        
        String email = request.getParameter("email");
        String fullName = request.getParameter("fullName");
        String phone = request.getParameter("phone");
        String role = request.getParameter("role");
        String shopName = request.getParameter("shopName");
        
        // Validate email
        if (email == null || email.trim().isEmpty()) {
            session.setAttribute("otpError", "Vui lòng nhập email");
            redirectBackToRegister(response, request, fullName, phone, email, role, shopName);
            return;
        }
        
        email = email.trim().toLowerCase();
        
        // Validate email format
        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            session.setAttribute("otpError", "Định dạng email không hợp lệ");
            redirectBackToRegister(response, request, fullName, phone, email, role, shopName);
            return;
        }
        
        try {
            // Generate and send OTP
            String otp = otpService.generateOtp(email);
            
            // Store in session that OTP was sent
            session.setAttribute("otpSent", true);
            session.setAttribute("otpEmail", email);
            session.setAttribute("otpSuccess", "Mã OTP đã được gửi đến " + email + ". Kiểm tra hộp thư spam nếu không thấy.");
            
            // For development - show OTP (remove in production)
            session.setAttribute("devOtp", otp);
            
        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("otpError", "Lỗi gửi OTP: " + e.getMessage());
        }
        
        // Redirect back to register with form data preserved
        redirectBackToRegister(response, request, fullName, phone, email, role, shopName);
    }
    
    private void redirectBackToRegister(HttpServletResponse response, HttpServletRequest request,
            String fullName, String phone, String email, String role, String shopName) throws IOException {
        
        StringBuilder url = new StringBuilder(request.getContextPath() + "/register?");
        
        if (fullName != null && !fullName.isEmpty()) {
            url.append("fullName=").append(java.net.URLEncoder.encode(fullName, "UTF-8")).append("&");
        }
        if (phone != null && !phone.isEmpty()) {
            url.append("phone=").append(java.net.URLEncoder.encode(phone, "UTF-8")).append("&");
        }
        if (email != null && !email.isEmpty()) {
            url.append("email=").append(java.net.URLEncoder.encode(email, "UTF-8")).append("&");
        }
        if (role != null && !role.isEmpty()) {
            url.append("role=").append(java.net.URLEncoder.encode(role, "UTF-8")).append("&");
        }
        if (shopName != null && !shopName.isEmpty()) {
            url.append("shopName=").append(java.net.URLEncoder.encode(shopName, "UTF-8")).append("&");
        }
        
        response.sendRedirect(url.toString());
    }
}
