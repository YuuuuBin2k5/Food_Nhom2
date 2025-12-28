package com.ecommerce.servlet;

import com.ecommerce.service.CartService;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
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
 * Cart operations servlet (returns JSON for AJAX)
 * Endpoints:
 * - POST /api/cart/add
 * - POST /api/cart/update
 * - DELETE /api/cart/remove/{productId}
 * - DELETE /api/cart/clear
 */
@WebServlet("/api/cart/*")
public class CartServlet extends HttpServlet {
    
    private final CartService cartService = new CartService();
    private final Gson gson = new Gson();
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        // Check authentication
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            sendJsonResponse(response, false, "Chưa đăng nhập", 401);
            return;
        }
        
        String pathInfo = request.getPathInfo();
        
        if ("/update".equals(pathInfo)) {
            updateCart(request, response, session);
        } else if ("/add".equals(pathInfo)) {
            addToCart(request, response, session);
        } else {
            sendJsonResponse(response, false, "Endpoint không hợp lệ", 404);
        }
    }
    
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        // Check authentication
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            sendJsonResponse(response, false, "Chưa đăng nhập", 401);
            return;
        }
        
        String pathInfo = request.getPathInfo();
        
        if (pathInfo != null && pathInfo.startsWith("/remove/")) {
            String productIdStr = pathInfo.substring("/remove/".length());
            removeFromCart(request, response, session, productIdStr);
        } else if ("/clear".equals(pathInfo)) {
            clearCart(request, response, session);
        } else {
            sendJsonResponse(response, false, "Endpoint không hợp lệ", 404);
        }
    }
    
    private void addToCart(HttpServletRequest request, HttpServletResponse response, HttpSession session) 
            throws IOException {
        try {
            JsonObject jsonRequest = gson.fromJson(request.getReader(), JsonObject.class);
            Long productId = jsonRequest.get("productId").getAsLong();
            int quantity = jsonRequest.has("quantity") ? jsonRequest.get("quantity").getAsInt() : 1;
            
            cartService.addToCart(session, productId, quantity);
            
            int cartCount = cartService.getCartCount(session);
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "Đã thêm vào giỏ hàng");
            result.put("cartCount", cartCount);
            
            response.setStatus(200);
            response.getWriter().write(gson.toJson(result));
            
        } catch (IllegalArgumentException e) {
            sendJsonResponse(response, false, e.getMessage(), 400);
        } catch (Exception e) {
            e.printStackTrace();
            sendJsonResponse(response, false, e.getMessage(), 500);
        }
    }
    
    private void updateCart(HttpServletRequest request, HttpServletResponse response, HttpSession session) 
            throws IOException {
        try {
            JsonObject jsonRequest = gson.fromJson(request.getReader(), JsonObject.class);
            Long productId = jsonRequest.get("productId").getAsLong();
            int quantity = jsonRequest.get("quantity").getAsInt();
            
            cartService.updateCartItem(session, productId, quantity);
            
            int cartCount = cartService.getCartCount(session);
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "Đã cập nhật giỏ hàng");
            result.put("cartCount", cartCount);
            
            response.setStatus(200);
            response.getWriter().write(gson.toJson(result));
            
        } catch (IllegalArgumentException e) {
            sendJsonResponse(response, false, e.getMessage(), 400);
        } catch (Exception e) {
            e.printStackTrace();
            sendJsonResponse(response, false, e.getMessage(), 500);
        }
    }
    
    private void removeFromCart(HttpServletRequest request, HttpServletResponse response, 
                                HttpSession session, String productIdStr) 
            throws IOException {
        try {
            Long productId = Long.parseLong(productIdStr);
            
            cartService.removeFromCart(session, productId);
            
            int cartCount = cartService.getCartCount(session);
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "Đã xóa sản phẩm");
            result.put("cartCount", cartCount);
            
            response.setStatus(200);
            response.getWriter().write(gson.toJson(result));
            
        } catch (NumberFormatException e) {
            sendJsonResponse(response, false, "Product ID không hợp lệ", 400);
        } catch (Exception e) {
            e.printStackTrace();
            sendJsonResponse(response, false, e.getMessage(), 500);
        }
    }
    
    private void clearCart(HttpServletRequest request, HttpServletResponse response, HttpSession session) 
            throws IOException {
        try {
            cartService.clearCart(session);
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "Đã xóa giỏ hàng");
            result.put("cartCount", 0);
            
            response.setStatus(200);
            response.getWriter().write(gson.toJson(result));
            
        } catch (Exception e) {
            e.printStackTrace();
            sendJsonResponse(response, false, "Lỗi xóa giỏ hàng", 500);
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
