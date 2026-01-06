package com.ecommerce.servlet;

import com.ecommerce.dto.CartItemDTO;
import com.ecommerce.dto.CheckoutRequest;
import com.ecommerce.entity.UserLog;
import com.ecommerce.entity.ActionType;
import com.ecommerce.entity.Role;
import com.ecommerce.service.OrderService;
import com.ecommerce.service.UserLogService;
import com.ecommerce.util.VNPayUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/vnpay")
public class VNPayServlet extends HttpServlet {

    private final OrderService orderService = new OrderService();
    private final UserLogService userLogService = new UserLogService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String vnpResponseCode = request.getParameter("vnp_ResponseCode");

        // If vnp_ResponseCode exists, this is a callback from VNPay
        if (vnpResponseCode != null) {
            handleCallback(request, response);
        } else {
            // Otherwise, initiate payment
            handlePaymentInitiation(request, response);
        }
    }

    /**
     * Handle payment initiation - Generate VNPay URL and redirect
     */
    private void handlePaymentInitiation(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        String userId = (String) session.getAttribute("userId");

        if (userId == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        // Get checkout request from session (stored by CheckoutServlet)
        CheckoutRequest checkoutRequest = (CheckoutRequest) session.getAttribute("checkoutRequest");
        List<CartItemDTO> cart = (List<CartItemDTO>) session.getAttribute("cart");

        if (checkoutRequest == null || cart == null || cart.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/cart");
            return;
        }

        try {
            // Calculate total amount
            double subtotal = cart.stream()
                .mapToDouble(item -> item.getProduct().getSalePrice() * item.getQuantity())
                .sum();
            double shippingFee = 30000;
            long totalAmount = (long) (subtotal + shippingFee);

            // Generate unique order reference
            String orderRef = "ORDER_" + System.currentTimeMillis();
            session.setAttribute("vnpayOrderRef", orderRef);

            // Get client IP
            String ipAddress = request.getRemoteAddr();

            // Build VNPay payment URL
            String orderInfo = "Thanh toan don hang " + orderRef;
            String paymentUrl = VNPayUtil.buildPaymentUrl(orderRef, totalAmount, orderInfo, ipAddress);

            System.out.println("=== [VNPayServlet] Payment Initiation ===");
            System.out.println("Order Ref: " + orderRef);
            System.out.println("Amount: " + totalAmount);
            System.out.println("Redirecting to VNPay...");

            // Redirect to VNPay
            response.sendRedirect(paymentUrl);

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Lỗi khi tạo thanh toán VNPay: " + e.getMessage());
            request.getRequestDispatcher("/buyer/checkout.jsp").forward(request, response);
        }
    }

    /**
     * Handle VNPay callback - Validate and process payment result
     */
    private void handleCallback(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        String userId = (String) session.getAttribute("userId");

        System.out.println("=== [VNPayServlet] Callback Received ===");

        // Get all VNPay parameters
        Map<String, String> vnpParams = new HashMap<>();
        request.getParameterMap().forEach((key, values) -> {
            if (values.length > 0) {
                vnpParams.put(key, values[0]);
                System.out.println(key + ": " + values[0]);
            }
        });

        // Validate signature
        String vnpSecureHash = request.getParameter("vnp_SecureHash");
        boolean isValidSignature = VNPayUtil.validateSignature(vnpParams, vnpSecureHash);

        if (!isValidSignature) {
            System.out.println("❌ Invalid signature!");
            request.setAttribute("error", "Chữ ký không hợp lệ");
            request.getRequestDispatcher("/buyer/checkout.jsp").forward(request, response);
            return;
        }

        // Get response code
        String responseCode = request.getParameter("vnp_ResponseCode");
        String orderRef = request.getParameter("vnp_TxnRef");
        String transactionNo = request.getParameter("vnp_TransactionNo");

        System.out.println("Response Code: " + responseCode);
        System.out.println("Order Ref: " + orderRef);
        System.out.println("Transaction No: " + transactionNo);

        // Check if payment successful
        if ("00".equals(responseCode)) {
            // Payment successful - Create order
            try {
                CheckoutRequest checkoutRequest = (CheckoutRequest) session.getAttribute("checkoutRequest");
                
                if (checkoutRequest == null) {
                    throw new Exception("Không tìm thấy thông tin đơn hàng");
                }

                // Place order
                List<String> orderIds = orderService.placeOrder(checkoutRequest);
                
                // Tạo log cho BUYER_PAY_ORDER
                String orderIdsStr = String.join(", ", orderIds);
                UserLog log = new UserLog(userId, Role.BUYER, ActionType.BUYER_PAY_ORDER,
                    "Buyer thanh toán VNPay thành công cho đơn hàng: " + orderIdsStr + " (Mã GD: " + transactionNo + ")", 
                    orderIdsStr, "ORDER", null);
                userLogService.save(log);

                // Clear cart and checkout data
                session.removeAttribute("cart");
                session.removeAttribute("cartCount");
                session.removeAttribute("checkoutRequest");
                session.removeAttribute("vnpayOrderRef");

                System.out.println("✅ Payment successful! Order IDs: " + orderIds);

                // Redirect to success page
                session.setAttribute("successMessage", "Thanh toán VNPay thành công! Mã giao dịch: " + transactionNo);
                response.sendRedirect(request.getContextPath() + "/my-orders");

            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("❌ Error creating order: " + e.getMessage());
                request.setAttribute("error", "Thanh toán thành công nhưng không thể tạo đơn hàng: " + e.getMessage());
                request.getRequestDispatcher("/buyer/checkout.jsp").forward(request, response);
            }

        } else {
            // Payment failed or cancelled
            System.out.println("❌ Payment failed/cancelled. Response code: " + responseCode);
            
            String errorMessage = getErrorMessage(responseCode);
            request.setAttribute("error", errorMessage);
            request.getRequestDispatcher("/buyer/checkout.jsp").forward(request, response);
        }
    }

    /**
     * Get error message from VNPay response code
     */
    private String getErrorMessage(String responseCode) {
        switch (responseCode) {
            case "07":
                return "Giao dịch bị nghi ngờ gian lận";
            case "09":
                return "Thẻ/Tài khoản chưa đăng ký dịch vụ InternetBanking";
            case "10":
                return "Xác thực thông tin thẻ/tài khoản không đúng quá 3 lần";
            case "11":
                return "Đã hết hạn chờ thanh toán";
            case "12":
                return "Thẻ/Tài khoản bị khóa";
            case "13":
                return "Mật khẩu xác thực giao dịch không đúng";
            case "24":
                return "Giao dịch bị hủy";
            case "51":
                return "Tài khoản không đủ số dư";
            case "65":
                return "Tài khoản đã vượt quá hạn mức giao dịch trong ngày";
            case "75":
                return "Ngân hàng thanh toán đang bảo trì";
            case "79":
                return "Nhập sai mật khẩu quá số lần quy định";
            default:
                return "Thanh toán thất bại. Mã lỗi: " + responseCode;
        }
    }
}
