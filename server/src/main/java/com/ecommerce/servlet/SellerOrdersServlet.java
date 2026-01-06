package com.ecommerce.servlet;

import java.io.IOException;
import java.util.List;

import com.ecommerce.entity.Order;
import com.ecommerce.entity.OrderStatus;
import com.ecommerce.entity.User;
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

@WebServlet("/seller/orders")
public class SellerOrdersServlet extends HttpServlet {

    private OrderService orderService = new OrderService();
    private UserLogService userLogService = new UserLogService();

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
        MenuHelper.setMenuItems(request, "SELLER", "/seller/orders");

        try {
            String sellerId = user.getUserId();
            List<Order> orders = orderService.getOrdersBySellerProducts(sellerId);

            // ✅ DEBUG: Log seller info
            System.out.println("=== [SellerOrdersServlet] ===");
            System.out.println("Seller ID: " + sellerId);
            System.out.println("Total orders from DB: " + orders.size());
            if (!orders.isEmpty()) {
                System.out.println("Orders:");
                for (Order o : orders) {
                    System.out.println("  - Order #" + o.getOrderId() + " | Status: " + o.getStatus());
                }
            }

            // ✅ Filter by status if provided
            String statusParam = request.getParameter("status");
            // 2. [QUAN TRỌNG] Logic mặc định:
            // Nếu không có status (vào từ sidebar hoặc redirect), ép kiểu về PENDING
            // Việc này giúp khớp với giao diện JSP đang highlight tab Pending
            if (statusParam == null || statusParam.trim().isEmpty()) {
                statusParam = "PENDING";
            }

            // 3. Thực hiện lọc
            // Nếu status là "ALL" thì bỏ qua đoạn này (giữ nguyên list đầy đủ)
            // Nếu khác "ALL" (ví dụ PENDING, CONFIRMED...) thì lọc theo trạng thái đó
            if (!"ALL".equals(statusParam)) {
                String finalStatus = statusParam; // Biến final để dùng trong lambda
                orders = orders.stream()
                        .filter(o -> finalStatus.equals(o.getStatus().name()))
                        .collect(java.util.stream.Collectors.toList());
            }

            request.setAttribute("orders", orders);
            request.getRequestDispatcher("/seller/orders.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Lỗi tải danh sách đơn hàng");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null || !"SELLER".equals(session.getAttribute("role"))) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        try {
            // 1. Lấy dữ liệu dạng String
            String orderIdStr = request.getParameter("orderId");
            String action = request.getParameter("action"); // Ví dụ: "CONFIRM", "CANCEL", "SHIP"
            User user = (User) session.getAttribute("user");

            if (orderIdStr != null && action != null) {
                Long orderId = Long.parseLong(orderIdStr);
                OrderStatus newStatus = null;
                ActionType logAction = null;
                String logDescription = null;

                // Logic chuyển đổi từ nút bấm sang Trạng thái
                switch (action) {
                    case "CONFIRM": // Duyệt đơn
                        newStatus = OrderStatus.CONFIRMED;
                        logAction = ActionType.SELLER_ACCEPT_ORDER;
                        logDescription = "Seller chấp nhận đơn hàng #" + orderId;
                        break;
                    case "SHIP": // Giao hàng
                        newStatus = OrderStatus.SHIPPING;
                        break;
                    case "CANCEL": // Hủy đơn
                        newStatus = OrderStatus.CANCELLED;
                        logAction = ActionType.SELLER_REJECT_ORDER;
                        logDescription = "Seller từ chối đơn hàng #" + orderId;
                        break;
                    case "DELIVER": // Đánh dấu đã giao (nếu cần)
                        newStatus = OrderStatus.DELIVERED;
                        break;
                    // Thêm các case khác nếu cần
                }

                if (newStatus != null) {
                    // Gọi Service xử lý (Code Service bạn đã viết rồi)
                    orderService.updateOrderStatus(orderId, newStatus);
                    
                    // Tạo log nếu là accept hoặc reject
                    if (logAction != null && logDescription != null) {
                        UserLog log = new UserLog(user.getUserId(), Role.SELLER, logAction,
                            logDescription, orderId.toString(), "ORDER", null);
                        userLogService.save(log);
                    }
                }
            }

            // 4. Redirect về trang danh sách để load lại dữ liệu mới
            response.sendRedirect(request.getContextPath() + "/seller/orders");

        } catch (IllegalArgumentException e) {
            // Lỗi này xảy ra nếu String status không khớp với bất kỳ Enum nào
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Trạng thái đơn hàng không hợp lệ");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Lỗi cập nhật đơn hàng: " + e.getMessage());
        }
    }
}