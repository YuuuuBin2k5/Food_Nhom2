package com.ecommerce.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

import com.ecommerce.dto.ProductDTO;
import com.ecommerce.service.ProductService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/test/product")
public class TestProductServlet extends HttpServlet {

    private ProductService productService = new ProductService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        try {
            out.println("<h2>Test Product Creation</h2>");

            // Tạo một sản phẩm test
            ProductDTO dto = new ProductDTO();
            dto.setName("Test Product");
            dto.setDescription("This is a test product");
            dto.setSalePrice(50000);
            dto.setQuantity(10);
            dto.setImageUrl("https://via.placeholder.com/300");
            dto.setExpirationDate(new Date(System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000)); // 7 days from now

            // Thử tạo với một seller ID test (bạn cần thay bằng ID thật)
            String testSellerId = "test-seller-id"; // Thay bằng seller ID thật từ database

            out.println("<p>Attempting to create product...</p>");
            productService.addProduct(testSellerId, dto);
            out.println("<p style='color: green;'>✅ Product created successfully!</p>");

        } catch (Exception e) {
            out.println("<p style='color: red;'>❌ Error: " + e.getMessage() + "</p>");
            out.println("<pre>");
            e.printStackTrace(out);
            out.println("</pre>");
        }

        out.close();
    }
}