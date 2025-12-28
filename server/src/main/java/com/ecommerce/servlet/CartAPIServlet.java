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

@WebServlet(name = "CartAPIServlet", urlPatterns = {"/api/cart/*"})
public class CartAPIServlet extends HttpServlet {

    private final ProductService productService = new ProductService();
    private final Gson gson = new Gson();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        String pathInfo = request.getPathInfo();
        if (pathInfo == null) {
            sendError(response, 400, "Invalid request");
            return;
        }

        HttpSession session = request.getSession();
        List<CartItemDTO> cart = (List<CartItemDTO>) session.getAttribute("cart");
        if (cart == null) {
            cart = new ArrayList<>();
            session.setAttribute("cart", cart);
        }

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
                    sendSuccess(response, "Đã xóa giỏ hàng");
                    break;
                default:
                    sendError(response, 404, "Endpoint not found");
            }
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
        
        String pathInfo = request.getPathInfo();
        if (pathInfo == null || !pathInfo.equals("/items")) {
            sendError(response, 400, "Invalid request");
            return;
        }

        HttpSession session = request.getSession();
        List<CartItemDTO> cart = (List<CartItemDTO>) session.getAttribute("cart");
        if (cart == null) {
            cart = new ArrayList<>();
        }

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("items", cart);
        result.put("totalItems", cart.stream().mapToInt(CartItemDTO::getQuantity).sum());
        
        response.getWriter().write(gson.toJson(result));
    }

    private void handleAddToCart(HttpServletRequest request, HttpServletResponse response, 
                                  List<CartItemDTO> cart, HttpSession session) throws Exception {
        // Read JSON body
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = request.getReader();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        
        Map<String, Object> data = gson.fromJson(sb.toString(), Map.class);
        Long productId = ((Number) data.get("productId")).longValue();
        int quantity = ((Number) data.get("quantity")).intValue();
        
        // Check if item exists
        boolean found = false;
        for (CartItemDTO item : cart) {
            if (item.getProduct().getProductId().equals(productId)) {
                item.setQuantity(item.getQuantity() + quantity);
                found = true;
                break;
            }
        }
        
        if (!found) {
            ProductDTO product = productService.getProductById(productId);
            if (product != null) {
                cart.add(new CartItemDTO(product, quantity));
            } else {
                sendError(response, 404, "Không tìm thấy sản phẩm");
                return;
            }
        }
        
        // Lưu cart vào session
        session.setAttribute("cart", cart);
        updateCartSize(session, cart);
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "Đã thêm vào giỏ hàng");
        result.put("cartSize", session.getAttribute("cartSize"));
        
        response.getWriter().write(gson.toJson(result));
    }

    private void handleUpdateCart(HttpServletRequest request, HttpServletResponse response,
                                   List<CartItemDTO> cart, HttpSession session) throws Exception {
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = request.getReader();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        
        Map<String, Object> data = gson.fromJson(sb.toString(), Map.class);
        Long productId = ((Number) data.get("productId")).longValue();
        int quantity = ((Number) data.get("quantity")).intValue();
        
        if (quantity <= 0) {
            handleRemoveFromCart(request, response, cart, session);
            return;
        }

        for (CartItemDTO item : cart) {
            if (item.getProduct().getProductId().equals(productId)) {
                item.setQuantity(quantity);
                break;
            }
        }
        
        // Lưu cart vào session
        session.setAttribute("cart", cart);
        updateCartSize(session, cart);
        sendSuccess(response, "Đã cập nhật giỏ hàng");
    }

    private void handleRemoveFromCart(HttpServletRequest request, HttpServletResponse response,
                                      List<CartItemDTO> cart, HttpSession session) throws Exception {
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = request.getReader();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        
        Map<String, Object> data = gson.fromJson(sb.toString(), Map.class);
        Long productId = ((Number) data.get("productId")).longValue();
        
        Iterator<CartItemDTO> iterator = cart.iterator();
        while (iterator.hasNext()) {
            CartItemDTO item = iterator.next();
            if (item.getProduct().getProductId().equals(productId)) {
                iterator.remove();
                break;
            }
        }
        
        // Lưu cart vào session
        session.setAttribute("cart", cart);
        updateCartSize(session, cart);
        sendSuccess(response, "Đã xóa khỏi giỏ hàng");
    }

    private void updateCartSize(HttpSession session, List<CartItemDTO> cart) {
        int totalItems = 0;
        for (CartItemDTO item : cart) {
            totalItems += item.getQuantity();
        }
        session.setAttribute("cartSize", totalItems);
    }

    private void sendSuccess(HttpServletResponse response, String message) throws IOException {
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", message);
        response.getWriter().write(gson.toJson(result));
    }

    private void sendError(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        Map<String, Object> error = new HashMap<>();
        error.put("success", false);
        error.put("message", message);
        response.getWriter().write(gson.toJson(error));
    }
}
