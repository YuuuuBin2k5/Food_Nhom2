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

@WebServlet(name = "ShipperOrderServlet", urlPatterns = {"/api/shipper/orders", "/api/shipper/orders/*"})
public class ShipperOrderServlet extends HttpServlet {

    private final Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create();
    private final OrderService orderService = new OrderService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        setHeaders(resp);
        String shipperId = (String) req.getAttribute("userId");

        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try (PrintWriter out = resp.getWriter()) {

            // Lấy đơn hàng:
            // 1. CONFIRMED (chưa có shipper) - Đơn có sẵn để nhận
            // 2. SHIPPING/DELIVERED (của shipper này) - Đơn đang giao/đã giao
            String jpql = "SELECT o FROM Order o WHERE " +
                    "(o.status = com.ecommerce.entity.OrderStatus.CONFIRMED AND o.shipper IS NULL) " +
                    "OR (o.shipper.userId = :shipperId AND o.status IN " +
                    "(com.ecommerce.entity.OrderStatus.SHIPPING, " +
                    "com.ecommerce.entity.OrderStatus.DELIVERED)) " +
                    "ORDER BY o.orderDate DESC";

            List<Order> orders = em.createQuery(jpql, Order.class)
                    .setParameter("shipperId", shipperId)
                    .getResultList();

            JsonArray jsonOrders = new JsonArray();

            for (Order o : orders) {
                JsonObject orderJson = new JsonObject();
                orderJson.addProperty("orderId", o.getOrderId());
                orderJson.addProperty("orderDate", o.getOrderDate().toString());
                orderJson.addProperty("status", o.getStatus().toString());
                orderJson.addProperty("buyerName", o.getBuyer().getFullName());
                orderJson.addProperty("shippingAddress", o.getShippingAddress());
                orderJson.addProperty("totalAmount", o.getPayment().getAmount());
                orderJson.addProperty("shippingFee", 15000.0); // Phí cố định hoặc tính toán

                // Items
                JsonArray itemsJson = new JsonArray();
                for (OrderDetail od : o.getOrderDetails()) {
                    JsonObject item = new JsonObject();
                    item.addProperty("name", od.getProduct().getName());
                    item.addProperty("quantity", od.getQuantity());
                    item.addProperty("price", od.getPriceAtPurchase());
                    itemsJson.add(item);
                }
                orderJson.add("items", itemsJson);

                jsonOrders.add(orderJson);
            }

            out.print(gson.toJson(jsonOrders));

        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(500);
            JsonObject err = new JsonObject();
            err.addProperty("message", e.getMessage());
            resp.getWriter().print(gson.toJson(err));
        } finally {
            em.close();
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        setHeaders(resp);

        try (PrintWriter out = resp.getWriter()) {
            // URL format: /api/shipper/orders/123/status
            String pathInfo = req.getPathInfo();
            if (pathInfo == null) throw new Exception("Invalid ID");

            String[] parts = pathInfo.split("/");
            if (parts.length < 3) throw new Exception("Invalid URL format");

            Long orderId = Long.parseLong(parts[1]);

            // Đọc body lấy status mới
            JsonObject body = gson.fromJson(req.getReader(), JsonObject.class);
            String statusStr = body.get("status").getAsString();
            OrderStatus newStatus = OrderStatus.valueOf(statusStr);

            // Shipper chỉ có thể chuyển từ CONFIRMED -> SHIPPING hoặc SHIPPING -> DELIVERED
            Order order = orderService.getOrderById(orderId);
            
            if (order.getStatus() == OrderStatus.CONFIRMED && newStatus == OrderStatus.SHIPPING) {
                // Shipper nhận đơn
                String shipperId = (String) req.getAttribute("userId");
                orderService.assignShipper(orderId, shipperId);
                orderService.updateOrderStatus(orderId, newStatus);
            } else if (order.getStatus() == OrderStatus.SHIPPING && newStatus == OrderStatus.DELIVERED) {
                // Shipper xác nhận đã giao
                orderService.updateOrderStatus(orderId, newStatus);
            } else {
                throw new Exception("Invalid status transition");
            }

            JsonObject success = new JsonObject();
            success.addProperty("success", true);
            success.addProperty("message", "Updated status to " + statusStr);
            out.print(gson.toJson(success));

        } catch (Exception e) {
            resp.setStatus(500);
            JsonObject err = new JsonObject();
            err.addProperty("success", false);
            err.addProperty("message", e.getMessage());
            resp.getWriter().print(gson.toJson(err));
        }
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String origin = req.getHeader("Origin");
        if (origin != null && (origin.equals("http://localhost:5173"))) {
            resp.setHeader("Access-Control-Allow-Origin", origin);
        }
        resp.setHeader("Access-Control-Allow-Methods", "GET, PUT, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
        resp.setHeader("Access-Control-Allow-Credentials", "true");
        resp.setStatus(HttpServletResponse.SC_OK);
    }

    private void setHeaders(HttpServletResponse resp) {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
    }
}
