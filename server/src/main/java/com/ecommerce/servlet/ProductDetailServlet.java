package com.ecommerce.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ecommerce.entity.Product;
import com.ecommerce.entity.User;
import com.ecommerce.service.ProductService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "ProductDetailServlet", urlPatterns = {"/products/*"})
public class ProductDetailServlet extends HttpServlet {

    private final ProductService productService = new ProductService();

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
        
        // Get product ID from path
        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.length() <= 1) {
            // No product ID, show error
            request.setAttribute("error", "Không tìm thấy sản phẩm");
            List<Map<String, String>> menuItems = getMenuItems(role);
            request.setAttribute("menuItems", menuItems);
            request.setAttribute("currentPath", "/products");
            request.getRequestDispatcher("/buyer/product-detail.jsp")
                   .forward(request, response);
            return;
        }
        
        try {
            Long productId = Long.parseLong(pathInfo.substring(1));
            
            // Fetch product
            Product product = productService.getActiveProductById(productId);
            
            if (product == null) {
                request.setAttribute("error", "Không tìm thấy sản phẩm");
                request.getRequestDispatcher("/buyer/product-detail.jsp")
                       .forward(request, response);
                return;
            }
            
            // Setup menu items
            List<Map<String, String>> menuItems = getMenuItems(role);
            request.setAttribute("menuItems", menuItems);
            request.setAttribute("currentPath", "/products");
            
            // Set product data
            request.setAttribute("product", product);
            
            // Calculate discount percentage
            int discountPercent = 0;
            if (product.getOriginalPrice() > product.getSalePrice()) {
                double discount = ((product.getOriginalPrice() - product.getSalePrice()) 
                                  / product.getOriginalPrice()) * 100;
                discountPercent = (int) Math.round(discount);
            }
            request.setAttribute("discountPercent", discountPercent);
            
            // Get cart quantity for this product (TODO: implement cart service)
            request.setAttribute("cartQuantity", 0);
            
            // Forward to JSP
            request.getRequestDispatcher("/buyer/product-detail.jsp")
                   .forward(request, response);
            
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/products");
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Không thể tải thông tin sản phẩm");
            request.getRequestDispatcher("/buyer/product-detail.jsp")
                   .forward(request, response);
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
