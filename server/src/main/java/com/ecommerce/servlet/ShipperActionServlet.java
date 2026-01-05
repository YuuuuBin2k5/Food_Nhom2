package com.ecommerce.servlet;

import java.io.IOException;

import com.ecommerce.entity.OrderStatus;
import com.ecommerce.entity.User;
import com.ecommerce.service.OrderService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Shipper Action Servlet - Xử lý các hành động của shipper (JSP/Form based)
 * 
 * POST /shipper/action?action=accept&orderId=123  : Nhận đơn hàng
 * POST /shipper/action?action=complete&orderId=123: Hoàn thành giao hàng
 */
@WebServlet("/shipper/action")
public class ShipperActionServlet extends HttpServlet {
    
    private final OrderService orderService = new OrderService();
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Validate shipper session
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
        
        // Get parameters
        String action = request.getParameter("action");
        String orderIdStr = request.getParameter("orderId");
        
        if (action == null || orderIdStr == null) {
            session.setAttribute("errorMessage", "Thiếu thông tin yêu cầu");
            response.sendRedirect(request.getContextPath() + "/shipper/orders");
            return;
        }
        
        try {
            Long orderId = Long.parseLong(orderIdStr);
            
            switch (action) {
                case "accept":
                    handleAcceptOrder(orderId, shipperId, session, response, request);
                    break;
                    
                case "complete":
                    handleCompleteOrder(orderId, shipperId, session, response, request);
                    break;
                    
                default:
                    session.setAttribute("errorMessage", "Hành động không hợp lệ");
                    response.sendRedirect(request.getContextPath() + "/shipper/orders");
            }
            
        } catch (NumberFormatException e) {
            session.setAttribute("errorMessage", "Mã đơn hàng không hợp lệ");
            response.sendRedirect(request.getContextPath() + "/shipper/orders");
        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("errorMessage", "Lỗi: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/shipper/orders");
        }
    }
    
    private void handleAcceptOrder(Long orderId, String shipperId, HttpSession session, 
            HttpServletResponse response, HttpServletRequest request) throws Exception, IOException {
        
        orderService.updateOrderStatus(orderId, OrderStatus.SHIPPING, shipperId);
        session.setAttribute("successMessage", "Nhận đơn thành công! Bắt đầu giao hàng.");
        response.sendRedirect(request.getContextPath() + "/shipper/delivering");
    }
    
    private void handleCompleteOrder(Long orderId, String shipperId, HttpSession session, 
            HttpServletResponse response, HttpServletRequest request) throws Exception, IOException {
        
        orderService.updateOrderStatus(orderId, OrderStatus.DELIVERED, shipperId);
        session.setAttribute("successMessage", "Giao hàng thành công! 🎉");
        response.sendRedirect(request.getContextPath() + "/shipper/orders");
    }
}
