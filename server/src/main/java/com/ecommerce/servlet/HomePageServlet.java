package com.ecommerce.servlet;

import java.io.IOException;

import com.ecommerce.entity.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "HomePageServlet", urlPatterns = { "", "/" })
public class HomePageServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        System.out.println("[HomePage] Starting home page request");

        // Check authentication
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            System.out.println("[HomePage] No session or user, redirecting to login");
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        User user = (User) session.getAttribute("user");
        String role = (String) session.getAttribute("role");

        System.out.println("[HomePage] User: " + user.getUserId() + ", Role: " + role);

        // Redirect to appropriate dashboard based on role
        switch (role) {
            case "SELLER":
                System.out.println("[HomePage] Redirecting seller to dashboard");
                response.sendRedirect(request.getContextPath() + "/seller/dashboard");
                return;

            case "ADMIN":
                System.out.println("[HomePage] Redirecting admin to dashboard");
                response.sendRedirect(request.getContextPath() + "/admin/dashboard");
                return;

            case "BUYER":
            case "SHIPPER":
            default:
                System.out.println("[HomePage] Redirecting to buyer home");
                response.sendRedirect(request.getContextPath() + "/buyer/home.jsp");
                return;
        }
    }
}