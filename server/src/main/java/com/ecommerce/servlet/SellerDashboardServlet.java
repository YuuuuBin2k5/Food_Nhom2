package com.ecommerce.servlet;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.ecommerce.entity.Order;
import com.ecommerce.entity.OrderDetail;
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
        
        System.out.println("=== SellerDashboardServlet.doGet() called ===");
        
        HttpSession session = request.getSession(false);
        System.out.println("Session: " + session);
        
        if (session == null || session.getAttribute("user") == null) {
            System.out.println("No session or user, redirecting to login");
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        String role = (String) session.getAttribute("role");
        System.out.println("User role: " + role);
        
        if (!"SELLER".equals(role)) {
            System.out.println("Not a seller, redirecting to home");
            response.sendRedirect(request.getContextPath() + "/home");
            return;
        }
        
        User user = (User) session.getAttribute("user");
        System.out.println("User: " + (user != null ? user.getUserId() : "null"));
        
        // Set menu items
        MenuHelper.setMenuItems(request, "SELLER", "/seller/dashboard");
        
        try {
            String sellerId = user.getUserId();
            
            if (sellerId == null) {
                System.err.println("Dashboard error: sellerId is null");
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Lỗi: Không tìm thấy thông tin seller");
                return;
            }
            
            System.out.println("Loading dashboard for seller: " + sellerId);
            
            // Load products and orders
            List<Product> products = null;
            List<Order> orders = null;
            
            try {
                products = productService.getProductsBySeller(sellerId);
                System.out.println("Loaded " + (products != null ? products.size() : 0) + " products");
            } catch (Exception e) {
                System.err.println("Error loading products: " + e.getMessage());
                e.printStackTrace();
                products = new ArrayList<>(); // Empty list
            }
            
            try {
                orders = orderService.getOrdersBySellerProducts(sellerId);
                System.out.println("Loaded " + (orders != null ? orders.size() : 0) + " orders");
            } catch (Exception e) {
                System.err.println("Error loading orders: " + e.getMessage());
                e.printStackTrace();
                orders = new ArrayList<>(); // Empty list
            }
            
            // Ensure lists are not null
            if (products == null) products = new ArrayList<>();
            if (orders == null) orders = new ArrayList<>();
            
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
            
            // Calculate revenue without stream to avoid UnsupportedOperationException
            double totalRevenue = 0.0;
            try {
                for (Order order : orders) {
                    if (order.getStatus() == OrderStatus.DELIVERED) {
                        List<OrderDetail> details = order.getOrderDetails();
                        if (details != null) {
                            for (OrderDetail od : details) {
                                totalRevenue += od.getPriceAtPurchase() * od.getQuantity();
                            }
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println("Error calculating revenue: " + e.getMessage());
                e.printStackTrace();
                totalRevenue = 0.0;
            }
            
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
            System.err.println("Dashboard error: " + e.getClass().getName());
            System.err.println("Error message: " + e.getMessage());
            e.printStackTrace();
            
            String errorMsg = e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Lỗi tải dữ liệu dashboard: " + errorMsg);
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
