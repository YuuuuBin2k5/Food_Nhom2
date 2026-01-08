package com.ecommerce.servlet;

import java.io.IOException;
import java.util.UUID;

import com.ecommerce.entity.User;
import com.ecommerce.service.AuthService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "LoginPageServlet", urlPatterns = {"/login"})
public class LoginPageServlet extends HttpServlet {

    private final AuthService authService = new AuthService();
    private static final String REMEMBER_COOKIE_NAME = "rememberToken";
    private static final int REMEMBER_COOKIE_MAX_AGE = 30 * 24 * 60 * 60; 

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Check if already logged in
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("user") != null) {
            String role = (String) session.getAttribute("role");
            redirectByRole(request, response, role); 
            return;
        }
        
        // Check remember cookie for auto-login
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (REMEMBER_COOKIE_NAME.equals(cookie.getName())) {
                    String cookieValue = cookie.getValue();
                    if (cookieValue != null && cookieValue.contains(":")) {
                        try {
                            String[] parts = cookieValue.split(":");
                            Long userId = Long.parseLong(parts[0]);
                            
                            // Lấy user từ database
                            User user = authService.getUserById(userId);
                            if (user != null && !user.isBanned()) {
                                // Tạo session mới
                                HttpSession newSession = request.getSession(true);
                                String role = user.getRole().name(); // Lấy role từ User entity
                                
                                newSession.setAttribute("user", user);
                                newSession.setAttribute("userId", user.getUserId());
                                newSession.setAttribute("role", role);
                                newSession.setMaxInactiveInterval(REMEMBER_COOKIE_MAX_AGE);
                                
                                redirectByRole(request, response, role);
                                return;
                            }
                        } catch (Exception e) {
                            // Cookie không hợp lệ, xóa đi
                            Cookie invalidCookie = new Cookie(REMEMBER_COOKIE_NAME, "");
                            invalidCookie.setMaxAge(0);
                            invalidCookie.setPath(request.getContextPath().isEmpty() ? "/" : request.getContextPath());
                            response.addCookie(invalidCookie);
                        }
                    }
                }
            }
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
            String role = user.getRole().name(); // Lấy role từ User entity

            session.setAttribute("user", user);
            session.setAttribute("userId", user.getUserId());
            session.setAttribute("role", role);
            
            // 3. Xử lý "Ghi nhớ tôi"
            if ("on".equals(remember)) {
                // Tạo remember token và lưu vào cookie
                String rememberToken = UUID.randomUUID().toString();
                
                // Lưu token vào session để verify sau này
                session.setAttribute("rememberToken", rememberToken);
                
                // Tạo cookie với thời gian sống 30 ngày
                Cookie rememberCookie = new Cookie(REMEMBER_COOKIE_NAME, user.getUserId() + ":" + rememberToken);
                rememberCookie.setMaxAge(REMEMBER_COOKIE_MAX_AGE);
                rememberCookie.setPath(request.getContextPath().isEmpty() ? "/" : request.getContextPath());
                rememberCookie.setHttpOnly(true); // Bảo mật: không cho JS truy cập
                response.addCookie(rememberCookie);
                
                // Session timeout dài hơn khi ghi nhớ
                session.setMaxInactiveInterval(REMEMBER_COOKIE_MAX_AGE);
            } else {
                // Xóa cookie cũ nếu có
                Cookie rememberCookie = new Cookie(REMEMBER_COOKIE_NAME, "");
                rememberCookie.setMaxAge(0);
                rememberCookie.setPath(request.getContextPath().isEmpty() ? "/" : request.getContextPath());
                response.addCookie(rememberCookie);
                
                // Session timeout mặc định 30 phút
                session.setMaxInactiveInterval(30 * 60);
            }
            
            // Redirect based on role
            redirectByRole(request, response, role);
            
        } catch (Exception e) {
            // Login failed - hiển thị message cụ thể từ AuthService
            String errorMessage = e.getMessage();
            
            // Nếu message null hoặc rỗng, dùng message mặc định
            if (errorMessage == null || errorMessage.trim().isEmpty()) {
                errorMessage = "Email hoặc mật khẩu không đúng";
            }
            
            request.setAttribute("error", errorMessage);
            request.setAttribute("email", email); // Giữ lại email để user đỡ phải gõ lại
            request.getRequestDispatcher("/auth/login.jsp").forward(request, response);
        }
    }
    
    private void redirectByRole(HttpServletRequest request, HttpServletResponse response, String role) throws IOException {
        String contextPath = request.getContextPath();
        String redirectUrl = contextPath + "/";

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
                redirectUrl = contextPath + "/"; 
                break;
        }
        
        response.sendRedirect(response.encodeRedirectURL(redirectUrl));
    }
}