package com.ecommerce.servlet;

import com.ecommerce.dto.CartItemDTO;
import com.ecommerce.dto.ProductDTO;
import com.ecommerce.service.ProductService;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * REST API for cart operations (AJAX requests)
 * Endpoints:
 * - POST /api/cart/add - Add product to cart
 * - POST /api/cart/update - Update item quantity
 * - POST /api/cart/remove - Remove item from cart
 * - POST /api/cart/clear - Clear all items
 * - GET /api/cart/items - Get all cart items
 */
@WebServlet(name = "CartAPIServlet", urlPatterns = {"/api/cart/*"})
public class CartAPIServlet extends HttpServlet {

    private final ProductService productService = new ProductService();
    private final Gson gson = new Gson();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        // Authentication check
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            sendError(response, 401, "Vui lòng đăng nhập");
            return;
        }
        
        String pathInfo = request.getPathInfo();
        if (pathInfo == null) {
            sendError(response, 400, "Invalid request");
            return;
        }

        List<CartItemDTO> cart = getOrCreateCart(session);

        try {
            switch (pathInfo) {
                case "/add":
                    handleAddToCart(request, response, cart, session);
                    break;
                case "/update":
                    handleUpdateCart(request, response, cart, session);
                    break;
                case "/remove":
                    handleRemoveFromCart(request, response, cart, session);
                    break;
                case "/clear":
                    cart.clear();
                    session.setAttribute("cart", cart);
                    updateCartSize(session, cart);
                    sendSuccessWithData(response, "Đã xóa giỏ hàng", Map.of("cartSize", 0));
                    break;
                default:
                    sendError(response, 404, "Endpoint not found");
            }
        } catch (IllegalArgumentException e) {
            sendError(response, 400, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            sendError(response, 500, "Lỗi server: " + e.getMessage());
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        // Authentication check
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            sendError(response, 401, "Vui lòng đăng nhập");
            return;
        }
        
        String pathInfo = request.getPathInfo();
        if (pathInfo == null) {
            sendError(response, 400, "Invalid request");
            return;
        }

        List<CartItemDTO> cart = getOrCreateCart(session);

        try {
            // Handle /remove/{productId} or /clear
            if (pathInfo.equals("/clear")) {
                cart.clear();
                session.setAttribute("cart", cart);
                updateCartSize(session, cart);
                sendSuccessWithData(response, "Đã xóa giỏ hàng", Map.of("cartSize", 0));
            } else if (pathInfo.startsWith("/remove/")) {
                // Extract productId from path: /remove/123
                String productIdStr = pathInfo.substring("/remove/".length());
                Long productId = Long.parseLong(productIdStr);
                handleRemoveFromCartInternal(productId, cart, session, response);
            } else {
                sendError(response, 404, "Endpoint not found");
            }
        } catch (NumberFormatException e) {
            sendError(response, 400, "Invalid product ID");
        } catch (Exception e) {
            e.printStackTrace();
            sendError(response, 500, "Lỗi server: " + e.getMessage());
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        // Authentication check
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            sendError(response, 401, "Vui lòng đăng nhập");
            return;
        }
        
        String pathInfo = request.getPathInfo();
        if (pathInfo == null || !pathInfo.equals("/items")) {
            sendError(response, 400, "Invalid request");
            return;
        }

        List<CartItemDTO> cart = getOrCreateCart(session);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("items", cart);
        result.put("totalItems", cart.stream().mapToInt(CartItemDTO::getQuantity).sum());
        
        response.getWriter().write(gson.toJson(result));
    }

    /**
     * Handle add to cart request
     */
    private void handleAddToCart(HttpServletRequest request, HttpServletResponse response, 
                                  List<CartItemDTO> cart, HttpSession session) throws Exception {
        
        // Parse JSON request body
        Map<String, Object> data = parseJsonBody(request);
        
        Long productId = parseLong(data.get("productId"));
        int quantity = parseInt(data.get("quantity"));
        
        if (quantity <= 0) {
            throw new IllegalArgumentException("Số lượng phải lớn hơn 0");
        }

        // Check if item already exists
        boolean found = false;
        for (CartItemDTO item : cart) {
            if (item.getProduct().getProductId().equals(productId)) {
                int newQuantity = item.getQuantity() + quantity;
                
                // Validate stock
                if (item.getProduct().getQuantity() < newQuantity) {
                    throw new IllegalArgumentException("Không đủ hàng trong kho (Còn: " + item.getProduct().getQuantity() + ")");
                }
                
                item.setQuantity(newQuantity);
                found = true;
                break;
            }
        }
        
        // Add new item if not found
        if (!found) {
            ProductDTO product = productService.getProductById(productId);
            if (product == null) {
                throw new IllegalArgumentException("Sản phẩm không tồn tại");
            }
            
            // Validate stock
            if (product.getQuantity() < quantity) {
                throw new IllegalArgumentException("Không đủ hàng trong kho (Còn: " + product.getQuantity() + ")");
            }
            
            cart.add(new CartItemDTO(product, quantity));
        }
        
        // Save to session
        session.setAttribute("cart", cart);
        updateCartSize(session, cart);
        
        sendSuccessWithData(response, "Đã thêm vào giỏ hàng", 
            Map.of("cartSize", session.getAttribute("cartSize")));
    }

    /**
     * Handle update cart request
     */
    private void handleUpdateCart(HttpServletRequest request, HttpServletResponse response,
                                   List<CartItemDTO> cart, HttpSession session) throws Exception {
        
        Map<String, Object> data = parseJsonBody(request);
        
        Long productId = parseLong(data.get("productId"));
        int quantity = parseInt(data.get("quantity"));
        
        if (quantity <= 0) {
            // If quantity is 0 or negative, remove item
            handleRemoveFromCartInternal(productId, cart, session, response);
            return;
        }

        boolean found = false;
        for (CartItemDTO item : cart) {
            if (item.getProduct().getProductId().equals(productId)) {
                // Validate stock
                if (item.getProduct().getQuantity() < quantity) {
                    throw new IllegalArgumentException("Không đủ hàng trong kho (Còn: " + item.getProduct().getQuantity() + ")");
                }
                
                item.setQuantity(quantity);
                found = true;
                break;
            }
        }
        
        if (!found) {
            throw new IllegalArgumentException("Sản phẩm không có trong giỏ hàng");
        }
        
        session.setAttribute("cart", cart);
        updateCartSize(session, cart);
        sendSuccessWithData(response, "Đã cập nhật giỏ hàng",
            Map.of("cartSize", session.getAttribute("cartSize")));
    }

    /**
     * Handle remove from cart request
     */
    private void handleRemoveFromCart(HttpServletRequest request, HttpServletResponse response,
                                      List<CartItemDTO> cart, HttpSession session) throws Exception {
        
        Map<String, Object> data = parseJsonBody(request);
        Long productId = parseLong(data.get("productId"));
        
        handleRemoveFromCartInternal(productId, cart, session, response);
    }

    /**
     * Internal method to remove item from cart
     */
    private void handleRemoveFromCartInternal(Long productId, List<CartItemDTO> cart, 
                                              HttpSession session, HttpServletResponse response) 
            throws IOException {
        
        Iterator<CartItemDTO> iterator = cart.iterator();
        boolean removed = false;
        
        while (iterator.hasNext()) {
            CartItemDTO item = iterator.next();
            if (item.getProduct().getProductId().equals(productId)) {
                iterator.remove();
                removed = true;
                break;
            }
        }
        
        if (!removed) {
            sendError(response, 404, "Sản phẩm không có trong giỏ hàng");
            return;
        }
        
        session.setAttribute("cart", cart);
        updateCartSize(session, cart);
        sendSuccessWithData(response, "Đã xóa khỏi giỏ hàng",
            Map.of("cartSize", session.getAttribute("cartSize")));
    }

    /**
     * Parse JSON request body
     */
    @SuppressWarnings("unchecked")
    private Map<String, Object> parseJsonBody(HttpServletRequest request) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = request.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }
        return gson.fromJson(sb.toString(), Map.class);
    }

    /**
     * Safely parse Long from JSON (Gson may return Double)
     */
    private Long parseLong(Object value) {
        if (value == null) {
            throw new IllegalArgumentException("Missing required parameter");
        }
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        return Long.parseLong(String.valueOf(value));
    }

    /**
     * Safely parse Integer from JSON (Gson may return Double)
     */
    private int parseInt(Object value) {
        if (value == null) {
            throw new IllegalArgumentException("Missing required parameter");
        }
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        return Integer.parseInt(String.valueOf(value));
    }

    /**
     * Get cart from session or create new one
     */
    @SuppressWarnings("unchecked")
    private List<CartItemDTO> getOrCreateCart(HttpSession session) {
        List<CartItemDTO> cart = (List<CartItemDTO>) session.getAttribute("cart");
        if (cart == null) {
            cart = new ArrayList<>();
            session.setAttribute("cart", cart);
        }
        return cart;
    }

    /**
     * Update cart size in session
     */
    private void updateCartSize(HttpSession session, List<CartItemDTO> cart) {
        int totalItems = cart.stream()
            .mapToInt(CartItemDTO::getQuantity)
            .sum();
        session.setAttribute("cartSize", totalItems);
    }

    /**
     * Send success response with data
     */
    private void sendSuccessWithData(HttpServletResponse response, String message, Map<String, Object> data) 
            throws IOException {
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", message);
        result.putAll(data);
        response.getWriter().write(gson.toJson(result));
    }

    /**
     * Send error response
     */
    private void sendError(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        Map<String, Object> error = new HashMap<>();
        error.put("success", false);
        error.put("message", message);
        response.getWriter().write(gson.toJson(error));
    }
}
