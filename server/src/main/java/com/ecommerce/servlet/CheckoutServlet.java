package com.ecommerce.servlet;

import com.ecommerce.dto.CheckoutRequest;
import com.ecommerce.dto.CheckoutResponse;
import com.ecommerce.service.OrderService;
import com.google.gson.Gson;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/api/checkout")
public class CheckoutServlet extends HttpServlet {

    private final OrderService orderService = new OrderService();
    private final Gson gson = new Gson();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        PrintWriter out = resp.getWriter();

        try {
            String userId = (String) req.getAttribute("userId");
            if (userId == null) {
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                out.print(gson.toJson(CheckoutResponse.error("Unauthorized")));
                return;
            }

            BufferedReader reader = req.getReader();
            CheckoutRequest request = gson.fromJson(reader, CheckoutRequest.class);
            request.setUserId(userId);

            if (request.getShippingAddress() == null || request.getShippingAddress().isEmpty()) {
                throw new IllegalArgumentException("Shipping address is required");
            }

            if (request.getItems() == null || request.getItems().isEmpty()) {
                throw new IllegalArgumentException("Cart is empty");
            }

            // Gọi service (đã sửa để trả về List ID)
            List<String> orderIds = orderService.placeOrder(request);

            // Gộp các ID lại thành chuỗi để trả về frontend (VD: "101, 102")
            String combinedOrderIds = String.join(", ", orderIds);

            resp.setStatus(HttpServletResponse.SC_OK);
            out.print(gson.toJson(CheckoutResponse.success(combinedOrderIds)));

        } catch (IllegalArgumentException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gson.toJson(CheckoutResponse.error(e.getMessage())));
        } catch (Exception e) {
            e.printStackTrace(); // Log lỗi server để debug
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gson.toJson(CheckoutResponse.error("Lỗi đặt hàng: " + e.getMessage())));
        } finally {
            out.close();
        }
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
        resp.setStatus(HttpServletResponse.SC_OK);
    }
}