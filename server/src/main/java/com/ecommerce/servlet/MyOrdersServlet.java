package com.ecommerce.servlet;

import com.ecommerce.entity.Order;
import com.ecommerce.service.OrderService;
import com.ecommerce.util.MenuHelper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

/**
 * Display buyer's order history
 * Shows all orders placed by the logged-in buyer
 */
@WebServlet(name = "MyOrdersServlet", urlPatterns = {"/orders", "/my-orders"})
public class MyOrdersServlet extends HttpServlet {

    private final OrderService orderService = new OrderService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        
        // Authentication check
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login?redirect=orders");
            return;
        }
        
        String userId = (String) session.getAttribute("userId");
        if (userId == null) {
            response.sendRedirect(request.getContextPath() + "/login?redirect=orders");
            return;
        }

        try {
            // Fetch orders for this buyer
            List<Order> orders = orderService.getOrdersByBuyer(userId);
            
            // Set attributes for JSP
            request.setAttribute("orders", orders);
            request.setAttribute("orderCount", orders.size());
            
            // Set menu items
            MenuHelper.setMenuItems(request, "BUYER", "/orders");
            
            request.getRequestDispatcher("/buyer/orders.jsp").forward(request, response);
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Không thể tải danh sách đơn hàng: " + e.getMessage());
            request.setAttribute("orders", List.of());
            request.getRequestDispatcher("/buyer/orders.jsp").forward(request, response);
        }
    }
}
