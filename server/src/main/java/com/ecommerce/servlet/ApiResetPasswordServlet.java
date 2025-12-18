package com.ecommerce.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import com.ecommerce.service.AuthService;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "ApiResetPassword", urlPatterns = {"/api/reset-password"})
public class ApiResetPasswordServlet extends HttpServlet {

    private final Gson gson = new Gson();
    private final AuthService authService = new AuthService();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        setAccessControlHeaders(resp);
        req.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = req.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) sb.append(line).append('\n');
        }
        
        String body = sb.toString().trim();
        String token = null;
        String password = null;
        try {
            if (!body.isEmpty()) {
                JsonObject obj = gson.fromJson(body, JsonObject.class);
                if (obj.has("token")) token = obj.get("token").getAsString();
                if (obj.has("password")) password = obj.get("password").getAsString();
            }
        } catch (Exception ignored) {}

        JsonObject respJson = new JsonObject();
        if (token == null || token.trim().isEmpty() || password == null || password.isEmpty()) {
            respJson.addProperty("success", false);
            respJson.addProperty("message", "Token và mật khẩu là bắt buộc");
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        } else {
            try {
                boolean ok = authService.resetPassword(token, password);
                if (ok) {
                    respJson.addProperty("success", true);
                    respJson.addProperty("message", "Đặt lại mật khẩu thành công");
                    resp.setStatus(HttpServletResponse.SC_OK);
                } else {
                    respJson.addProperty("success", false);
                    respJson.addProperty("message", "Token không hợp lệ hoặc đã hết hạn");
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                }
            } catch (Exception e) {
                respJson.addProperty("success", false);
                respJson.addProperty("message", "Lỗi server");
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        }

        try (PrintWriter out = resp.getWriter()) {
            out.print(gson.toJson(respJson));
            out.flush();
        }
    }

    private void setAccessControlHeaders(HttpServletResponse resp) {
        resp.setHeader("Access-Control-Allow-Origin", "http://localhost:5173");
        resp.setHeader("Access-Control-Allow-Methods", "POST, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
        resp.setHeader("Access-Control-Allow-Credentials", "true");
    }
}
