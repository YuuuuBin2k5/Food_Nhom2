package com.ecommerce.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ecommerce.dto.CartItemDTO;
import com.ecommerce.dto.CheckoutRequest;
import com.ecommerce.entity.User;
import com.ecommerce.service.OrderService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "CheckoutPageServlet", urlPatterns = {"/checkout"})
public class CheckoutPageServlet extends HttpServlet {

    private final OrderService orderService = new OrderService();
    private static final double SHIPPING_FEE = 30000.0;

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
        request.setAttribute("currentPath", "/checkout");
        
        try {
            // Get cart from session - using CartItemDTO
            @SuppressWarnings("unchecked")
            List<CartItemDTO> cartItems = (List<CartItemDTO>) session.getAttribute("cart");
            
            if (cartItems == null || cartItems.isEmpty()) {
                request.setAttribute("cartItems", new ArrayList<>());
                request.getRequestDispatcher("/buyer/checkout.jsp").forward(request, response);
                return;
            }
            
            // Calculate totals
            double subtotal = 0;
            for (CartItemDTO item : cartItems) {
                subtotal += item.getProduct().getSalePrice() * item.getQuantity();
            }
            
            double total = subtotal + SHIPPING_FEE;
            
            request.setAttribute("cartItems", cartItems);
            request.setAttribute("subtotal", subtotal);
            request.setAttribute("shippingFee", SHIPPING_FEE);
            request.setAttribute("total", total);
            
            // Forward to JSP
            request.getRequestDispatcher("/buyer/checkout.jsp").forward(request, response);
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Không thể tải trang thanh toán");
            request.getRequestDispatcher("/buyer/checkout.jsp").forward(request, response);
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        
        // Check authentication
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        User user = (User) session.getAttribute("user");
        
        try {
            // Get form data
            String recipientName = request.getParameter("recipientName");
            String recipientPhone = request.getParameter("recipientPhone");
            String shippingAddress = request.getParameter("shippingAddress");
            String paymentMethod = request.getParameter("paymentMethod");
            String note = request.getParameter("note");
            
            // Validate
            Map<String, String> fieldErrors = new HashMap<>();
            
            if (recipientName == null || recipientName.trim().isEmpty()) {
                fieldErrors.put("recipientName", "Vui lòng nhập họ tên");
            }
            
            if (recipientPhone == null || recipientPhone.trim().isEmpty()) {
                fieldErrors.put("recipientPhone", "Vui lòng nhập số điện thoại");
            }
            
            if (shippingAddress == null || shippingAddress.trim().isEmpty()) {
                fieldErrors.put("shippingAddress", "Vui lòng nhập địa chỉ giao hàng");
            }
            
            if (!fieldErrors.isEmpty()) {
                request.setAttribute("fieldErrors", fieldErrors);
                doGet(request, response);
                return;
            }
            
            // Get cart from session - using CartItemDTO
            @SuppressWarnings("unchecked")
            List<CartItemDTO> cartItems = (List<CartItemDTO>) session.getAttribute("cart");
            
            if (cartItems == null || cartItems.isEmpty()) {
                request.setAttribute("error", "Giỏ hàng trống");
                doGet(request, response);
                return;
            }
            
            // Convert cart to CheckoutRequest
            CheckoutRequest checkoutRequest = new CheckoutRequest();
            checkoutRequest.setUserId(user.getUserId());
            checkoutRequest.setShippingAddress(shippingAddress);
            checkoutRequest.setPaymentMethod(paymentMethod);
            checkoutRequest.setItems(cartItems);
            
            // Place order
            List<String> orderIds = orderService.placeOrder(checkoutRequest);
            
            // Calculate total
            double subtotal = 0;
            for (CartItemDTO item : cartItems) {
                subtotal += item.getProduct().getSalePrice() * item.getQuantity();
            }
            double total = subtotal + SHIPPING_FEE;
            
            // Clear cart
            session.removeAttribute("cart");
            
            // Redirect to success page
            String orderIdParam = String.join(",", orderIds);
            response.sendRedirect(request.getContextPath() + "/order-success?orderId=" + orderIdParam 
                + "&total=" + total + "&paymentMethod=" + paymentMethod);
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Không thể đặt hàng: " + e.getMessage());
            doGet(request, response);
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
