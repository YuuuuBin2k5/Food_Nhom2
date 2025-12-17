package com.ecommerce.filter;

import java.io.IOException;
import java.io.PrintWriter;

import com.ecommerce.util.JwtUtil;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebFilter("/*")
public class JwtAuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        // ===============================
        // 1️⃣ Set CORS headers cho mọi request (cho môi trường dev: 5173/5174)
        // ===============================
        String origin = request.getHeader("Origin");
        if (origin != null) {
            if (origin.equals("http://localhost:5173") || origin.equals("http://localhost:5174")
                    || origin.equals("http://127.0.0.1:5173") || origin.equals("http://127.0.0.1:5174")) {
                response.setHeader("Access-Control-Allow-Origin", origin);
                response.setHeader("Access-Control-Allow-Credentials", "true");
            }
        }
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Authorization, Content-Type");

        // OPTIONS request → trả luôn 200
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        // ===============================
        // 2️⃣ Bypass public endpoints
        // ===============================
        String contextPath = request.getContextPath();
        String path = request.getRequestURI().substring(contextPath.length());
        if (path.equals("/api/login") ||
            path.equals("/api/register") ||
            path.equals("/api/forgot-password") ||
            path.equals("/forgot-password") ||
            path.equals("/api/reset-password") ||
            path.equals("/reset-password") ||
            path.equals("/api/ping")) {
            chain.doFilter(req, res);
            return;
        }

        // Publicly allow GET /api/products (product listing) without auth
        if ((path.equals("/api/products") || path.startsWith("/api/products/"))
                && "GET".equalsIgnoreCase(request.getMethod())) {
            chain.doFilter(req, res);
            return;
        }

        // ===============================
        // 3️⃣ Check Authorization header
        // ===============================
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            sendJsonError(response, HttpServletResponse.SC_UNAUTHORIZED, "Missing or invalid Authorization header");
            return;
        }

        String token = authHeader.substring(7);

        try {
            String userId = JwtUtil.getUserId(token);
            String role = JwtUtil.getRole(token);

            // ===============================
            // 4️⃣ Role-based authorization
            // ===============================
            if (path.startsWith("/api/admin") && !"ADMIN".equals(role)) {
                sendJsonError(response, HttpServletResponse.SC_FORBIDDEN, "Forbidden: Admin only");
                return;
            }

            if (path.startsWith("/api/seller") && !"SELLER".equals(role)) {
                sendJsonError(response, HttpServletResponse.SC_FORBIDDEN, "Forbidden: Seller only");
                return;
            }

            if (path.startsWith("/api/shipper") && !"SHIPPER".equals(role)) {
                sendJsonError(response, HttpServletResponse.SC_FORBIDDEN, "Forbidden: Shipper only");
                return;
            }

            if (path.equals("/api/checkout") && !"BUYER".equals(role)) {
                sendJsonError(response, HttpServletResponse.SC_FORBIDDEN, "Forbidden: Buyer only");
                return;
            }

            // ===============================
            // 5️⃣ Gắn thông tin user cho Controller
            // ===============================
            request.setAttribute("userId", userId);
            request.setAttribute("role", role);

            // Continue chain
            chain.doFilter(req, res);

        } catch (ExpiredJwtException e) {
            sendJsonError(response, HttpServletResponse.SC_UNAUTHORIZED, "Token expired");
        } catch (JwtException e) {
            sendJsonError(response, HttpServletResponse.SC_UNAUTHORIZED, "Invalid token");
        }
    }

    private void sendJsonError(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String json = String.format("{\"success\":false,\"message\":\"%s\"}", message.replace("\"","\\\""));

        // ❌ KHÔNG close PrintWriter → chỉ flush
        PrintWriter out = response.getWriter();
        out.print(json);
        out.flush();
    }
}
