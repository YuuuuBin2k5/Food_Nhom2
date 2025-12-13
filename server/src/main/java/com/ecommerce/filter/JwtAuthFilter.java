package com.ecommerce.filter;

import com.ecommerce.util.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter("/*")
public class JwtAuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        // ✅ Lấy path CHUẨN (bỏ /server)
        String contextPath = request.getContextPath(); // /server
        String path = request.getRequestURI().substring(contextPath.length());
        String method = request.getMethod();

        // ===============================
        // 1️⃣ BYPASS PUBLIC ENDPOINTS
        // ===============================
        if (path.equals("/api/login")
                || path.equals("/api/register")
                || "OPTIONS".equalsIgnoreCase(method)) {
            chain.doFilter(req, res);
            return;
        }

        // ===============================
        // 2️⃣ CHECK JWT HEADER
        // ===============================
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String token = authHeader.substring(7);

        try {
            String userId = JwtUtil.getUserId(token);
            String role = JwtUtil.getRole(token);

            // ===============================
            // 3️⃣ ROLE-BASED AUTHORIZATION
            // ===============================

            // ADMIN APIs
            if (path.startsWith("/api/admin") && !"ADMIN".equals(role)) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                return;
            }

            // SELLER APIs
            if (path.startsWith("/api/seller") && !"SELLER".equals(role)) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                return;
            }

            // SHIPPER APIs
            if (path.startsWith("/api/shipper") && !"SHIPPER".equals(role)) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                return;
            }

            // 🔥 CHECKOUT – BUYER ONLY (URL KHÔNG CÓ buyer)
            if (path.equals("/api/checkout") && !"BUYER".equals(role)) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                return;
            }


            // ===============================
            // 4️⃣ GẮN USER INFO CHO CONTROLLER
            // ===============================
            request.setAttribute("userId", userId);
            request.setAttribute("role", role);

            chain.doFilter(req, res);

        } catch (ExpiredJwtException e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        } catch (JwtException e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }
}
