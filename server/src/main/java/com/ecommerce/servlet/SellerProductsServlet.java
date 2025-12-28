package com.ecommerce.servlet;

import java.io.IOException;
import java.util.List;

import com.ecommerce.entity.Product;
import com.ecommerce.entity.User;
import com.ecommerce.service.ProductService;
import com.ecommerce.util.MenuHelper;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/seller/products")
public class SellerProductsServlet extends HttpServlet {
    
    private ProductService productService = new ProductService();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        String role = (String) session.getAttribute("role");
        if (!"SELLER".equals(role)) {
            response.sendRedirect(request.getContextPath() + "/home");
            return;
        }
        
        User user = (User) session.getAttribute("user");
        
        // Set menu items
        MenuHelper.setMenuItems(request, "SELLER", "/seller/products");
        
        try {
            String sellerId = user.getUserId();
            List<Product> products = productService.getProductsBySeller(sellerId);
            
            request.setAttribute("products", products);
            request.setAttribute("categories", com.ecommerce.entity.ProductCategory.values());
            request.getRequestDispatcher("/seller/products.jsp").forward(request, response);
            
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Lỗi tải danh sách sản phẩm");
        }
    }
}
