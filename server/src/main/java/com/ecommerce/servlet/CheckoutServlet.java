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

@WebServlet("/api/checkout")
public class CheckoutServlet extends HttpServlet {
    
    private OrderService orderService = new OrderService();
    private Gson gson = new Gson();
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        
        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type");
        
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        
        PrintWriter out = resp.getWriter();
        
        try {
            StringBuilder requestBody = new StringBuilder();
            String line;
            BufferedReader reader = req.getReader();
            
            while ((line = reader.readLine()) != null) {
                requestBody.append(line);
            }
            
            String jsonRequest = requestBody.toString();
            
            CheckoutRequest request = gson.fromJson(jsonRequest, CheckoutRequest.class);
            
            if (request.getUserId() == null || request.getUserId().isEmpty()) {
                throw new IllegalArgumentException("User ID is required");
            }
            
            if (request.getShippingAddress() == null || request.getShippingAddress().isEmpty()) {
                throw new IllegalArgumentException("Shipping address is required");
            }
            
            if (request.getItems() == null || request.getItems().isEmpty()) {
                throw new IllegalArgumentException("Cart is empty");
            }
            
            if (request.getPaymentMethod() == null || request.getPaymentMethod().isEmpty()) {
                throw new IllegalArgumentException("Payment method is required");
            }
            
            String orderId = orderService.placeOrder(request);
            
            CheckoutResponse response = CheckoutResponse.success(orderId);
            String jsonResponse = gson.toJson(response);
            
            resp.setStatus(HttpServletResponse.SC_OK);
            out.print(jsonResponse);
            out.flush();
            
        } catch (IllegalArgumentException e) {
            CheckoutResponse response = CheckoutResponse.error(e.getMessage());
            String jsonResponse = gson.toJson(response);
            
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(jsonResponse);
            out.flush();
            
        } catch (Exception e) {
            CheckoutResponse response = CheckoutResponse.error(e.getMessage());
            String jsonResponse = gson.toJson(response);
            
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(jsonResponse);
            out.flush();
            
        } finally {
            out.close();
        }
    }
    
    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        
        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type");
        resp.setStatus(HttpServletResponse.SC_OK);
    }
}
