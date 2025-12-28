package com.ecommerce.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ecommerce.entity.Order;
import com.ecommerce.entity.User;
import com.ecommerce.service.OrderService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "OrderHistoryServlet", urlPatterns = {"/orders"})
public class OrderHistoryServlet extends HttpServlet {

    private final OrderService orderService = new OrderService();

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
        request.setAttribute("currentPath", "/orders");
        
        try {
            // Fetch orders
            List<Order> orders = orderService.getOrdersByBuyer(user.getUserId());
            
            request.setAttribute("orders", orders);
            
            // Forward to JSP
            request.getRequestDispatcher("/buyer/orders.jsp").forward(request, response);
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Không thể tải danh sách đơn hàng");
            request.setAttribute("orders", new ArrayList<>());
            request.getRequestDispatcher("/buyer/orders.jsp").forward(request, response);
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
