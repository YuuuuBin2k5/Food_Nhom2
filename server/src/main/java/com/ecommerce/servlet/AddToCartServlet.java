package com.ecommerce.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import com.ecommerce.dto.CartItemDTO;
import com.ecommerce.entity.Product;
import com.ecommerce.service.ProductService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "AddToCartServlet", urlPatterns = {"/add-to-cart"})
public class AddToCartServlet extends HttpServlet {

    private final ProductService productService = new ProductService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        // Check authentication
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            sendJsonResponse(response, false, "Vui lòng đăng nhập");
            return;
        }
        
        try {
            // Get product ID from request
            String productIdStr = request.getParameter("productId");
            String quantityStr = request.getParameter("quantity");
            
            if (productIdStr == null || productIdStr.isEmpty()) {
                sendJsonResponse(response, false, "Thiếu thông tin sản phẩm");
                return;
            }
            
            Long productId = Long.parseLong(productIdStr);
            int quantity = (quantityStr != null && !quantityStr.isEmpty()) 
                          ? Integer.parseInt(quantityStr) 
                          : 1;
            
            // Get product info
            Product product = productService.getActiveProductById(productId);
            if (product == null) {
                sendJsonResponse(response, false, "Sản phẩm không tồn tại");
                return;
            }
            
            if (product.getQuantity() < quantity) {
                sendJsonResponse(response, false, "Không đủ hàng trong kho");
                return;
            }
            
            // Get cart from session - now using CartItemDTO
            @SuppressWarnings("unchecked")
            List<CartItemDTO> cart = (List<CartItemDTO>) session.getAttribute("cart");
            if (cart == null) {
                cart = new ArrayList<>();
            }
            
            // Check if product already in cart
            boolean found = false;
            for (CartItemDTO item : cart) {
                if (item.getProduct().getProductId().equals(productId)) {
                    int newQuantity = item.getQuantity() + quantity;
                    if (product.getQuantity() < newQuantity) {
                        sendJsonResponse(response, false, "Không đủ hàng trong kho");
                        return;
                    }
                    item.setQuantity(newQuantity);
                    found = true;
                    break;
                }
            }
            
            if (!found) {
                CartItemDTO newItem = new CartItemDTO(product, quantity);
                cart.add(newItem);
            }
            
            // Save cart to session
            session.setAttribute("cart", cart);
            
            // Calculate total items for response
            int totalItems = cart.stream().mapToInt(CartItemDTO::getQuantity).sum();
            
            sendJsonResponse(response, true, "Đã thêm vào giỏ hàng", totalItems);
            
        } catch (NumberFormatException e) {
            sendJsonResponse(response, false, "Thông tin sản phẩm không hợp lệ", 0);
        } catch (Exception e) {
            e.printStackTrace();
            sendJsonResponse(response, false, "Không thể thêm vào giỏ hàng: " + e.getMessage(), 0);
        }
    }
    
    private void sendJsonResponse(HttpServletResponse response, boolean success, String message) 
            throws IOException {
        sendJsonResponse(response, success, message, 0);
    }
    
    private void sendJsonResponse(HttpServletResponse response, boolean success, String message, int cartCount) 
            throws IOException {
        String json = String.format("{\"success\":%b,\"message\":\"%s\",\"cartCount\":%d}", 
                                   success, 
                                   message.replace("\"", "\\\""),
                                   cartCount);
        PrintWriter out = response.getWriter();
        out.print(json);
        out.flush();
    }
}
