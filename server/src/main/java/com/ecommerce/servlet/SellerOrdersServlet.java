package com.ecommerce.servlet;

import java.io.IOException;
import java.util.List;

import com.ecommerce.entity.Order;
import com.ecommerce.entity.User;
import com.ecommerce.service.OrderService;
import com.ecommerce.util.MenuHelper;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/seller/orders")
public class SellerOrdersServlet extends HttpServlet {
    
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
        if (!"SELLER".equals(role)) {
            response.sendRedirect(request.getContextPath() + "/home");
            return;
        }
        
        User user = (User) session.getAttribute("user");
        
        // Set menu items
        MenuHelper.setMenuItems(request, "SELLER", "/seller/orders");
        
        try {
            String sellerId = user.getUserId();
            List<Order> orders = orderService.getOrdersBySellerProducts(sellerId);
            
            request.setAttribute("orders", orders);
            request.getRequestDispatcher("/seller/orders.jsp").forward(request, response);
            
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Lỗi tải danh sách đơn hàng");
        }
    }
}
