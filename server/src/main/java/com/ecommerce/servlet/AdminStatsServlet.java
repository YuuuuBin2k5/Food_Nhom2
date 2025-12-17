package com.ecommerce.servlet;

import com.ecommerce.service.AdminService;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "AdminStatsServlet", urlPatterns = {"/api/admin/stats"})
public class AdminStatsServlet extends HttpServlet {

    private final Gson gson = new Gson();
    private final AdminService adminService = new AdminService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        setAccessControlHeaders(response);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try (PrintWriter out = response.getWriter()) {
            JsonObject stats = new JsonObject();
            
            // Count users by role
            stats.addProperty("totalSellers", adminService.getAllSellers().size());
            stats.addProperty("totalBuyers", adminService.getAllBuyers().size());
            stats.addProperty("totalShippers", adminService.getAllShippers().size());
            stats.addProperty("totalUsers", adminService.getAllUsers().size());
            stats.addProperty("bannedUsers", adminService.getBannedUsers().size());
            
            // Count pending approvals
            stats.addProperty("pendingSellers", adminService.countPendingSellers());
            stats.addProperty("pendingProducts", adminService.countPendingProducts());
            
            out.print(gson.toJson(stats));
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            sendError(response, "Lỗi khi tải thống kê");
        }
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) {
        setAccessControlHeaders(resp);
        resp.setStatus(HttpServletResponse.SC_OK);
    }

    private void setAccessControlHeaders(HttpServletResponse resp) {
        resp.setHeader("Access-Control-Allow-Origin", "http://localhost:5173");
        resp.setHeader("Access-Control-Allow-Methods", "GET, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
        resp.setHeader("Access-Control-Allow-Credentials", "true");
    }

    private void sendError(HttpServletResponse resp, String message) throws IOException {
        JsonObject jsonResponse = new JsonObject();
        jsonResponse.addProperty("success", false);
        jsonResponse.addProperty("message", message);
        resp.getWriter().print(gson.toJson(jsonResponse));
    }
}
