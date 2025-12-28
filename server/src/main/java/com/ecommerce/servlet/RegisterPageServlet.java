package com.ecommerce.servlet;

import java.io.IOException;

import com.ecommerce.service.AuthService;
import com.ecommerce.service.OtpService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "RegisterPageServlet", urlPatterns = {"/register"})
public class RegisterPageServlet extends HttpServlet {

    private final AuthService authService = new AuthService();
    private final OtpService otpService = new OtpService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Check if already logged in
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("user") != null) {
            // Already logged in, redirect to home
            response.sendRedirect(request.getContextPath() + "/");
            return;
        }
        
        // Show register page
        request.getRequestDispatcher("/auth/register.jsp").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        
        String fullName = request.getParameter("fullName");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");
        String phone = request.getParameter("phone");
        String address = request.getParameter("address");
        String role = request.getParameter("role");
        String otp = request.getParameter("otp");
        
        // Seller-specific fields
        String shopName = request.getParameter("shopName");
        String businessLicense = request.getParameter("businessLicense");
        
        try {
            // Validate
            if (fullName == null || fullName.trim().isEmpty()) {
                throw new Exception("Vui lòng nhập họ tên");
            }
            if (email == null || email.trim().isEmpty()) {
                throw new Exception("Vui lòng nhập email");
            }
            if (password == null || password.length() < 6) {
                throw new Exception("Mật khẩu phải có ít nhất 6 ký tự");
            }
            if (!password.equals(confirmPassword)) {
                throw new Exception("Mật khẩu xác nhận không khớp");
            }
            if (phone == null || phone.trim().isEmpty()) {
                throw new Exception("Vui lòng nhập số điện thoại");
            }
            if (otp == null || otp.trim().isEmpty()) {
                throw new Exception("Vui lòng nhập mã OTP");
            }
            
            // Verify OTP
            String cleanEmail = email.trim().toLowerCase();
            if (!otpService.verifyOtp(cleanEmail, otp)) {
                throw new Exception("Mã OTP không đúng hoặc đã hết hạn");
            }
            
            // Register user
            authService.register(fullName, email, password, phone, role, shopName);
            
            // Success - redirect to login
            response.sendRedirect(request.getContextPath() + "/login?registered=true");
            
        } catch (Exception e) {
            // Registration failed
            request.setAttribute("error", e.getMessage());
            request.setAttribute("fullName", fullName);
            request.setAttribute("email", email);
            request.setAttribute("phone", phone);
            request.setAttribute("address", address);
            request.setAttribute("role", role);
            request.setAttribute("shopName", shopName);
            request.setAttribute("businessLicense", businessLicense);
            request.getRequestDispatcher("/auth/register.jsp").forward(request, response);
        }
    }
}
