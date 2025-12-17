package com.ecommerce.servlet;

import com.ecommerce.dto.RegisterDTO;
import com.ecommerce.service.RegisterService;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "RegisterServlet", urlPatterns = {"/api/register"})
public class RegisterServlet extends HttpServlet {

    private final Gson gson = new Gson();
    private final RegisterService registerService = new RegisterService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        setAccessControlHeaders(response);
        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try (PrintWriter out = response.getWriter();
             BufferedReader reader = request.getReader()) {

            JsonObject jsonResponse = new JsonObject();

            try {
                RegisterDTO dto = gson.fromJson(reader, RegisterDTO.class);
                
                registerService.registerUser(dto.fullName, dto.email, dto.password, dto.phoneNumber, dto.role, dto.shopName);

                jsonResponse.addProperty("status", "success");
                jsonResponse.addProperty("message", "Đăng ký thành công! Vui lòng đăng nhập.");
                response.setStatus(HttpServletResponse.SC_CREATED);

            } catch (Exception e) {
                jsonResponse.addProperty("status", "error");
                jsonResponse.addProperty("message", e.getMessage());
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }

            out.print(gson.toJson(jsonResponse));
        }
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) {
        setAccessControlHeaders(resp);
        resp.setStatus(HttpServletResponse.SC_OK);
    }

    private void setAccessControlHeaders(HttpServletResponse resp) {
        resp.setHeader("Access-Control-Allow-Origin", "http://localhost:5173");
        resp.setHeader("Access-Control-Allow-Methods", "POST, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
        resp.setHeader("Access-Control-Allow-Credentials", "true");
    }
}