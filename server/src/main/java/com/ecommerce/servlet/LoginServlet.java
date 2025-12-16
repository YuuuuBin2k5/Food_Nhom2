package com.ecommerce.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter; // 1. Import Service

import com.ecommerce.dto.LoginDTO;
import com.ecommerce.entity.Admin;
import com.ecommerce.entity.Buyer;
import com.ecommerce.entity.Seller;
import com.ecommerce.entity.Shipper;
import com.ecommerce.entity.User;
import com.ecommerce.service.LoginService;
import com.ecommerce.util.JwtUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "LoginServlet", urlPatterns = {"/api/login"})
public class LoginServlet extends HttpServlet {

    private final Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
    
    private final LoginService loginService = new LoginService(); 

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        setAccessControlHeaders(response);
        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try (PrintWriter out = response.getWriter();
             BufferedReader reader = request.getReader()) {

            LoginDTO loginData = gson.fromJson(reader, LoginDTO.class);
            JsonObject jsonResponse = new JsonObject();

            try {
                User user = loginService.login(loginData.email, loginData.password);

                // Xử lý Role và Extra Info
                String role = "UNKNOWN";
                JsonObject extraInfo = new JsonObject();

                if (user instanceof Admin) {
                    role = "ADMIN";
                } else if (user instanceof Seller) {
                    role = "SELLER";
                    Seller seller = (Seller) user;
                    extraInfo.addProperty("shopName", seller.getShopName());
                    extraInfo.addProperty("revenue", seller.getRevenue());
                } else if (user instanceof Buyer) {
                    role = "BUYER";
                    // Don't access lazy collections - just set a placeholder
                    extraInfo.addProperty("savedAddressCount", 0);
                } else if (user instanceof Shipper) {
                    role = "SHIPPER";
                }
                
                // Tạo JWT
                String jwtToken = JwtUtil.generateToken(user.getUserId(), role);
                
                jsonResponse.addProperty("success", true);
                jsonResponse.addProperty("message", "Đăng nhập thành công");
                jsonResponse.addProperty("token", jwtToken);
                
                // Đóng gói JSON trả về
                JsonObject userJson = new JsonObject();
                userJson.addProperty("userId", user.getUserId());
                userJson.addProperty("fullName", user.getFullName());
                userJson.addProperty("email", user.getEmail());
                userJson.addProperty("phoneNumber", user.getPhoneNumber());
                userJson.addProperty("role", role);
                userJson.add("extraInfo", extraInfo);

                jsonResponse.add("user", userJson);
                response.setStatus(HttpServletResponse.SC_OK);

            } catch (Exception e) {
                jsonResponse.addProperty("success", false);
                jsonResponse.addProperty("message", "Email hoặc mật khẩu không đúng");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            }

            out.print(gson.toJson(jsonResponse));
            
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) {
        setAccessControlHeaders(resp);
        resp.setStatus(HttpServletResponse.SC_OK);
    }

    private void setAccessControlHeaders(HttpServletResponse resp) {
        resp.setHeader("Access-Control-Allow-Origin", "http://localhost:5173");
        resp.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
        resp.setHeader("Access-Control-Allow-Credentials", "true");
    }

    
}