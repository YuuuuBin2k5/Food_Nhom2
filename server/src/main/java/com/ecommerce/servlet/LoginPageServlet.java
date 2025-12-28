package com.ecommerce.servlet;

import java.io.IOException;
import com.ecommerce.entity.*;
import com.ecommerce.service.AuthService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "LoginPageServlet", urlPatterns = {"/login"})
public class LoginPageServlet extends HttpServlet {

    private final AuthService authService = new AuthService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Check if already logged in
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("user") != null) {
            String role = (String) session.getAttribute("role");
            // Truyền request vào để lấy ContextPath
            redirectByRole(request, response, role); 
            return;
        }
        
        // Show login page
        request.getRequestDispatcher("/auth/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String remember = request.getParameter("remember");
        
        try {
            // Authenticate user
            User user = authService.login(email, password);
            
            // 1. SECURITY: Ngăn chặn Session Fixation Attack
            HttpSession oldSession = request.getSession(false);
            if (oldSession != null) {
                oldSession.invalidate();
            }

            // 2. Create NEW session
            HttpSession session = request.getSession(true);
            String role = determineRole(user);

            session.setAttribute("user", user);
            session.setAttribute("userId", user.getUserId());
            session.setAttribute("role", role);
            
            // 3. Logic Timeout tối ưu hơn
            if ("on".equals(remember)) {
                session.setMaxInactiveInterval(30 * 24 * 60 * 60); // 30 ngày
            } else {
                // Mặc định 30 phút (1800s) là chuẩn industry, 1 ngày là quá dài cho session thường
                session.setMaxInactiveInterval(30 * 60); 
            }
            
            // Redirect based on role
            redirectByRole(request, response, role);
            
        } catch (Exception e) {
            // Login failed
            request.setAttribute("error", "Email hoặc mật khẩu không đúng");
            request.setAttribute("email", email); // Giữ lại email để user đỡ phải gõ lại
            request.getRequestDispatcher("/auth/login.jsp").forward(request, response);
        }
    }
    
    private String determineRole(User user) {
        if (user instanceof Admin) return "ADMIN";
        if (user instanceof Seller) return "SELLER";
        if (user instanceof Shipper) return "SHIPPER";
        if (user instanceof Buyer) return "BUYER";
        return "UNKNOWN";
    }
    
    // Đã thêm tham số HttpServletRequest để lấy ContextPath
    private void redirectByRole(HttpServletRequest request, HttpServletResponse response, String role) throws IOException {
        String contextPath = request.getContextPath(); // Lấy tên project, ví dụ: "/MyEcommerce"
        String redirectUrl = contextPath + "/"; // Mặc định

        switch (role) {
            case "ADMIN":
                redirectUrl = contextPath + "/admin/statistics";
                break;
            case "SELLER":
                redirectUrl = contextPath + "/seller/dashboard";
                break;
            case "SHIPPER":
                redirectUrl = contextPath + "/shipper/orders";
                break;
            case "BUYER":
            default:
                // Buyer thường về trang chủ shop chứ không vào trang server quản trị
                redirectUrl = contextPath + "/"; 
                break;
        }
        
        response.sendRedirect(response.encodeRedirectURL(redirectUrl));
    }
}