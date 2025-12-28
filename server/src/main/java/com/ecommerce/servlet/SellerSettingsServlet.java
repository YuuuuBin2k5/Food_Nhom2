package com.ecommerce.servlet;

import java.io.IOException;

import com.ecommerce.entity.User;
import com.ecommerce.util.MenuHelper;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/seller/settings")
public class SellerSettingsServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        String role = (String) session.getAttribute("role");
        if (!"SELLER".equals(role)) {
            response.sendRedirect(request.getContextPath() + "/home");
            return;
        }
        
        // Set menu items
        MenuHelper.setMenuItems(request, "SELLER", "/seller/settings");
        
        request.getRequestDispatcher("/seller/settings.jsp").forward(request, response);
    }
}
