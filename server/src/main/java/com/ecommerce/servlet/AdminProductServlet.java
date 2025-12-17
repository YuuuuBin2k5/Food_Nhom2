package com.ecommerce.servlet;

import com.ecommerce.dto.ProductDTO;
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
import java.util.List;

@WebServlet(name = "AdminProductServlet", urlPatterns = {"/api/admin/products", "/api/admin/products/*"})
public class AdminProductServlet extends HttpServlet {

    private final Gson gson = new Gson();
    private final AdminService adminService = new AdminService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        setAccessControlHeaders(response);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String pathInfo = request.getPathInfo();
        
        try (PrintWriter out = response.getWriter()) {
            if (pathInfo != null && pathInfo.equals("/pending")) {
                // Get first pending product
                ProductDTO product = adminService.getFirstPendingProduct();
                long pendingCount = adminService.countPendingProducts();
                
                JsonObject result = new JsonObject();
                result.addProperty("pendingCount", pendingCount);
                if (product != null) {
                    result.add("product", gson.toJsonTree(product));
                }
                out.print(gson.toJson(result));
            } else if (pathInfo != null && pathInfo.equals("/pending/all")) {
                // Get all pending products
                List<ProductDTO> products = adminService.getAllPendingProducts();
                out.print(gson.toJson(products));
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            sendError(response, "Lỗi khi tải sản phẩm");
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
            Long productId = requestData.get("productId").getAsLong();
            String action = requestData.get("action").getAsString(); // "approve" or "reject"

            if ("approve".equals(action)) {
                adminService.approveProduct(productId);
            } else if ("reject".equals(action)) {
                adminService.rejectProduct(productId);
            }

            JsonObject res = new JsonObject();
            res.addProperty("success", true);
            res.addProperty("message", "approve".equals(action) ? "Đã duyệt sản phẩm" : "Đã từ chối sản phẩm");
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
