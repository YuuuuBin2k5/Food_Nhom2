package com.ecommerce.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.ecommerce.entity.User;
import com.ecommerce.util.MenuHelper;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "OrderSuccessServlet", urlPatterns = {"/order-success"})
public class OrderSuccessServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Check authentication
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        // Set menu items for buyer
        String role = (String) session.getAttribute("role");
        MenuHelper.setMenuItems(request, role != null ? role : "BUYER", "/order-success");
        
        try {
            // Get parameters
            String orderIdParam = request.getParameter("orderId");
            String totalParam = request.getParameter("total");
            String paymentMethod = request.getParameter("paymentMethod");
            
            if (orderIdParam == null || totalParam == null) {
                response.sendRedirect(request.getContextPath() + "/");
                return;
            }
            
            // Parse order IDs (can be comma-separated for multiple orders)
            List<String> orderIds = new ArrayList<>();
            if (orderIdParam.contains(",")) {
                orderIds = Arrays.asList(orderIdParam.split(","));
            } else {
                orderIds.add(orderIdParam);
            }
            
            // Parse total
            double total = Double.parseDouble(totalParam);
            
            // Set attributes
            request.setAttribute("orderIds", orderIds);
            request.setAttribute("orderId", orderIds.get(0)); // First order ID for single display
            request.setAttribute("total", total);
            request.setAttribute("paymentMethod", paymentMethod != null ? paymentMethod : "COD");
            
            // Forward to JSP
            request.getRequestDispatcher("/buyer/order-success.jsp").forward(request, response);
            
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/");
        }
    }
}
