package com.ecommerce.servlet;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.ecommerce.entity.Order;
import com.ecommerce.entity.OrderStatus;
import com.ecommerce.entity.Product;
import com.ecommerce.entity.ProductStatus;
import com.ecommerce.entity.User;
import com.ecommerce.service.OrderService;
import com.ecommerce.service.ProductService;
import com.ecommerce.util.MenuHelper;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/seller/dashboard")
public class SellerDashboardServlet extends HttpServlet {
    
    private ProductService productService = new ProductService();
    private OrderService orderService = new OrderService();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        String role = (String) session.getAttribute("role");
        if (!"SELLER".equals(role)) {
            response.sendRedirect(request.getContextPath() + "/home");
            return;
        }
        
        User user = (User) session.getAttribute("user");
        
        // Set menu items
        MenuHelper.setMenuItems(request, "SELLER", "/seller/dashboard");
        
        try {
            String sellerId = user.getUserId();
            
            // Load products and orders
            List<Product> products = null;
            List<Order> orders = null;
            
            try {
                products = productService.getProductsBySeller(sellerId);
            } catch (Exception e) {
                System.err.println("Error loading products: " + e.getMessage());
                e.printStackTrace();
                products = List.of(); // Empty list
            }
            
            try {
                orders = orderService.getOrdersBySellerProducts(sellerId);
            } catch (Exception e) {
                System.err.println("Error loading orders: " + e.getMessage());
                e.printStackTrace();
                orders = List.of(); // Empty list
            }
            
            // Calculate stats
            long totalProducts = products.size();
            long activeProducts = products.stream()
                .filter(p -> p.getStatus() == ProductStatus.ACTIVE)
                .count();
            
            long expiringSoon = products.stream()
                .filter(p -> p.getStatus() == ProductStatus.ACTIVE && isExpiringSoon(p.getExpirationDate()))
                .count();
            
            long pendingOrders = orders.stream()
                .filter(o -> o.getStatus() == OrderStatus.PENDING)
                .count();
            
            long todayOrders = orders.stream()
                .filter(o -> isToday(o.getOrderDate()))
                .count();
            
            double totalRevenue = orders.stream()
                .filter(o -> o.getStatus() == OrderStatus.DELIVERED)
                .flatMap(o -> o.getOrderDetails().stream())
                .mapToDouble(od -> od.getPriceAtPurchase() * od.getQuantity())
                .sum();
            
            // Get recent items
            List<Product> recentProducts = products.stream()
                .limit(5)
                .collect(Collectors.toList());
            
            List<Order> recentOrders = orders.stream()
                .limit(5)
                .collect(Collectors.toList());
            
            // Set attributes
            request.setAttribute("totalProducts", totalProducts);
            request.setAttribute("activeProducts", activeProducts);
            request.setAttribute("expiringSoon", expiringSoon);
            request.setAttribute("pendingOrders", pendingOrders);
            request.setAttribute("todayOrders", todayOrders);
            request.setAttribute("totalRevenue", totalRevenue);
            request.setAttribute("recentProducts", recentProducts);
            request.setAttribute("recentOrders", recentOrders);
            
            request.getRequestDispatcher("/seller/dashboard.jsp").forward(request, response);
            
        } catch (Exception e) {
            System.err.println("Dashboard error: " + e.getMessage());
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Lỗi tải dữ liệu dashboard: " + e.getMessage());
        }
    }
    
    private boolean isExpiringSoon(Date expirationDate) {
        if (expirationDate == null) return false;
        
        LocalDate expiry = expirationDate.toInstant()
            .atZone(ZoneId.systemDefault())
            .toLocalDate();
        LocalDate now = LocalDate.now();
        LocalDate threeDaysLater = now.plusDays(3);
        
        return !expiry.isBefore(now) && !expiry.isAfter(threeDaysLater);
    }
    
    private boolean isToday(Date date) {
        if (date == null) return false;
        
        LocalDate orderDate = date.toInstant()
            .atZone(ZoneId.systemDefault())
            .toLocalDate();
        return orderDate.equals(LocalDate.now());
    }
}
