package com.ecommerce.servlet.buyer;

import com.ecommerce.entity.Order;
import com.ecommerce.service.OrderService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "MyOrdersServlet", urlPatterns = {"/my-orders"})
public class MyOrdersServlet extends HttpServlet {

    private final OrderService orderService = new OrderService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        String userId = (String) session.getAttribute("userId");
        
        if (userId == null) {
            response.sendRedirect(request.getContextPath() + "/login?redirect=my-orders");
            return;
        }
        
        // Check for cancellation message
        String message = request.getParameter("message");
        if (message != null) {
             request.setAttribute("message", message);
        }

        try {
            List<Order> orders = orderService.getOrdersByBuyer(userId);
            request.setAttribute("orders", orders);
            request.getRequestDispatcher("/buyer/orders.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error loading orders");
        }
    }
}
