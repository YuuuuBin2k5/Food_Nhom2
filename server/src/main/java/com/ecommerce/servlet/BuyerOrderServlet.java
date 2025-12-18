package com.ecommerce.servlet;

import com.ecommerce.entity.Order;
import com.ecommerce.entity.OrderDetail;
import com.ecommerce.entity.OrderStatus;
import com.ecommerce.service.OrderService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.*;
import java.util.List;

@WebServlet("/api/buyer/orders/*")
public class BuyerOrderServlet extends HttpServlet {

    private final Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create();
    private final OrderService orderService = new OrderService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        setHeaders(resp);
        String buyerId = (String) req.getAttribute("userId");

        try (PrintWriter out = resp.getWriter()) {
            // Sử dụng hàm getOrdersByBuyer đã tối ưu Lazy Loading
            List<Order> orders = orderService.getOrdersByBuyer(buyerId);

            JsonArray jsonOrders = new JsonArray();
            for (Order o : orders) {
                JsonObject orderJson = new JsonObject();
                orderJson.addProperty("orderId", o.getOrderId());
                orderJson.addProperty("orderDate", o.getOrderDate().toString());
                orderJson.addProperty("status", o.getStatus().toString());
                orderJson.addProperty("shippingAddress", o.getShippingAddress());
                orderJson.addProperty("totalAmount", o.getPayment().getAmount());
                orderJson.addProperty("paymentMethod", o.getPayment().getMethod().toString());

                JsonArray items = new JsonArray();
                for (OrderDetail od : o.getOrderDetails()) {
                    JsonObject item = new JsonObject();
                    item.addProperty("productId", od.getProduct().getProductId());
                    item.addProperty("name", od.getProduct().getName());
                    item.addProperty("quantity", od.getQuantity());
                    item.addProperty("price", od.getPriceAtPurchase());
                    item.addProperty("imageUrl", od.getProduct().getImageUrl());
                    item.addProperty("shopName", od.getProduct().getSeller().getShopName());
                    items.add(item);
                }
                orderJson.add("items", items);
                jsonOrders.add(orderJson);
            }
            out.print(gson.toJson(jsonOrders));
        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(500);
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        setHeaders(resp);
        String userId = (String) req.getAttribute("userId");

        try (PrintWriter out = resp.getWriter()) {
            String pathInfo = req.getPathInfo();
            Long orderId = Long.parseLong(pathInfo.split("/")[1]);

            // Gọi service hủy đơn (đã bao gồm check quyền Buyer và hoàn kho)
            orderService.cancelOrder(orderId, userId, "BUYER");

            JsonObject success = new JsonObject();
            success.addProperty("success", true);
            success.addProperty("message", "Đã hủy đơn hàng");
            out.print(gson.toJson(success));

        } catch (Exception e) {
            resp.setStatus(500);
            JsonObject err = new JsonObject();
            err.addProperty("message", e.getMessage());
            resp.getWriter().print(gson.toJson(err));
        }
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) {
        setHeaders(resp);
        resp.setStatus(200);
    }

    private void setHeaders(HttpServletResponse resp) {
        resp.setHeader("Access-Control-Allow-Origin", "http://localhost:5173");
        resp.setHeader("Access-Control-Allow-Methods", "GET, PUT, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
        resp.setHeader("Access-Control-Allow-Credentials", "true");
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
    }
}