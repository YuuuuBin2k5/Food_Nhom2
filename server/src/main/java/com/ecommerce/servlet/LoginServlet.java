package com.ecommerce.servlet;

import com.ecommerce.dto.UserDB;
import com.ecommerce.entity.User;
import com.ecommerce.entity.Seller;
import com.ecommerce.entity.Buyer;
import com.ecommerce.entity.Shipper;
import com.ecommerce.entity.Admin;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns = {"/api/login"})
public class LoginServlet extends HttpServlet {

    private Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        setAccessControlHeaders(response);
        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        PrintWriter out = response.getWriter();
        JsonObject jsonResponse = new JsonObject();

        try {
            BufferedReader reader = request.getReader();
            LoginDTO loginData = gson.fromJson(reader, LoginDTO.class);

            // 1. Gọi DB kiểm tra (JPA sẽ tự động trả về đúng class con: Seller/Buyer...)
            User user = UserDB.checkLogin(loginData.email, loginData.password);

            if (user != null) {
                jsonResponse.addProperty("status", "success");
                jsonResponse.addProperty("message", "Đăng nhập thành công");
                // Token giả lập (Có thể thêm role vào token nếu muốn)
                jsonResponse.addProperty("token", "fake-jwt-token-" + user.getUserId());

                // 2. XÁC ĐỊNH VAI TRÒ (ROLE) DỰA TRÊN CLASS DIAGRAM
                String role = "UNKNOWN";
                JsonObject extraInfo = new JsonObject();

                if (user instanceof Admin) {
                    role = "ADMIN";
                    // Admin không có thuộc tính riêng đặc biệt trong diagram để trả về thêm
                } else if (user instanceof Seller) {
                    role = "SELLER";
                    Seller seller = (Seller) user;
                    extraInfo.addProperty("shopName", seller.getShopName());
                    extraInfo.addProperty("revenue", seller.getRevenue());
                } else if (user instanceof Buyer) {
                    role = "BUYER";
                    Buyer buyer = (Buyer) user;
                    // Có thể trả về số lượng địa chỉ đã lưu
                    extraInfo.addProperty("savedAddressCount", 
                        buyer.getSavedAddresses() != null ? buyer.getSavedAddresses().size() : 0);
                } else if (user instanceof Shipper) {
                    role = "SHIPPER";
                }

                // 3. Đóng gói JSON trả về React
                JsonObject userJson = new JsonObject();
                userJson.addProperty("userId", user.getUserId());
                userJson.addProperty("fullName", user.getFullName());
                userJson.addProperty("email", user.getEmail());
                userJson.addProperty("phoneNumber", user.getPhoneNumber());
                userJson.addProperty("role", role); // React sẽ dùng cái này để chuyển trang
                
                // Gắn thêm thông tin riêng (ví dụ ShopName cho Seller)
                userJson.add("extraInfo", extraInfo);

                jsonResponse.add("user", userJson);
                response.setStatus(200);
            } else {
                jsonResponse.addProperty("status", "error");
                jsonResponse.addProperty("message", "Email hoặc mật khẩu không đúng!");
                response.setStatus(401);
            }

        } catch (Exception e) {
            e.printStackTrace();
            jsonResponse.addProperty("status", "error");
            jsonResponse.addProperty("message", "Lỗi server: " + e.getMessage());
            response.setStatus(500);
        }

        out.print(gson.toJson(jsonResponse));
        out.flush();
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        setAccessControlHeaders(resp);
        resp.setStatus(HttpServletResponse.SC_OK);
    }

    private void setAccessControlHeaders(HttpServletResponse resp) {
        resp.setHeader("Access-Control-Allow-Origin", "http://localhost:5173");
        resp.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
        resp.setHeader("Access-Control-Allow-Credentials", "true");
    }

    private static class LoginDTO {
        String email;
        String password;
    }
}