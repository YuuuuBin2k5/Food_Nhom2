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

@WebServlet("/shipper/orders")
public class ShipperOrdersServlet extends HttpServlet {
    
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
        
        // Set menu items
        MenuHelper.setMenuItems(request, "SHIPPER", "/shipper/orders");
        
        try {
            String shipperId = user.getUserId();
            
            // Get all orders for shipper (CONFIRMED, SHIPPING, DELIVERED)
            List<Order> orders = orderService.getOrdersForShipper(shipperId);
            
            // Calculate stats
            long availableOrders = orders.stream()
                .filter(o -> o.getStatus() == OrderStatus.CONFIRMED)
                .count();
            
            long shippingOrders = orders.stream()
                .filter(o -> o.getStatus() == OrderStatus.SHIPPING && 
                           shipperId.equals(o.getShipper() != null ? o.getShipper().getUserId() : null))
                .count();
            
            long deliveredOrders = orders.stream()
                .filter(o -> o.getStatus() == OrderStatus.DELIVERED && 
                           shipperId.equals(o.getShipper() != null ? o.getShipper().getUserId() : null))
                .count();
            
            double totalEarnings = orders.stream()
                .filter(o -> o.getStatus() == OrderStatus.DELIVERED && 
                           shipperId.equals(o.getShipper() != null ? o.getShipper().getUserId() : null))
                .mapToDouble(o -> 15000.0) // Fixed shipping fee
                .sum();
            
            // Filter only CONFIRMED orders for the new orders page
            List<Order> availableOrdersList = orders.stream()
                .filter(o -> o.getStatus() == OrderStatus.CONFIRMED)
                .toList();
            
            request.setAttribute("orders", availableOrdersList);
            request.setAttribute("availableOrders", availableOrders);
            request.setAttribute("shippingOrders", shippingOrders);
            request.setAttribute("deliveredOrders", deliveredOrders);
            request.setAttribute("totalEarnings", totalEarnings);
            request.setAttribute("user", user);
            
            request.getRequestDispatcher("/shipper/orders.jsp").forward(request, response);
            
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Lỗi tải danh sách đơn hàng");
        }
    }
}
