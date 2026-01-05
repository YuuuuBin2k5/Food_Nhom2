package com.ecommerce.servlet;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import com.ecommerce.entity.Order;
import com.ecommerce.entity.OrderStatus;
import com.ecommerce.entity.User;
import com.ecommerce.service.OrderService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Shipper API Servlet - Xử lý tất cả API cho shipper
 * 
 * Endpoints:
 * - GET    /api/shipper/orders              : Lấy danh sách đơn hàng
 * - GET    /api/shipper/orders/{id}         : Lấy chi tiết đơn hàng
 * - PATCH  /api/shipper/orders/{id}/accept  : Nhận đơn hàng
 * - PATCH  /api/shipper/orders/{id}/complete: Hoàn thành giao hàng
 * - GET    /api/shipper/stats               : Thống kê shipper
 */
@WebServlet(name = "ShipperAPIServlet", urlPatterns = {"/api/shipper/*"})
public class ShipperAPIServlet extends HttpServlet {
    
    private final OrderService orderService = new OrderService();
    private final Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create();
    
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        if ("PATCH".equalsIgnoreCase(request.getMethod())) {
            doPatch(request, response);
        } else {
            super.service(request, response);
        }
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        setupResponse(response);
        
        User user = validateShipper(request, response);
        if (user == null) return;
        
        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid endpoint");
            return;
        }
        
        try {
            if (pathInfo.equals("/orders")) {
                handleGetOrders(request, response, user);
            } else if (pathInfo.equals("/stats")) {
                handleGetStats(response, user);
            } else if (pathInfo.matches("/orders/\\d+")) {
                Long orderId = extractOrderId(pathInfo, "/orders/");
                handleGetOrderDetail(response, orderId);
            } else {
                sendError(response, HttpServletResponse.SC_NOT_FOUND, "Endpoint not found");
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
    
    protected void doPatch(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        setupResponse(response);
        
        User user = validateShipper(request, response);
        if (user == null) return;
        
        String pathInfo = request.getPathInfo();
        if (pathInfo == null) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid endpoint");
            return;
        }
        
        try {
            if (pathInfo.matches("/orders/\\d+/accept")) {
                Long orderId = extractOrderId(pathInfo, "/orders/", "/accept");
                handleAcceptOrder(response, orderId, user.getUserId());
            } else if (pathInfo.matches("/orders/\\d+/complete")) {
                Long orderId = extractOrderId(pathInfo, "/orders/", "/complete");
                handleCompleteOrder(response, orderId, user.getUserId());
            } else {
                sendError(response, HttpServletResponse.SC_NOT_FOUND, "Endpoint not found");
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    
    // ==================== HANDLERS ====================
    
    private void handleGetOrders(HttpServletRequest request, HttpServletResponse response, User user) 
            throws Exception, IOException {
        String status = request.getParameter("status");
        List<Order> orders = orderService.getOrdersForShipper(user.getUserId());
        
        if (status != null && !status.isEmpty()) {
            OrderStatus filterStatus = OrderStatus.valueOf(status.toUpperCase());
            orders = orders.stream()
                .filter(o -> o.getStatus() == filterStatus)
                .collect(Collectors.toList());
        }
        
        sendSuccess(response, "orders", orders);
    }
    
    private void handleGetOrderDetail(HttpServletResponse response, Long orderId) 
            throws Exception, IOException {
        Order order = orderService.getOrderById(orderId);
        sendSuccess(response, "order", order);
    }
    
    private void handleGetStats(HttpServletResponse response, User user) throws IOException {
        String shipperId = user.getUserId();
        List<Order> orders = orderService.getOrdersForShipper(shipperId);
        
        long available = orders.stream().filter(o -> o.getStatus() == OrderStatus.CONFIRMED).count();
        long shipping = orders.stream()
            .filter(o -> o.getStatus() == OrderStatus.SHIPPING && 
                        shipperId.equals(o.getShipper() != null ? o.getShipper().getUserId() : null))
            .count();
        long delivered = orders.stream()
            .filter(o -> o.getStatus() == OrderStatus.DELIVERED && 
                        shipperId.equals(o.getShipper() != null ? o.getShipper().getUserId() : null))
            .count();
        double earnings = delivered * 15000.0;
        
        JsonObject stats = new JsonObject();
        stats.addProperty("availableOrders", available);
        stats.addProperty("shippingOrders", shipping);
        stats.addProperty("deliveredOrders", delivered);
        stats.addProperty("totalEarnings", earnings);
        
        sendSuccess(response, "stats", stats);
    }
    
    private void handleAcceptOrder(HttpServletResponse response, Long orderId, String shipperId) 
            throws Exception, IOException {
        orderService.updateOrderStatus(orderId, OrderStatus.SHIPPING, shipperId);
        sendSuccess(response, "Nhận đơn thành công");
    }
    
    private void handleCompleteOrder(HttpServletResponse response, Long orderId, String shipperId) 
            throws Exception, IOException {
        orderService.updateOrderStatus(orderId, OrderStatus.DELIVERED, shipperId);
        sendSuccess(response, "Giao hàng thành công");
    }
    
    // ==================== UTILITIES ====================
    
    private void setupResponse(HttpServletResponse response) {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
    }
    
    private User validateShipper(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            sendError(response, HttpServletResponse.SC_UNAUTHORIZED, "Chưa đăng nhập");
            return null;
        }
        
        String role = (String) session.getAttribute("role");
        if (!"SHIPPER".equals(role)) {
            sendError(response, HttpServletResponse.SC_FORBIDDEN, "Không có quyền truy cập");
            return null;
        }
        
        return (User) session.getAttribute("user");
    }
    
    private Long extractOrderId(String path, String prefix) {
        return Long.parseLong(path.replace(prefix, ""));
    }
    
    private Long extractOrderId(String path, String prefix, String suffix) {
        return Long.parseLong(path.replace(prefix, "").replace(suffix, ""));
    }
    
    private void sendSuccess(HttpServletResponse response, String message) throws IOException {
        JsonObject json = new JsonObject();
        json.addProperty("success", true);
        json.addProperty("message", message);
        response.getWriter().write(json.toString());
    }
    
    private void sendSuccess(HttpServletResponse response, String key, Object data) throws IOException {
        JsonObject json = new JsonObject();
        json.addProperty("success", true);
        json.add(key, gson.toJsonTree(data));
        response.getWriter().write(json.toString());
    }
    
    private void sendError(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        JsonObject json = new JsonObject();
        json.addProperty("success", false);
        json.addProperty("message", message);
        response.getWriter().write(json.toString());
    }
}
