package com.ecommerce.servlet;

import com.ecommerce.service.AuthService;
import com.ecommerce.util.AppConfig;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/forgot-password")
public class ForgotPasswordPageServlet extends HttpServlet {
    
    private final AuthService authService = new AuthService();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        request.getRequestDispatcher("/auth/forgot-password.jsp").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        String email = request.getParameter("email");
        
        try {
            if (email == null || email.trim().isEmpty()) {
                throw new Exception("Vui lòng nhập email");
            }
            
            // Lấy base URL động
            String baseUrl = AppConfig.getBaseUrl(request);
            
            // Send reset password email
            authService.forgotPassword(email, baseUrl);
            
            // Success
            request.setAttribute("success", "Đã gửi email hướng dẫn đặt lại mật khẩu. Vui lòng kiểm tra hộp thư.");
            request.setAttribute("email", email);
            request.getRequestDispatcher("/auth/forgot-password.jsp").forward(request, response);
            
        } catch (Exception e) {
            request.setAttribute("error", e.getMessage());
            request.setAttribute("email", email);
            request.getRequestDispatcher("/auth/forgot-password.jsp").forward(request, response);
        }
    }
}
