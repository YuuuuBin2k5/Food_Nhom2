package com.ecommerce.servlet;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.ecommerce.entity.Product;
import com.ecommerce.entity.ProductStatus;
import com.ecommerce.entity.User;
import com.ecommerce.service.ProductService;
import com.ecommerce.service.OrderService;
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

        System.out.println("[SellerDashboard] Starting dashboard request");

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            System.out.println("[SellerDashboard] No session or user, redirecting to login");
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String role = (String) session.getAttribute("role");
        if (!"SELLER".equals(role)) {
            System.out.println("[SellerDashboard] User is not seller, role: " + role);
            response.sendRedirect(request.getContextPath() + "/home");
            return;
        }

        User user = (User) session.getAttribute("user");
        System.out.println("[SellerDashboard] User: " + user.getUserId() + " - " + user.getFullName());

        try {
            // Set menu items
            MenuHelper.setMenuItems(request, "SELLER", "/seller/dashboard");
            System.out.println("[SellerDashboard] Menu items set");

            String sellerId = user.getUserId();
            System.out.println("[SellerDashboard] Loading data for seller: " + sellerId);

            // Initialize with safe defaults
            List<Product> products = new ArrayList<>();

            // Try to load products safely
            try {
                products = productService.getProductsBySeller(sellerId);
                System.out.println("[SellerDashboard] Loaded " + products.size() + " products");
            } catch (Exception e) {
                System.err.println("[SellerDashboard] Error loading products: " + e.getMessage());
                e.printStackTrace();
                products = new ArrayList<>(); // Fallback to empty list
            }

            // Skip orders for now to avoid issues
            System.out.println("[SellerDashboard] Skipping orders to avoid issues");

            // Calculate basic stats
            long totalProducts = products.size();
            long activeProducts = products.stream()
                    .filter(p -> p.getStatus() == ProductStatus.ACTIVE)
                    .count();

            long expiringSoon = 0;
            try {
                // Sử dụng method mới từ ProductService để đếm sản phẩm sắp hết hạn
                expiringSoon = productService.countExpiringSoonProducts(sellerId);
            } catch (Exception e) {
                System.err.println("[SellerDashboard] Error calculating expiring products: " + e.getMessage());
                expiringSoon = 0;
            }

            // Set safe defaults for order-related stats
            long pendingOrders = 0;
            double totalRevenue = 0.0;

            // Tính tổng doanh thu từ các đơn hàng DELIVERED
            try {
                totalRevenue = orderService.calculateSellerRevenue(sellerId);
                System.out.println("[SellerDashboard] Total revenue calculated: " + totalRevenue);
            } catch (Exception e) {
                System.err.println("[SellerDashboard] Error calculating revenue: " + e.getMessage());
                totalRevenue = 0.0;
            }

            // Get recent products safely
            List<Product> recentProducts = products.stream()
                    .limit(5)
                    .collect(Collectors.toList());

            List<Object> recentOrders = new ArrayList<>(); // Empty for now

            System.out.println("[SellerDashboard] Stats calculated:");
            System.out.println("  - Total products: " + totalProducts);
            System.out.println("  - Active products: " + activeProducts);
            System.out.println("  - Expiring soon: " + expiringSoon);
            System.out.println("  - Total revenue: " + totalRevenue);

            // Set attributes
            request.setAttribute("totalProducts", totalProducts);
            request.setAttribute("activeProducts", activeProducts);
            request.setAttribute("expiringSoon", expiringSoon);
            request.setAttribute("pendingOrders", pendingOrders);
            request.setAttribute("totalRevenue", totalRevenue);
            request.setAttribute("recentProducts", recentProducts);
            request.setAttribute("recentOrders", recentOrders);

            System.out.println("[SellerDashboard] Forwarding to JSP");
            request.getRequestDispatcher("/seller/dashboard.jsp").forward(request, response);

        } catch (Exception e) {
            System.err.println("[SellerDashboard] Dashboard error: " + e.getMessage());
            e.printStackTrace();

            // Set minimal attributes to avoid JSP errors
            request.setAttribute("totalProducts", 0L);
            request.setAttribute("activeProducts", 0L);
            request.setAttribute("expiringSoon", 0L);
            request.setAttribute("pendingOrders", 0L);
            request.setAttribute("totalRevenue", 0.0);
            request.setAttribute("recentProducts", new ArrayList<>());
            request.setAttribute("recentOrders", new ArrayList<>());
            request.setAttribute("error", "Có lỗi khi tải dữ liệu: " + e.getMessage());

            try {
                request.getRequestDispatcher("/seller/dashboard.jsp").forward(request, response);
            } catch (Exception ex) {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        "Lỗi tải dữ liệu dashboard: " + ex.getMessage());
            }
        }
    }

    private boolean isExpiringSoon(Date expirationDate) {
        if (expirationDate == null)
            return false;

        LocalDate expiry = expirationDate.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
        LocalDate now = LocalDate.now();
        LocalDate threeDaysLater = now.plusDays(3);

        return !expiry.isBefore(now) && !expiry.isAfter(threeDaysLater);
    }
}
