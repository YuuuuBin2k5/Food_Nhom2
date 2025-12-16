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

@WebServlet("/forgot-password")
public class ForgotPasswordServlet extends HttpServlet {

    private final AuthService authService = new AuthService();
    private final Gson gson = new Gson();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        setAccessControlHeaders(resp);
        req.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        // Try form param first, then JSON body
        String email = req.getParameter("email");
        if (email == null || email.trim().isEmpty()) {
            StringBuilder sb = new StringBuilder();
            try (BufferedReader reader = req.getReader()) {
                String line;
                while ((line = reader.readLine()) != null) sb.append(line).append('\n');
            } catch (Exception ignored) {}
            String body = sb.toString().trim();
            try {
                if (!body.isEmpty()) {
                    JsonObject obj = gson.fromJson(body, JsonObject.class);
                    if (obj != null && obj.has("email")) email = obj.get("email").getAsString();
                }
            } catch (Exception ignored) {}
        }

        JsonObject respJson = new JsonObject();
        if (email == null || email.trim().isEmpty()) {
            respJson.addProperty("success", false);
            respJson.addProperty("message", "Email is required");
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        } else {
            try {
                authService.requestPasswordReset(email);
                respJson.addProperty("success", true);
                respJson.addProperty("message", "If the email exists, a reset link has been sent.");
                resp.setStatus(HttpServletResponse.SC_OK);
            } catch (Exception e) {
                respJson.addProperty("success", false);
                respJson.addProperty("message", "Failed to process request");
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        }

        try (PrintWriter out = resp.getWriter()) {
            String outStr = gson.toJson(respJson);
            System.out.println("[DEBUG] ForgotPasswordServlet response: " + outStr);
            out.print(outStr);
            out.flush();
            try { resp.flushBuffer(); } catch (IllegalStateException ignored) {}
        }
    }

    private void setAccessControlHeaders(HttpServletResponse resp) {
        resp.setHeader("Access-Control-Allow-Origin", "http://localhost:5173");
        resp.setHeader("Access-Control-Allow-Methods", "POST, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
        resp.setHeader("Access-Control-Allow-Credentials", "true");
    }
}

