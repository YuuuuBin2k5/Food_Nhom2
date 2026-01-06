package com.ecommerce.servlet.buyer;

import com.ecommerce.entity.Order;
import com.ecommerce.entity.OrderStatus;
import com.ecommerce.entity.UserLog;
import com.ecommerce.entity.ActionType;
import com.ecommerce.entity.Role;
import com.ecommerce.service.OrderService;
import com.ecommerce.service.UserLogService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(name = "CancelOrderServlet", urlPatterns = {"/order-cancel"})
public class CancelOrderServlet extends HttpServlet {

    private final OrderService orderService = new OrderService();
    private final UserLogService userLogService = new UserLogService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        String userId = (String) session.getAttribute("userId");
        
        if (userId == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        String orderIdStr = request.getParameter("orderId");
        if (orderIdStr == null || orderIdStr.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/my-orders?error=Invalid Order ID");
            return;
        }
        
        try {
            Long orderId = Long.parseLong(orderIdStr);
            Order order = orderService.getOrderById(orderId);
            
            // Security check
            if (!order.getBuyer().getUserId().equals(userId)) {
                response.sendRedirect(request.getContextPath() + "/my-orders?error=Access Denied");
                return;
            }
            
            if (order.getStatus() != OrderStatus.PENDING) {
                response.sendRedirect(request.getContextPath() + "/my-orders?error=Cannot cancel order in " + order.getStatus() + " status");
                return;
            }
            
            orderService.updateOrderStatus(orderId, OrderStatus.CANCELLED);
            
            // Tạo log cho BUYER_CANCEL_ORDER
            UserLog log = new UserLog(userId, Role.BUYER, ActionType.BUYER_CANCEL_ORDER,
                "Buyer hủy đơn hàng #" + orderId, orderId.toString(), "ORDER", null);
            userLogService.save(log);
            
            response.sendRedirect(request.getContextPath() + "/my-orders?message=Order cancelled successfully");
            
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/my-orders?error=" + e.getMessage());
        }
    }
}
