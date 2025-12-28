package com.ecommerce.servlet;

import com.ecommerce.dto.CartItemDTO;
import com.ecommerce.dto.CheckoutRequest;
import com.ecommerce.service.OrderService;
import com.ecommerce.util.MenuHelper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

/**
 * Handles checkout process
 * GET - Display checkout form with cart summary
 * POST - Process order placement
 */
@WebServlet(name = "CheckoutServlet", urlPatterns = {"/checkout"})
public class CheckoutServlet extends HttpServlet {

    private final OrderService orderService = new OrderService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        
        // Authentication check
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login?redirect=checkout");
            return;
        }
        
        String userId = (String) session.getAttribute("userId");
        if (userId == null) {
            response.sendRedirect(request.getContextPath() + "/login?redirect=checkout");
            return;
        }
        
        // Check cart not empty
        @SuppressWarnings("unchecked")
        List<CartItemDTO> cart = (List<CartItemDTO>) session.getAttribute("cart");
        if (cart == null || cart.isEmpty()) {
            session.setAttribute("errorMessage", "Giỏ hàng trống! Vui lòng thêm sản phẩm trước khi thanh toán.");
            response.sendRedirect(request.getContextPath() + "/view-cart");
            return;
        }
        
        // Calculate cart summary
        double subtotal = cart.stream()
            .mapToDouble(CartItemDTO::getSubtotal)
            .sum();
        
        double savings = cart.stream()
            .mapToDouble(CartItemDTO::getSavings)
            .sum();
        
        int itemCount = cart.stream()
            .mapToInt(CartItemDTO::getQuantity)
            .sum();
        
        double shippingFee = 30000; // Fixed shipping fee
        double total = subtotal + shippingFee;
        
        // Set attributes for JSP
        request.setAttribute("cartItems", cart);
        request.setAttribute("subtotal", subtotal);
        request.setAttribute("savings", savings);
        request.setAttribute("shippingFee", shippingFee);
        request.setAttribute("total", total);
        request.setAttribute("itemCount", itemCount);
        
        // Set menu items
        MenuHelper.setMenuItems(request, "BUYER", "/checkout");
        
        request.getRequestDispatcher("/buyer/checkout.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        
        // Authentication check
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        String userId = (String) session.getAttribute("userId");
        if (userId == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        // Get cart
        @SuppressWarnings("unchecked")
        List<CartItemDTO> cart = (List<CartItemDTO>) session.getAttribute("cart");
        if (cart == null || cart.isEmpty()) {
            session.setAttribute("errorMessage", "Giỏ hàng trống!");
            response.sendRedirect(request.getContextPath() + "/view-cart");
            return;
        }
        
        // Get form parameters
        String address = request.getParameter("deliveryAddress");
        String paymentMethod = request.getParameter("paymentMethod");
        String fullName = request.getParameter("fullName");
        String phone = request.getParameter("phone");
        
        // Validation
        if (!validateCheckoutForm(address, paymentMethod, fullName, phone)) {
            request.setAttribute("error", "Vui lòng điền đầy đủ thông tin giao hàng!");
            doGet(request, response); // Re-display form with error
            return;
        }
        
        try {
            // Build checkout request
            CheckoutRequest checkoutRequest = new CheckoutRequest();
            checkoutRequest.setUserId(userId);
            checkoutRequest.setShippingAddress(formatAddress(fullName, phone, address));
            checkoutRequest.setPaymentMethod(paymentMethod);
            checkoutRequest.setItems(cart);
            
            // Place order (may create multiple orders if items from different sellers)
            List<String> orderIds = orderService.placeOrder(checkoutRequest);
            
            // Clear cart after successful order
            session.removeAttribute("cart");
            session.setAttribute("cartSize", 0);
            
            // Store success info in session
            session.setAttribute("successMessage", "Đặt hàng thành công! Mã đơn hàng: " + String.join(", ", orderIds));
            session.setAttribute("recentOrderIds", orderIds);
            
            // Redirect to success page
            response.sendRedirect(request.getContextPath() + "/order-success");
            
        } catch (IllegalArgumentException e) {
            // Validation errors (e.g., insufficient stock)
            request.setAttribute("error", e.getMessage());
            doGet(request, response);
            
        } catch (Exception e) {
            // System errors
            e.printStackTrace();
            request.setAttribute("error", "Đặt hàng thất bại: " + e.getMessage());
            doGet(request, response);
        }
    }
    
    /**
     * Validate checkout form fields
     */
    private boolean validateCheckoutForm(String address, String paymentMethod, 
                                         String fullName, String phone) {
        if (address == null || address.trim().isEmpty()) return false;
        if (paymentMethod == null || paymentMethod.trim().isEmpty()) return false;
        if (fullName == null || fullName.trim().isEmpty()) return false;
        if (phone == null || phone.trim().isEmpty()) return false;
        
        // Validate phone number format (Vietnamese phone numbers)
        if (!phone.matches("^0\\d{9}$")) return false;
        
        return true;
    }
    
    /**
     * Format shipping address with contact info
     */
    private String formatAddress(String fullName, String phone, String address) {
        return fullName + " - " + phone + " - " + address;
    }
}
