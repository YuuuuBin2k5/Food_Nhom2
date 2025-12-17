package com.ecommerce.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.ecommerce.dto.ProductDTO;
import com.ecommerce.entity.Product;
import com.ecommerce.service.ProductService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/api/seller/products/*") // Map cho cả danh sách và chi tiết ID
public class SellerProductServlet extends HttpServlet {
    
    private final ProductService productService = new ProductService();
    // Cấu hình Gson để parse ngày tháng đúng định dạng yyyy-MM-dd từ frontend
    private final Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
    
    // --- 1. GET: LẤY DANH SÁCH SẢN PHẨM ---
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        setHeaders(resp);

        try (PrintWriter out = resp.getWriter()) {
            String sellerId = (String) req.getAttribute("userId");
            if (sellerId == null) {
                sendError(resp, 401, "Unauthorized: Vui lòng đăng nhập lại");
                return;
            }

            // Lấy danh sách từ Service
            List<Product> products = productService.getProductsBySeller(sellerId);
            if (products == null) products = new ArrayList<>();

            // Chuyển đổi Entity -> DTO (Tránh lỗi vòng lặp)
            List<ProductDTO> dtoList = new ArrayList<>();
            for (Product p : products) {
                ProductDTO dto = new ProductDTO(p);
                dtoList.add(dto);
            }

            out.print(gson.toJson(dtoList));

        } catch (Exception e) {
            e.printStackTrace();
            sendError(resp, 500, "Lỗi Server: " + e.getMessage());
        }
    }

    // --- 2. POST: THÊM SẢN PHẨM MỚI ---
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        setHeaders(resp);
        PrintWriter out = resp.getWriter();

        try {
            String sellerId = (String) req.getAttribute("userId");
            if (sellerId == null) {
                sendError(resp, 401, "Unauthorized");
                return;
            }

            // Parse JSON từ Body
            ProductDTO dto = gson.fromJson(req.getReader(), ProductDTO.class);

            // DEBUG: log payload to server console to help trace 500 errors in dev
            try {
                System.out.println("[SellerProductServlet] Received DTO: " + gson.toJson(dto));
            } catch (Exception ignore) {}

            // VALIDATION
            if(dto.getName() == null || dto.getName().isEmpty()) throw new Exception("Tên sản phẩm trống");
            if(dto.getSalePrice() < 0) throw new Exception("Giá bán không hợp lệ");

            // XỬ LÝ NGÀY SẢN XUẤT (Fix lỗi null do frontend bỏ trường này)
            if (dto.getManufactureDate() == null) {
                dto.setManufactureDate(new Date()); // Mặc định là hôm nay
            }

            // Gọi Service thêm mới
            productService.addProduct(sellerId, dto);
            
            // Trả về Success
            JsonObject jsonResponse = new JsonObject();
            jsonResponse.addProperty("success", true);
            jsonResponse.addProperty("message", "Thêm sản phẩm thành công");
            out.print(gson.toJson(jsonResponse));

        } catch (Exception e) {
            e.printStackTrace(); 
            sendError(resp, 500, e.getMessage());
        }
    }

    // --- 3. PUT: CẬP NHẬT SẢN PHẨM (SỬA HOẶC ẨN/HIỆN) ---
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        setHeaders(resp);
        PrintWriter out = resp.getWriter();

        try {
            String sellerId = (String) req.getAttribute("userId");
            if (sellerId == null) {
                sendError(resp, 401, "Unauthorized");
                return;
            }

            // Parse JSON
            ProductDTO dto = gson.fromJson(req.getReader(), ProductDTO.class);
            
            if (dto.getProductId() == null) throw new Exception("Thiếu Product ID để cập nhật");

            // Xử lý ngày sản xuất nếu null (để không bị lỗi khi update)
            if (dto.getManufactureDate() == null) {
                dto.setManufactureDate(new Date());
            }

            // Gọi Service cập nhật (Bạn cần đảm bảo Service có hàm updateProduct)
            // Lưu ý: Logic updateProduct trong Service phải kiểm tra xem Product có thuộc về SellerId này không
            productService.updateProduct(sellerId, dto);

            JsonObject jsonResponse = new JsonObject();
            jsonResponse.addProperty("success", true);
            jsonResponse.addProperty("message", "Cập nhật thành công");
            out.print(gson.toJson(jsonResponse));

        } catch (Exception e) {
            e.printStackTrace();
            sendError(resp, 500, e.getMessage());
        }
    }

    // --- 4. DELETE: XÓA SẢN PHẨM ---
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        setHeaders(resp);
        PrintWriter out = resp.getWriter();

        try {
            String sellerId = (String) req.getAttribute("userId");
            if (sellerId == null) {
                sendError(resp, 401, "Unauthorized");
                return;
            }

            // Lấy ID từ URL (ví dụ: /api/seller/products/123 -> lấy 123)
            String pathInfo = req.getPathInfo();
            if (pathInfo == null || pathInfo.equals("/")) {
                throw new Exception("Thiếu ID sản phẩm cần xóa");
            }
            
            // Cắt chuỗi để lấy ID (bỏ dấu / ở đầu)
            String idStr = pathInfo.substring(1); 
            Long productId = Long.parseLong(idStr);

            // Gọi Service xóa
            productService.deleteProduct(sellerId, productId);

            JsonObject jsonResponse = new JsonObject();
            jsonResponse.addProperty("success", true);
            jsonResponse.addProperty("message", "Đã xóa sản phẩm");
            out.print(gson.toJson(jsonResponse));

        } catch (Exception e) {
            e.printStackTrace();
            sendError(resp, 500, e.getMessage());
        }
    }

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
        JsonObject jsonResponse = new JsonObject();
        jsonResponse.addProperty("success", false);
        jsonResponse.addProperty("message", message);
        resp.getWriter().print(gson.toJson(jsonResponse));
    }
}