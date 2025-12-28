package com.ecommerce.servlet;

import com.ecommerce.service.AdminService;
import com.ecommerce.util.MenuHelper;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/admin/statistics")
public class AdminStatisticsServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private AdminService adminService = new AdminService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        
        // Check authentication
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        String role = (String) session.getAttribute("role");
        if (!"ADMIN".equals(role)) {
            response.sendRedirect(request.getContextPath() + "/home");
            return;
        }
        
        // Set menu items
        MenuHelper.setMenuItems(request, "ADMIN", "/admin/statistics");
        
        // Load statistics
        long totalBuyers = adminService.countBuyers();
        long totalSellers = adminService.countSellers();
        long totalShippers = adminService.countShippers();
        long totalOrders = adminService.countOrders();
        long totalProducts = adminService.countProducts();
        long totalUsers = totalBuyers + totalSellers + totalShippers;
        
        request.setAttribute("totalBuyers", totalBuyers);
        request.setAttribute("totalSellers", totalSellers);
        request.setAttribute("totalShippers", totalShippers);
        request.setAttribute("totalOrders", totalOrders);
        request.setAttribute("totalProducts", totalProducts);
        request.setAttribute("totalUsers", totalUsers);
        
        request.getRequestDispatcher("/admin/admin_statistics.jsp").forward(request, response);
    }
}
