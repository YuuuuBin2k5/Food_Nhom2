package com.ecommerce.servlet;

import com.ecommerce.entity.Order;
import com.ecommerce.entity.OrderDetail;
import com.ecommerce.entity.OrderStatus;
import com.ecommerce.entity.Product;
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
import java.util.stream.Collectors;

@WebServlet(name = "SellerOrderServlet", urlPatterns = {"/api/seller/orders", "/api/seller/orders/*"})
public class SellerOrderServlet extends HttpServlet {

    private final Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create();
    private final OrderService orderService = new OrderService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        setHeaders(resp);
        String sellerId = (String) req.getAttribute("userId"); // Từ JwtAuthFilter

        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try (PrintWriter out = resp.getWriter()) {

            // ✅ FIX: Sử dụng JOIN FETCH để load tất cả data trong 1 query
            // Tránh N+1 query problem
            String jpql = "SELECT DISTINCT o FROM Order o " +
                    "JOIN FETCH o.orderDetails od " +
                    "JOIN FETCH od.product p " +
                    "JOIN FETCH p.seller s " +
                    "JOIN FETCH o.buyer " +
                    "WHERE s.userId = :sellerId " +
                    "ORDER BY o.orderDate DESC";

            List<Order> orders = em.createQuery(jpql, Order.class)
                    .setParameter("sellerId", sellerId)
                    .getResultList();

            JsonArray jsonOrders = new JsonArray();

            for (Order o : orders) {
                JsonObject orderJson = new JsonObject();
                orderJson.addProperty("orderId", o.getOrderId());
                orderJson.addProperty("orderDate", o.getOrderDate().toString());
                orderJson.addProperty("status", o.getStatus().toString());
                orderJson.addProperty("buyerName", o.getBuyer().getFullName());

                // Tính tổng tiền CHỈ CHO CÁC MÓN CỦA SELLER NÀY
                double sellerTotal = 0;
                JsonArray itemsJson = new JsonArray();

                for (OrderDetail od : o.getOrderDetails()) {
                    Product p = od.getProduct();
                    // Chỉ lấy món của seller này
                    if (p.getSeller().getUserId().equals(sellerId)) {
                        JsonObject item = new JsonObject();
                        item.addProperty("name", p.getName());
                        item.addProperty("quantity", od.getQuantity());
                        item.addProperty("price", od.getPriceAtPurchase());
                        itemsJson.add(item);

                        sellerTotal += od.getPriceAtPurchase() * od.getQuantity();
                    }
                }

                orderJson.addProperty("totalAmount", sellerTotal);
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
            // Lấy ID từ URL: /api/seller/orders/123/status
            String pathInfo = req.getPathInfo(); // "/123/status"
            if (pathInfo == null) throw new Exception("Invalid ID");

            String[] parts = pathInfo.split("/");
            if (parts.length < 3) throw new Exception("Invalid URL format");

            Long orderId = Long.parseLong(parts[1]); // parts[0] là rỗng

            // Đọc body lấy status mới
            JsonObject body = gson.fromJson(req.getReader(), JsonObject.class);
            String statusStr = body.get("status").getAsString();
            OrderStatus newStatus = OrderStatus.valueOf(statusStr);

            // Cập nhật trạng thái
            orderService.updateOrderStatus(orderId, newStatus);

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
