package com.ecommerce.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import com.ecommerce.dto.ProductDTO;
import com.ecommerce.entity.Product;
import com.ecommerce.service.ProductService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/api/products/*")
public class PublicProductServlet extends HttpServlet {

    private final ProductService productService = new ProductService();
    private final Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        setHeaders(resp);
        PrintWriter out = resp.getWriter();
        try {
            System.out.println("[PublicProductServlet] GET called");
            String pathInfo = req.getPathInfo(); // may be null or like "/123"
            if (pathInfo != null && pathInfo.length() > 1) {
                // detail
                String idStr = pathInfo.substring(1);
                try {
                    Long id = Long.parseLong(idStr);
                    Product p = productService.getActiveProductById(id);
                    if (p == null) {
                        sendError(resp, 404, "Product not found");
                        return;
                    }
                    ProductDTO dto = new ProductDTO(p);

                    String json = gson.toJson(dto);
                    System.out.println("[PublicProductServlet] Detail Response length: " + (json != null ? json.length() : 0));
                    out.print(json);
                    out.flush();
                    return;
                } catch (NumberFormatException nfe) {
                    sendError(resp, 400, "Invalid product id");
                    return;
                }
            }

            List<Product> products = productService.getActiveProducts();
            if (products == null) products = new ArrayList<>();

            List<ProductDTO> dtoList = new ArrayList<>();
            for (Product p : products) {
                ProductDTO dto = new ProductDTO(p);
                dtoList.add(dto);
            }

            String json = gson.toJson(dtoList);
            System.out.println("[PublicProductServlet] Response length: " + (json != null ? json.length() : 0));
            out.print(json);
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
            sendError(resp, 500, "Lỗi Server: " + e.getMessage());
        }
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String origin = req.getHeader("Origin");
        if (origin != null && (origin.equals("http://localhost:5173") || origin.equals("http://localhost:5174"))) {
            resp.setHeader("Access-Control-Allow-Origin", origin);
        }
        resp.setHeader("Access-Control-Allow-Methods", "GET, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
        resp.setHeader("Access-Control-Allow-Credentials", "true");
        resp.setStatus(HttpServletResponse.SC_OK);
    }

    private void setHeaders(HttpServletResponse resp) {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
    }

    private void sendError(HttpServletResponse resp, int statusCode, String message) throws IOException {
        resp.setStatus(statusCode);
        resp.getWriter().print(gson.toJson(java.util.Map.of("success", false, "message", message)));
    }
}
