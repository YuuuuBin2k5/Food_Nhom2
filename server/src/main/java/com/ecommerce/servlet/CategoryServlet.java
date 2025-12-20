package com.ecommerce.servlet;

import com.ecommerce.entity.ProductCategory;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/api/categories")
public class CategoryServlet extends HttpServlet {
    
    private Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        try {
            List<Map<String, String>> categories = new ArrayList<>();
            
            for (ProductCategory category : ProductCategory.values()) {
                Map<String, String> categoryMap = new HashMap<>();
                categoryMap.put("value", category.name());
                categoryMap.put("name", category.getDisplayName());
                categoryMap.put("emoji", category.getEmoji());
                categoryMap.put("displayName", category.getDisplayNameWithEmoji());
                categories.add(categoryMap);
            }
            
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write(gson.toJson(categories));
            
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Không thể lấy danh sách categories");
            error.put("message", e.getMessage());
            response.getWriter().write(gson.toJson(error));
        }
    }
}
