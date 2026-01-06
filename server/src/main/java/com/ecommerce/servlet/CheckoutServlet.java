package com.ecommerce.servlet;

import com.ecommerce.dto.CartItemDTO;
import com.ecommerce.dto.CheckoutRequest;
import com.ecommerce.entity.UserLog;
import com.ecommerce.entity.ActionType;
import com.ecommerce.entity.Role;
import com.ecommerce.service.OrderService;
import com.ecommerce.service.UserLogService;
import com.ecommerce.util.MenuHelper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "CheckoutServlet", urlPatterns = {"/checkout"})
public class CheckoutServlet extends HttpServlet {

    private final OrderService orderService = new OrderService();
    private final UserLogService userLogService = new UserLogService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        String userId = (String) session.getAttribute("userId");
        
        if (userId == null) {
            response.sendRedirect(request.getContextPath() + "/login?redirect=checkout");
            return;
        }
        
        List<CartItemDTO> cart = (List<CartItemDTO>) session.getAttribute("cart");
        
        if (cart == null || cart.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/cart");
            return;
        }
        
        // Set cart items and calculate totals for JSP
        request.setAttribute("cartItems", cart);
        
        // Calculate subtotal
        double subtotal = cart.stream()
            .mapToDouble(item -> item.getProduct().getSalePrice() * item.getQuantity())
            .sum();
        
        double shippingFee = 30000; // Fixed shipping fee
        double total = subtotal + shippingFee;
        
        request.setAttribute("subtotal", subtotal);
        request.setAttribute("shippingFee", shippingFee);
        request.setAttribute("total", total);
        
        // Set menu items for buyer
        MenuHelper.setMenuItems(request, "BUYER", "/checkout");
        
        request.getRequestDispatcher("/buyer/checkout.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        String userId = (String) session.getAttribute("userId");
        
        if (userId == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        List<CartItemDTO> cart = (List<CartItemDTO>) session.getAttribute("cart");
        if (cart == null || cart.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/cart");
            return;
        }
        
        String address = request.getParameter("address");
        String paymentMethod = request.getParameter("paymentMethod");
        
        if (address == null || address.trim().isEmpty() || paymentMethod == null) {
            request.setAttribute("error", "Please fill in all fields.");
            request.getRequestDispatcher("/buyer/checkout.jsp").forward(request, response);
            return;
        }
        
        try {
            CheckoutRequest checkoutRequest = new CheckoutRequest();
            checkoutRequest.setUserId(userId);
            checkoutRequest.setShippingAddress(address);
            checkoutRequest.setPaymentMethod(paymentMethod);
            checkoutRequest.setItems(cart);
            
            // Check if payment method is BANKING (VNPay uses BANKING in DB)
            if ("BANKING".equals(paymentMethod)) {
                // Store checkout request in session for VNPayServlet
                session.setAttribute("checkoutRequest", checkoutRequest);
                
                // Redirect to VNPay payment
                response.sendRedirect(request.getContextPath() + "/vnpay");
                return;
            }
            
            // ✅ COD payment - Process normally
            List<String> orderIds = orderService.placeOrder(checkoutRequest);
            
            // Tạo log cho BUYER_PLACE_ORDER
            String orderIdsStr = String.join(", ", orderIds);
            UserLog log = new UserLog(userId, Role.BUYER, ActionType.BUYER_PLACE_ORDER,
                "Buyer đặt hàng: " + orderIdsStr + " (Phương thức: " + paymentMethod + ")", 
                orderIdsStr, "ORDER", null);
            userLogService.save(log);
            
            // Clear cart
            session.removeAttribute("cart");
            session.removeAttribute("cartSize");
            
            // Redirect to orders page with success message
            String orderIdsParam = String.join(",", orderIds);
            response.sendRedirect(request.getContextPath() + "/my-orders?success=true&orderIds=" + orderIdsParam);
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Checkout failed: " + e.getMessage());
            request.getRequestDispatcher("/buyer/checkout.jsp").forward(request, response);
        }
    }
}