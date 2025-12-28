package com.ecommerce.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ecommerce.entity.Product;
import com.ecommerce.entity.ProductCategory;
import com.ecommerce.entity.User;
import com.ecommerce.service.ProductService;
import com.ecommerce.util.MenuHelper;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "HomePageServlet", urlPatterns = {"", "/"})
public class HomePageServlet extends HttpServlet {

    private final ProductService productService = new ProductService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Check authentication
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        User user = (User) session.getAttribute("user");
        String role = (String) session.getAttribute("role");
        
        // Set menu items using MenuHelper
        MenuHelper.setMenuItems(request, role, "/");
        
        try {
            // Get filter parameters
            String category = request.getParameter("category");
            String search = request.getParameter("search");
            String sortBy = request.getParameter("sortBy");
            
            // Fetch products
            List<Product> products;
            if (search != null && !search.trim().isEmpty()) {
                products = productService.searchProducts(search);
            } else if (category != null && !category.trim().isEmpty()) {
                products = productService.getProductsByCategory(category);
            } else {
                products = productService.getActiveProducts();
            }
            
            // Sort if needed
            if ("price_asc".equals(sortBy)) {
                products.sort((p1, p2) -> Double.compare(p1.getSalePrice(), p2.getSalePrice()));
            } else if ("price_desc".equals(sortBy)) {
                products.sort((p1, p2) -> Double.compare(p2.getSalePrice(), p1.getSalePrice()));
            } else if ("name_asc".equals(sortBy)) {
                products.sort((p1, p2) -> p1.getName().compareToIgnoreCase(p2.getName()));
            }
            
            // Set all categories from backend
            request.setAttribute("categories", ProductCategory.values());
            request.setAttribute("products", products);
            request.setAttribute("productsCount", products.size());
            request.setAttribute("currentCategory", category != null ? category : "");
            request.setAttribute("search", search != null ? search : "");
            request.setAttribute("currentSort", sortBy != null ? sortBy : "newest");
            
            // Forward to JSP
            request.getRequestDispatcher("/buyer/home.jsp").forward(request, response);
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Không thể tải danh sách sản phẩm: " + e.getMessage());
            request.getRequestDispatcher("/buyer/home.jsp").forward(request, response);
        }
    }
}