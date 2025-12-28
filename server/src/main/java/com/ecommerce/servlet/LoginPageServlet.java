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
            User user = (User) session.getAttribute("user");
            String role = (String) session.getAttribute("role");
            redirectByRole(response, role);
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
            
            // Determine role
            String role = determineRole(user);
            
            // Create session
            HttpSession session = request.getSession(true);
            session.setAttribute("user", user);
            session.setAttribute("userId", user.getUserId());
            session.setAttribute("role", role);
            
            // Set session timeout
            if ("on".equals(remember)) {
                session.setMaxInactiveInterval(30 * 24 * 60 * 60); // 30 days
            } else {
                session.setMaxInactiveInterval(24 * 60 * 60); // 1 day
            }
            
            // Redirect based on role
            redirectByRole(response, role);
            
        } catch (Exception e) {
            // Login failed
            request.setAttribute("error", "Email hoặc mật khẩu không đúng");
            request.setAttribute("email", email);
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
    
    private void redirectByRole(HttpServletResponse response, String role) throws IOException {
        switch (role) {
            case "ADMIN":
                response.sendRedirect(response.encodeRedirectURL("/server/admin/statistics"));
                break;
            case "SELLER":
                response.sendRedirect(response.encodeRedirectURL("/server/seller/dashboard"));
                break;
            case "SHIPPER":
                response.sendRedirect(response.encodeRedirectURL("/server/shipper/orders"));
                break;
            case "BUYER":
            default:
                response.sendRedirect(response.encodeRedirectURL("/server/"));
                break;
        }
    }
}
