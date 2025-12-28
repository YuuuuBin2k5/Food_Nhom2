package com.ecommerce.servlet;

import com.ecommerce.dto.ProductDTO;
import com.ecommerce.entity.ProductCategory;
import com.ecommerce.entity.ProductStatus;
import com.ecommerce.entity.User;
import com.ecommerce.service.ProductService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@WebServlet(name = "SellerProductAPIServlet", urlPatterns = {"/api/seller/products", "/api/seller/products/*"})
public class SellerProductAPIServlet extends HttpServlet {

    private final ProductService productService = new ProductService();
    private final Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        handleRequest(request, response, "POST");
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        handleRequest(request, response, "PUT");
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        handleRequest(request, response, "DELETE");
    }

    // PATCH is not standard in HttpServlet, usually simulated or need to override service()
    // But since frontend sends PATCH, we should handle it.
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (req.getMethod().equalsIgnoreCase("PATCH")) {
            handleRequest(req, resp, "PATCH");
        } else {
            super.service(req, resp);
        }
    }

    private void handleRequest(HttpServletRequest request, HttpServletResponse response, String method) 
            throws IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\": \"Unauthorized\"}");
            return;
        }

        User user = (User) session.getAttribute("user");
        if (!"SELLER".equals(user.getRole())) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("{\"error\": \"Forbidden\"}");
            return;
        }

        String pathInfo = request.getPathInfo(); // /id or null

        try {
            if ("POST".equals(method)) {
                // Create
                ProductDTO dto = parseBody(request);
                productService.addProduct(user.getUserId(), dto);
                response.getWriter().write("{\"message\": \"Success\"}");
                
            } else if ("PUT".equals(method)) {
                // Update
                if (pathInfo == null || pathInfo.equals("/")) {
                    throw new IllegalArgumentException("Missing product ID");
                }
                String idStr = pathInfo.substring(1);
                ProductDTO dto = parseBody(request);
                dto.setProductId(Long.parseLong(idStr)); // Ensure ID matches URL
                
                productService.updateProduct(user.getUserId(), dto);
                response.getWriter().write("{\"message\": \"Success\"}");
                
            } else if ("DELETE".equals(method)) {
                // Delete
                if (pathInfo == null || pathInfo.equals("/")) {
                    throw new IllegalArgumentException("Missing product ID");
                }
                Long productId = Long.parseLong(pathInfo.substring(1));
                productService.deleteProduct(user.getUserId(), productId);
                response.getWriter().write("{\"message\": \"Success\"}");
                
            } else if ("PATCH".equals(method)) {
                 // Update Status (e.g. /123/status)
                 // Or just /123 if body has status? 
                 // Frontend: /api/seller/products/123/status
                 
                 String[] parts = pathInfo.split("/");
                 if (parts.length < 2) throw new IllegalArgumentException("Invalid path");
                 
                 Long productId = Long.parseLong(parts[1]);
                 
                 // Expect body: { "status": "INACTIVE" }
                 JsonObject json = gson.fromJson(request.getReader(), JsonObject.class);
                 String statusStr = json.get("status").getAsString();
                 ProductStatus status = ProductStatus.valueOf(statusStr);
                 
                 // We need a specific method for updating status or use updateProduct
                 // updateProduct requires full DTO. 
                 // Let's perform a fetch-modify-save or add a method in service.
                 // For now, let's fetch DTO, update status, and save.
                 
                 ProductDTO dto = productService.getProductById(productId);
                 dto.setStatus(status);
                 productService.updateProduct(user.getUserId(), dto);
                 
                 response.getWriter().write("{\"message\": \"Success\"}");
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    private ProductDTO parseBody(HttpServletRequest request) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = request.getReader();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        return gson.fromJson(sb.toString(), ProductDTO.class);
    }
}
