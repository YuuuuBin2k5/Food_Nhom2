package com.ecommerce.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ecommerce.dto.CartItemDTO;
import com.ecommerce.entity.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "CartPageServlet", urlPatterns = {"/cart"})
public class CartPageServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Check authentication
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        User user = (User) session.getAttribute("user");
        String role = (String) session.getAttribute("role");
        
        // Setup menu items
        List<Map<String, String>> menuItems = getMenuItems(role);
        request.setAttribute("menuItems", menuItems);
        request.setAttribute("currentPath", "/cart");
        
        try {
            // Get cart from session - now using CartItemDTO
            @SuppressWarnings("unchecked")
            List<CartItemDTO> cartItems = (List<CartItemDTO>) session.getAttribute("cart");
            
            if (cartItems == null) {
                cartItems = new ArrayList<>();
            }
            
            // Calculate totals
            double subtotal = 0;
            double total = 0;
            double savings = 0;
            int totalItems = 0;
            
            for (CartItemDTO item : cartItems) {
                double originalPrice = item.getProduct().getOriginalPrice();
                double salePrice = item.getProduct().getSalePrice();
                int quantity = item.getQuantity();
                
                subtotal += originalPrice * quantity;
                total += salePrice * quantity;
                totalItems += quantity;
            }
            
            savings = subtotal - total;
            
            request.setAttribute("cartItems", cartItems);
            request.setAttribute("subtotal", subtotal);
            request.setAttribute("total", total);
            request.setAttribute("savings", savings);
            request.setAttribute("totalItems", totalItems);
            request.setAttribute("cartCount", totalItems);
            
            // Forward to JSP
            request.getRequestDispatcher("/buyer/cart.jsp").forward(request, response);
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Không thể tải giỏ hàng");
            request.setAttribute("cartItems", new ArrayList<>());
            request.getRequestDispatcher("/buyer/cart.jsp").forward(request, response);
        }
    }
    
    private List<Map<String, String>> getMenuItems(String role) {
        List<Map<String, String>> items = new ArrayList<>();
        
        if (!"ADMIN".equals(role)) {
            items.add(createMenuItem("/", "Trang chủ", "🏠"));
        }
        
        switch (role) {
            case "BUYER":
                items.add(createMenuItem("/products", "Sản phẩm", "🛍️"));
                items.add(createMenuItem("/cart", "Giỏ hàng", "🛒"));
                items.add(createMenuItem("/orders", "Đơn mua", "📦"));
                break;
                
            case "SELLER":
                items.add(createMenuItem("/seller/dashboard", "Tổng quan", "📊"));
                items.add(createMenuItem("/seller/products", "Kho hàng", "📦"));
                items.add(createMenuItem("/seller/orders", "Đơn hàng", "📄"));
                items.add(createMenuItem("/seller/settings", "Cài đặt", "⚙️"));
                break;
                
            case "ADMIN":
                items.add(createMenuItem("/admin/dashboard", "Trang chủ", "📊"));
                items.add(createMenuItem("/admin/users", "Quản lý User", "👥"));
                items.add(createMenuItem("/admin/seller-approval", "Duyệt Seller", "🏪"));
                items.add(createMenuItem("/admin/product-approval", "Duyệt Product", "📦"));
                break;
                
            case "SHIPPER":
                items.add(createMenuItem("/shipper/orders", "Đơn cần giao", "🚚"));
                break;
        }
        
        return items;
    }
    
    private Map<String, String> createMenuItem(String path, String label, String icon) {
        Map<String, String> item = new HashMap<>();
        item.put("path", path);
        item.put("label", label);
        item.put("icon", icon);
        return item;
    }
}
