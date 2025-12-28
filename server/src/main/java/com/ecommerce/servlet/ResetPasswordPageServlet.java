package com.ecommerce.servlet;

import com.ecommerce.service.AuthService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/reset-password")
public class ResetPasswordPageServlet extends HttpServlet {
    
    private final AuthService authService = new AuthService();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        request.getRequestDispatcher("/auth/reset-password.jsp").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        String token = request.getParameter("token");
        String password = request.getParameter("password");
        String confirm = request.getParameter("confirm");
        
        try {
            if (token == null || token.trim().isEmpty()) {
                throw new Exception("Token không hợp lệ");
            }
            
            if (password == null || password.trim().isEmpty()) {
                throw new Exception("Vui lòng nhập mật khẩu mới");
            }
            
            if (confirm == null || confirm.trim().isEmpty()) {
                throw new Exception("Vui lòng xác nhận mật khẩu");
            }
            
            if (!password.equals(confirm)) {
                throw new Exception("Mật khẩu xác nhận không khớp");
            }
            
            if (password.length() < 6) {
                throw new Exception("Mật khẩu phải có ít nhất 6 ký tự");
            }
            
            // Reset password
            authService.resetPassword(token, password);
            
            // Success
            request.setAttribute("success", "Đổi mật khẩu thành công! Đang chuyển đến trang đăng nhập...");
            request.getRequestDispatcher("/auth/reset-password.jsp").forward(request, response);
            
        } catch (Exception e) {
            request.setAttribute("error", e.getMessage());
            request.getRequestDispatcher("/auth/reset-password.jsp").forward(request, response);
        }
    }
}
