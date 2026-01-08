package com.ecommerce.servlet;

import com.ecommerce.service.AdminService;
import com.ecommerce.util.MenuHelper;
import java.io.IOException;
import java.util.Map;
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
        
        // Load statistics - TỐI ƯU: Dùng 1 EntityManager thay vì 5
        Map<String, Object> stats = adminService.getAllStatistics();
        
        long totalBuyers = (Long) stats.get("buyers");
        long totalSellers = (Long) stats.get("sellers");
        long totalShippers = (Long) stats.get("shippers");
        long totalOrders = (Long) stats.get("orders");
        long totalProducts = (Long) stats.get("products");
        double totalRevenue = (Double) stats.get("revenue");
        long totalUsers = totalBuyers + totalSellers + totalShippers;
        
        System.out.println("AdminStatisticsServlet: Total revenue: " + totalRevenue);
        
        request.setAttribute("totalBuyers", totalBuyers);
        request.setAttribute("totalSellers", totalSellers);
        request.setAttribute("totalShippers", totalShippers);
        request.setAttribute("totalOrders", totalOrders);
        request.setAttribute("totalProducts", totalProducts);
        request.setAttribute("totalRevenue", totalRevenue);
        request.setAttribute("totalUsers", totalUsers);
        
        request.getRequestDispatcher("/admin/admin_statistics.jsp").forward(request, response);
    }
}
