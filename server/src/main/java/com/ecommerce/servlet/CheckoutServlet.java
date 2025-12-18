package com.ecommerce.servlet;

import com.ecommerce.dto.CheckoutRequest;
import com.ecommerce.dto.CheckoutResponse;
import com.ecommerce.service.OrderService;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.*;
import java.util.List;

@WebServlet("/api/checkout")
public class CheckoutServlet extends HttpServlet {
    private final OrderService orderService = new OrderService();
    private final Gson gson = new Gson();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        setCors(resp);

        try {
            String userId = (String) req.getAttribute("userId");
            CheckoutRequest request = gson.fromJson(req.getReader(), CheckoutRequest.class);
            request.setUserId(userId);

            // Gọi service (giờ trả về List<String> id các đơn hàng con)
            List<String> orderIds = orderService.placeOrder(request);

            // Trả về ID đơn hàng đầu tiên hoặc chuỗi gộp để Frontend hiển thị
            String combinedOrderId = String.join(", ", orderIds);

            PrintWriter out = resp.getWriter();
            out.print(gson.toJson(CheckoutResponse.success(combinedOrderId)));
            out.flush();

        } catch (Exception e) {
            resp.setStatus(500);
            resp.getWriter().print(gson.toJson(CheckoutResponse.error(e.getMessage())));
        }
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) {
        setCors(resp);
        resp.setStatus(200);
    }

    private void setCors(HttpServletResponse resp) {
        resp.setHeader("Access-Control-Allow-Origin", "http://localhost:5173");
        resp.setHeader("Access-Control-Allow-Methods", "POST, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
        resp.setHeader("Access-Control-Allow-Credentials", "true");
    }
}