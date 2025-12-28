package com.ecommerce.servlet;

import com.ecommerce.entity.Order;
import com.ecommerce.entity.OrderStatus;
import com.ecommerce.service.OrderService;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Buyer order operations servlet (returns JSON for AJAX)
 * Endpoints:
 * - GET /api/buyer/orders/{orderId}
 * - PUT /api/buyer/orders/{orderId}/cancel
 */
@WebServlet("/api/buyer/orders/*")
public class BuyerOrderServlet extends HttpServlet {
    
    private final OrderService orderService = new OrderService();
    private final Gson gson = new Gson();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        // Check authentication
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            sendJsonResponse(response, false, "Chưa đăng nhập", 401);
            return;
        }
        
        String userId = (String) session.getAttribute("userId");
        String role = (String) session.getAttribute("role");
        
        if (!"BUYER".equals(role)) {
            sendJsonResponse(response, false, "Không có quyền", 403);
            return;
        }
        
        try {
            String pathInfo = request.getPathInfo();
            if (pathInfo == null || pathInfo.length() <= 1) {
                sendJsonResponse(response, false, "URL không hợp lệ", 400);
                return;
            }
            
            // Parse orderId from URL
            String orderIdStr = pathInfo.substring(1);
            Long orderId = Long.parseLong(orderIdStr);
            
            Order order = orderService.getOrderById(orderId);
            
            // Verify buyer owns this order
            if (!order.getBuyer().getUserId().equals(userId)) {
                sendJsonResponse(response, false, "Không có quyền xem đơn hàng này", 403);
                return;
            }
            
            // Return order data
            response.setStatus(200);
            response.getWriter().write(gson.toJson(order));
            
        } catch (NumberFormatException e) {
            sendJsonResponse(response, false, "Order ID không hợp lệ", 400);
        } catch (Exception e) {
            e.printStackTrace();
            sendJsonResponse(response, false, "Lỗi: " + e.getMessage(), 500);
        }
    }
    
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        // Check authentication
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            sendJsonResponse(response, false, "Chưa đăng nhập", 401);
            return;
        }
        
        String userId = (String) session.getAttribute("userId");
        String role = (String) session.getAttribute("role");
        
        if (!"BUYER".equals(role)) {
            sendJsonResponse(response, false, "Không có quyền", 403);
            return;
        }
        
        try {
            // Parse URL: /api/buyer/orders/123/cancel
            String pathInfo = request.getPathInfo();
            if (pathInfo == null || pathInfo.length() <= 1) {
                sendJsonResponse(response, false, "URL không hợp lệ", 400);
                return;
            }
            
            String[] parts = pathInfo.substring(1).split("/");
            if (parts.length < 2) {
                sendJsonResponse(response, false, "URL không hợp lệ", 400);
                return;
            }
            
            String orderIdStr = parts[0];
            String action = parts[1];
            
            if (!"cancel".equals(action)) {
                sendJsonResponse(response, false, "Action không hợp lệ", 400);
                return;
            }
            
            Long orderId = Long.parseLong(orderIdStr);
            
            // Get order and verify ownership
            Order order = orderService.getOrderById(orderId);
            if (!order.getBuyer().getUserId().equals(userId)) {
                sendJsonResponse(response, false, "Không có quyền hủy đơn hàng này", 403);
                return;
            }
            
            // Check if order can be cancelled
            if (order.getStatus() == OrderStatus.CANCELLED) {
                sendJsonResponse(response, false, "Đơn hàng đã bị hủy", 400);
                return;
            }
            
            if (order.getStatus() == OrderStatus.DELIVERED) {
                sendJsonResponse(response, false, "Không thể hủy đơn hàng đã giao", 400);
                return;
            }
            
            // Cancel order
            orderService.updateOrderStatus(orderId, OrderStatus.CANCELLED);
            
            sendJsonResponse(response, true, "Đã hủy đơn hàng thành công", 200);
            
        } catch (NumberFormatException e) {
            sendJsonResponse(response, false, "Order ID không hợp lệ", 400);
        } catch (Exception e) {
            e.printStackTrace();
            sendJsonResponse(response, false, "Lỗi: " + e.getMessage(), 500);
        }
    }
    
    private void sendJsonResponse(HttpServletResponse response, boolean success, String message, int status) 
            throws IOException {
        response.setStatus(status);
        Map<String, Object> result = new HashMap<>();
        result.put("success", success);
        result.put("message", message);
        response.getWriter().write(gson.toJson(result));
    }
}
