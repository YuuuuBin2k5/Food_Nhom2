/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.ecommerce.servlet;

import com.ecommerce.dto.UserDB;
import com.ecommerce.entity.User;
import com.ecommerce.entity.Buyer;
import com.ecommerce.entity.Seller;
import com.ecommerce.entity.Shipper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 *
 * @author Admin
 */
@WebServlet(urlPatterns = {"/api/register"})
public class RegisterServlet extends HttpServlet {
    
    private Gson gson = new Gson();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // 1. Cấu hình Header cho React gọi vào
        setAccessControlHeaders(response);
        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        PrintWriter out = response.getWriter();
        JsonObject jsonResponse = new JsonObject();

        try {
            // 2. Đọc dữ liệu JSON từ React
            BufferedReader reader = request.getReader();
            RegisterDTO regData = gson.fromJson(reader, RegisterDTO.class);

            // 3. Validate cơ bản
            if (UserDB.emailExists(regData.email)) {
                jsonResponse.addProperty("status", "error");
                jsonResponse.addProperty("message", "Email này đã được sử dụng!");
                response.setStatus(409); // Conflict
                out.print(gson.toJson(jsonResponse));
                return;
            }

            // 4. KHỞI TẠO ĐÚNG LOẠI USER (Theo Diagram)
            User newUser = null;
            String role = regData.role != null ? regData.role.toUpperCase() : "BUYER";

            switch (role) {
                case "SELLER":
                    Seller seller = new Seller();
                    // Seller có thuộc tính riêng là shopName
                    if (regData.shopName == null || regData.shopName.isEmpty()) {
                        throw new Exception("Người bán phải có tên cửa hàng!");
                    }
                    seller.setShopName(regData.shopName);
                    seller.setRevenue(0.0); // Mặc định doanh thu = 0
                    // seller.setRating(0.0f); 
                    newUser = seller; 
                    break;
                    
                case "SHIPPER":
                    Shipper shipper = new Shipper();
                    // Setup thuộc tính riêng của Shipper nếu có
                    newUser = shipper;
                    break;
                    
                case "BUYER":
                default:
                    Buyer buyer = new Buyer();      
                    newUser = buyer;
                    break;
            }

            // 5. Điền các thông tin chung
            newUser.setFullName(regData.fullName);
            newUser.setEmail(regData.email);
            newUser.setPassword(regData.password); 
            newUser.setPhoneNumber(regData.phoneNumber);
            newUser.setCreatedDate(new Date());

            // 6. Lưu xuống DB 
            UserDB.insert(newUser);

            // 7. Trả về thành công
            jsonResponse.addProperty("status", "success");
            jsonResponse.addProperty("message", "Đăng ký thành công! Vui lòng đăng nhập.");
            response.setStatus(201); // Created

        } catch (Exception e) {
            e.printStackTrace();
            jsonResponse.addProperty("status", "error");
            jsonResponse.addProperty("message", "Lỗi: " + e.getMessage());
            response.setStatus(500);
        }

        out.print(gson.toJson(jsonResponse));
        out.flush();
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        setAccessControlHeaders(resp);
        resp.setStatus(HttpServletResponse.SC_OK);
    }

    private void setAccessControlHeaders(HttpServletResponse resp) {
        resp.setHeader("Access-Control-Allow-Origin", "http://localhost:5173");
        resp.setHeader("Access-Control-Allow-Methods", "POST, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type");
    }
    
    private static class RegisterDTO {
    private String fullName;
    private String email;
    private String password;
    private String phoneNumber;
    private String role;
    private String shopName;
}
}
