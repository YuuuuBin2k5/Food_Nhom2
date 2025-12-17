package com.ecommerce.servlet;

import com.ecommerce.dto.SellerUpdateDTO;
import com.ecommerce.service.SellerService;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/api/seller/profile")
public class SellerProfileServlet extends HttpServlet {
    private final SellerService sellerService = new SellerService();
    private final Gson gson = new Gson();

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        setHeaders(resp);
        String sellerId = (String) req.getAttribute("userId"); // Lấy từ JWT
        
        try (PrintWriter out = resp.getWriter()) {
            SellerUpdateDTO dto = gson.fromJson(req.getReader(), SellerUpdateDTO.class);
            sellerService.updateSellerProfile(sellerId, dto);
            
            JsonObject json = new JsonObject();
            json.addProperty("success", true);
            json.addProperty("message", "Cập nhật thông tin thành công");
            out.print(gson.toJson(json));
        } catch (Exception e) {
            resp.setStatus(500);
            resp.getWriter().write(gson.toJson("Error: " + e.getMessage()));
        }
    }
    
    // --- OPTIONS (CORS Preflight) ---
    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String origin = req.getHeader("Origin");
        if (origin != null && (origin.equals("http://localhost:5173"))) {
            resp.setHeader("Access-Control-Allow-Origin", origin);
        }
        resp.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
        resp.setHeader("Access-Control-Allow-Credentials", "true");
        resp.setStatus(HttpServletResponse.SC_OK);
    }

    // --- HELPER ---
    private void setHeaders(HttpServletResponse resp) {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
    }

    private void sendError(HttpServletResponse resp, int statusCode, String message) throws IOException {
        resp.setStatus(statusCode);
        JsonObject json = new JsonObject();
        json.addProperty("success", false);
        json.addProperty("message", message);
        resp.getWriter().print(gson.toJson(json));
    }
}