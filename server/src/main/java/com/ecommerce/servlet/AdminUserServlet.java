package com.ecommerce.servlet;

import com.ecommerce.entity.*;
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
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "AdminUserServlet", urlPatterns = {"/api/admin/users"})
public class AdminUserServlet extends HttpServlet {

    private final Gson gson = new Gson();
    private final AdminService adminService = new AdminService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        setAccessControlHeaders(response);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try (PrintWriter out = response.getWriter()) {
            String keyword = request.getParameter("keyword");
            String filter = request.getParameter("filter"); // all, sellers, buyers, shippers, banned
            
            List<User> users;
            
            // Determine which users to fetch based on filter and keyword
            if (keyword != null && !keyword.trim().isEmpty()) {
                users = adminService.searchUsers(keyword);
            } else if ("banned".equals(filter)) {
                users = adminService.getBannedUsers();
            } else if ("sellers".equals(filter)) {
                users = new ArrayList<>(adminService.getAllSellers());
            } else if ("buyers".equals(filter)) {
                users = new ArrayList<>(adminService.getAllBuyers());
            } else if ("shippers".equals(filter)) {
                users = new ArrayList<>(adminService.getAllShippers());
            } else {
                users = adminService.getAllUsers();
            }
            
            List<JsonObject> allUsers = new ArrayList<>();

            for (User user : users) {
                JsonObject userJson = new JsonObject();
                userJson.addProperty("userId", user.getUserId());
                userJson.addProperty("fullName", user.getFullName());
                userJson.addProperty("email", user.getEmail());
                userJson.addProperty("phoneNumber", user.getPhoneNumber());
                userJson.addProperty("banned", user.isBanned());
                
                // Determine role
                if (user instanceof Buyer) {
                    userJson.addProperty("role", "BUYER");
                } else if (user instanceof Seller) {
                    userJson.addProperty("role", "SELLER");
                    userJson.addProperty("shopName", ((Seller) user).getShopName());
                } else if (user instanceof Shipper) {
                    userJson.addProperty("role", "SHIPPER");
                } else if (user instanceof Admin) {
                    userJson.addProperty("role", "ADMIN");
                }
                
                allUsers.add(userJson);
            }

            out.print(gson.toJson(allUsers));
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            sendError(response, "Lỗi khi tải danh sách users");
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        setAccessControlHeaders(response);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try (PrintWriter out = response.getWriter()) {
            JsonObject requestData = gson.fromJson(request.getReader(), JsonObject.class);
            String userId = requestData.get("userId").getAsString();
            boolean banned = requestData.get("banned").getAsBoolean();

            adminService.toggleUserBan(userId, banned);

            JsonObject res = new JsonObject();
            res.addProperty("success", true);
            res.addProperty("message", banned ? "Đã khóa tài khoản" : "Đã mở khóa tài khoản");
            out.print(gson.toJson(res));
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            sendError(response, e.getMessage());
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

    private void sendError(HttpServletResponse resp, String message) throws IOException {
        JsonObject jsonResponse = new JsonObject();
        jsonResponse.addProperty("success", false);
        jsonResponse.addProperty("message", message);
        resp.getWriter().print(gson.toJson(jsonResponse));
    }
}
