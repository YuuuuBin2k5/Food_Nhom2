package com.ecommerce.servlet;

import java.io.IOException;
import java.util.List;

import com.ecommerce.entity.Order;
import com.ecommerce.entity.OrderStatus;
import com.ecommerce.entity.User;
import com.ecommerce.service.OrderService;
import com.ecommerce.util.MenuHelper;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/shipper/earnings")
public class ShipperEarningsServlet extends HttpServlet {
    
    private OrderService orderService = new OrderService();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        String role = (String) session.getAttribute("role");
        if (!"SHIPPER".equals(role)) {
            response.sendRedirect(request.getContextPath() + "/home");
            return;
        }
        
        User user = (User) session.getAttribute("user");
        String shipperId = user.getUserId();
        
        MenuHelper.setMenuItems(request, "SHIPPER", "/shipper/earnings");
        
        try {
            List<Order> orders = orderService.getOrdersForShipper(shipperId);
            
            // Calculate today's earnings
            long todayTrips = orders.stream()
                .filter(o -> o.getStatus() == OrderStatus.DELIVERED && 
                           shipperId.equals(o.getShipper() != null ? o.getShipper().getUserId() : null))
                .count();
            
            double todayEarnings = todayTrips * 15000.0;
            double weeklyTotal = todayEarnings; // Simplified
            
            request.setAttribute("todayEarnings", todayEarnings);
            request.setAttribute("todayTrips", todayTrips);
            request.setAttribute("weeklyTotal", weeklyTotal);
            request.setAttribute("user", user);
            
            request.getRequestDispatcher("/shipper/earnings.jsp").forward(request, response);
            
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Lỗi tải thông tin thu nhập");
        }
    }
}
