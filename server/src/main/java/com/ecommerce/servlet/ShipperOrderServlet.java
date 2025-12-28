package com.ecommerce.servlet;

import java.io.IOException;
import com.ecommerce.entity.Order;
import com.ecommerce.entity.OrderStatus;
import com.ecommerce.entity.User;
import com.ecommerce.service.OrderService;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/api/shipper/orders/*")
public class ShipperOrderServlet extends HttpServlet {
    
    private final OrderService orderService = new OrderService();
    private final Gson gson = new Gson();
    
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String method = request.getMethod();
        if ("PATCH".equalsIgnoreCase(method)) {
            doPatch(request, response);
        } else {
            super.service(request, response);
        }
    }
    
    protected void doPatch(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        // Check authentication
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write(gson.toJson(new ErrorResponse("Vui lòng đăng nhập")));
            return;
        }
        
        String role = (String) session.getAttribute("role");
        if (!"SHIPPER".equals(role)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write(gson.toJson(new ErrorResponse("Không có quyền truy cập")));
            return;
        }
        
        User shipper = (User) session.getAttribute("user");
        
        try {
            String pathInfo = request.getPathInfo();
            if (pathInfo == null || pathInfo.equals("/")) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(gson.toJson(new ErrorResponse("Thiếu order ID")));
                return;
            }
            
            // Parse path: /22/accept or /22/complete
            String[] pathParts = pathInfo.split("/");
            if (pathParts.length < 3) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(gson.toJson(new ErrorResponse("URL không hợp lệ")));
                return;
            }
            
            String orderIdStr = pathParts[1];
            String action = pathParts[2];
            
            Long orderId = Long.parseLong(orderIdStr);
            
            // Get order
            Order order = orderService.getOrderById(orderId);
            if (order == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write(gson.toJson(new ErrorResponse("Không tìm thấy đơn hàng")));
                return;
            }
            
            // Handle action
            if ("accept".equals(action)) {
                // Accept order (CONFIRMED -> SHIPPING)
                if (order.getStatus() != OrderStatus.CONFIRMED) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.getWriter().write(gson.toJson(new ErrorResponse("Đơn hàng không ở trạng thái chờ giao")));
                    return;
                }
                
                orderService.updateOrderStatus(orderId, OrderStatus.SHIPPING, shipper.getUserId());
                response.getWriter().write(gson.toJson(new SuccessResponse("Nhận đơn thành công")));
                
            } else if ("complete".equals(action)) {
                // Complete order (SHIPPING -> DELIVERED)
                if (order.getStatus() != OrderStatus.SHIPPING) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.getWriter().write(gson.toJson(new ErrorResponse("Đơn hàng không ở trạng thái đang giao")));
                    return;
                }
                
                // Verify shipper owns this order
                if (order.getShipper() == null || !order.getShipper().getUserId().equals(shipper.getUserId())) {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.getWriter().write(gson.toJson(new ErrorResponse("Bạn không phải shipper của đơn hàng này")));
                    return;
                }
                
                orderService.updateOrderStatus(orderId, OrderStatus.DELIVERED, shipper.getUserId());
                response.getWriter().write(gson.toJson(new SuccessResponse("Xác nhận giao hàng thành công")));
                
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(gson.toJson(new ErrorResponse("Action không hợp lệ")));
            }
            
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(gson.toJson(new ErrorResponse("Order ID không hợp lệ")));
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(gson.toJson(new ErrorResponse("Có lỗi xảy ra: " + e.getMessage())));
        }
    }
    
    // Response classes
    private static class ErrorResponse {
        private final String error;
        
        public ErrorResponse(String error) {
            this.error = error;
        }
        
        public String getError() {
            return error;
        }
    }
    
    private static class SuccessResponse {
        private final String message;
        
        public SuccessResponse(String message) {
            this.message = message;
        }
        
        public String getMessage() {
            return message;
        }
    }
}
