package com.ecommerce.servlet;

import com.ecommerce.entity.Order;
import com.ecommerce.entity.OrderDetail;
import com.ecommerce.entity.OrderStatus;
import com.ecommerce.service.OrderService;
import com.ecommerce.util.DBUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import jakarta.persistence.EntityManager;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet(name = "BuyerOrderServlet", urlPatterns = {"/api/buyer/orders", "/api/buyer/orders/*"})
public class BuyerOrderServlet extends HttpServlet {

    private final Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create();
    private final OrderService orderService = new OrderService();

    // --- GET: LẤY DANH SÁCH ĐƠN HÀNG CỦA BUYER ---
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        setHeaders(resp);
        String buyerId = (String) req.getAttribute("userId"); // Lấy từ Token

        // Kiểm tra Role
        String role = (String) req.getAttribute("role");
        if (!"BUYER".equals(role)) {
            sendError(resp, 403, "Access Denied");
            return;
        }

        try (PrintWriter out = resp.getWriter()) {
            // Lấy danh sách đơn hàng
            List<Order> orders = orderService.getOrdersByBuyer(buyerId);

            // Convert sang JSON (Custom để tránh vòng lặp và lấy đủ thông tin cần thiết)
            JsonArray jsonOrders = new JsonArray();

            for (Order o : orders) {
                JsonObject orderJson = new JsonObject();
                orderJson.addProperty("orderId", o.getOrderId());
                orderJson.addProperty("orderDate", o.getOrderDate().toString());
                orderJson.addProperty("status", o.getStatus().toString());
                orderJson.addProperty("shippingAddress", o.getShippingAddress());
                orderJson.addProperty("totalAmount", o.getPayment().getAmount());
                orderJson.addProperty("paymentMethod", o.getPayment().getMethod().toString());

                // Items
                JsonArray itemsJson = new JsonArray();
                for (OrderDetail od : o.getOrderDetails()) {
                    JsonObject item = new JsonObject();
                    item.addProperty("productId", od.getProduct().getProductId());
                    item.addProperty("name", od.getProduct().getName());
                    item.addProperty("quantity", od.getQuantity());
                    item.addProperty("price", od.getPriceAtPurchase());
                    item.addProperty("imageUrl", od.getProduct().getImageUrl());
                    item.addProperty("shopName", od.getProduct().getSeller().getShopName());
                    itemsJson.add(item);
                }
                orderJson.add("items", itemsJson);

                jsonOrders.add(orderJson);
            }

            out.print(gson.toJson(jsonOrders));

        } catch (Exception e) {
            e.printStackTrace();
            sendError(resp, 500, e.getMessage());
        }
    }

    // --- PUT: HỦY ĐƠN HÀNG (CHỈ KHI PENDING) ---
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        setHeaders(resp);

        try (PrintWriter out = resp.getWriter()) {
            // Lấy Order ID từ URL
            String pathInfo = req.getPathInfo();
            if (pathInfo == null || pathInfo.length() < 2) throw new Exception("Invalid Order ID");

            // Format URL mong đợi: /123/cancel
            String[] parts = pathInfo.split("/");
            Long orderId = Long.parseLong(parts[1]);
            String action = parts.length > 2 ? parts[2] : "";

            if (!"cancel".equals(action)) {
                throw new Exception("Invalid action");
            }

            // Kiểm tra trạng thái hiện tại trước khi hủy
            Order order = orderService.getOrderById(orderId);

            // Bảo mật: check xem đơn này có đúng của user đang login không
            String currentUserId = (String) req.getAttribute("userId");
            if (!order.getBuyer().getUserId().equals(currentUserId)) {
                sendError(resp, 403, "Bạn không có quyền hủy đơn hàng này");
                return;
            }

            if (order.getStatus() != OrderStatus.PENDING) {
                throw new Exception("Chỉ có thể hủy đơn hàng khi đang ở trạng thái CHỜ (Pending)");
            }

            // Thực hiện hủy
            orderService.updateOrderStatus(orderId, OrderStatus.CANCELLED);

            JsonObject success = new JsonObject();
            success.addProperty("success", true);
            success.addProperty("message", "Đã hủy đơn hàng thành công");
            out.print(gson.toJson(success));

        } catch (Exception e) {
            resp.setStatus(500); // Hoặc 400 tùy logic
            JsonObject err = new JsonObject();
            err.addProperty("success", false);
            err.addProperty("message", e.getMessage());
            resp.getWriter().print(gson.toJson(err));
        }
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        setHeaders(resp);
        resp.setStatus(HttpServletResponse.SC_OK);
    }

    private void setHeaders(HttpServletResponse resp) {
        resp.setHeader("Access-Control-Allow-Origin", "http://localhost:5173");
        resp.setHeader("Access-Control-Allow-Methods", "GET, PUT, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
        resp.setHeader("Access-Control-Allow-Credentials", "true");
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
    }

    private void sendError(HttpServletResponse resp, int code, String message) throws IOException {
        resp.setStatus(code);
        JsonObject json = new JsonObject();
        json.addProperty("success", false);
        json.addProperty("message", message);
        resp.getWriter().print(gson.toJson(json));
    }
}
