package com.ecommerce.servlet;

import com.ecommerce.dto.SellerDTO;
import com.ecommerce.service.AdminService;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "AdminSellerServlet", urlPatterns = {"/api/admin/sellers/*"})
public class AdminSellerServlet extends HttpServlet {

    private final Gson gson = new Gson();
    private final AdminService adminService = new AdminService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        setAccessControlHeaders(response);

        String pathInfo = request.getPathInfo();
        
        if (pathInfo != null && pathInfo.equals("/pending")) {
            try {
                SellerDTO seller = adminService.getFirstPendingSeller();
                long pendingCount = adminService.countPendingSellers();
                
                JsonObject result = new JsonObject();
                result.addProperty("pendingCount", pendingCount);
                if (seller != null) {
                    result.add("seller", gson.toJsonTree(seller));
                }
                
                response.getWriter().write(gson.toJson(result));
            } catch (Exception e) {
                e.printStackTrace();
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                
                JsonObject error = new JsonObject();
                error.addProperty("success", false);
                error.addProperty("message", e.getMessage() != null ? e.getMessage() : "Internal server error");
                response.getWriter().write(gson.toJson(error));
            }
        } else if (pathInfo != null && pathInfo.equals("/pending/all")) {
            try {
                List<SellerDTO> sellers = adminService.getAllPendingSellers();
                response.getWriter().write(gson.toJson(sellers));
            } catch (Exception e) {
                e.printStackTrace();
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                
                JsonObject error = new JsonObject();
                error.addProperty("success", false);
                error.addProperty("message", e.getMessage() != null ? e.getMessage() : "Internal server error");
                response.getWriter().write(gson.toJson(error));
            }
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("{\"success\":false,\"message\":\"Endpoint not found\"}");
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        setAccessControlHeaders(response);

        try {
            JsonObject requestData = gson.fromJson(request.getReader(), JsonObject.class);
            String sellerId = requestData.get("sellerId").getAsString();
            String action = requestData.get("action").getAsString();

            if ("approve".equals(action)) {
                adminService.approveSeller(sellerId);
            } else if ("reject".equals(action)) {
                adminService.rejectSeller(sellerId);
            }

            JsonObject res = new JsonObject();
            res.addProperty("success", true);
            res.addProperty("message", "approve".equals(action) ? "Đã duyệt seller" : "Đã từ chối seller");
            response.getWriter().write(gson.toJson(res));
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"success\":false,\"message\":\"" + e.getMessage() + "\"}");
        }
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) {
        setAccessControlHeaders(resp);
        resp.setStatus(HttpServletResponse.SC_OK);
    }

    private void setAccessControlHeaders(HttpServletResponse resp) {
        resp.setHeader("Access-Control-Allow-Origin", "http://localhost:5173");
        resp.setHeader("Access-Control-Allow-Methods", "GET, PUT, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
        resp.setHeader("Access-Control-Allow-Credentials", "true");
    }
}
