package com.ecommerce.servlet;

import java.io.IOException;
import java.util.ArrayList;
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

@WebServlet(name = "OrderHistoryServlet", urlPatterns = {"/orders"})
public class OrderHistoryServlet extends HttpServlet {

    private final OrderService orderService = new OrderService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Check authentication
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        User user = (User) session.getAttribute("user");
        String role = (String) session.getAttribute("role");
        
        // Set menu items using MenuHelper
        MenuHelper.setMenuItems(request, role, "/orders");
        
        try {
            // Fetch orders
            List<Order> orders = orderService.getOrdersByBuyer(user.getUserId());
            
            request.setAttribute("orders", orders);
            
            // Forward to JSP
            request.getRequestDispatcher("/buyer/ordersBuyer.jsp").forward(request, response);
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Không thể tải danh sách đơn hàng");
            request.setAttribute("orders", new ArrayList<>());
            request.getRequestDispatcher("/buyer/ordersBuyer.jsp").forward(request, response);
        }
    }
}
