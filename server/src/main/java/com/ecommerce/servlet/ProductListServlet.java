package com.ecommerce.servlet;

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
import java.io.IOException;
import java.util.List;

@WebServlet(name = "ProductListServlet", urlPatterns = {"/products"})
public class ProductListServlet extends HttpServlet {

    private final ProductService productService = new ProductService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        // Set menu items for buyer
        MenuHelper.setMenuItems(request, "BUYER", "/products");
        
        try {
            // Get filter parameters
            String category = request.getParameter("category");
            String search = request.getParameter("search");
            String sortBy = request.getParameter("sortBy");
            String minPriceStr = request.getParameter("minPrice");
            String maxPriceStr = request.getParameter("maxPrice");
            
            // Fetch products
            List<Product> products;
            if (search != null && !search.trim().isEmpty()) {
                products = productService.searchProducts(search);
            } else if (category != null && !category.trim().isEmpty()) {
                products = productService.getProductsByCategory(category);
            } else {
                products = productService.getActiveProducts();
            }
            
            // Filter by price range
            if (minPriceStr != null && !minPriceStr.trim().isEmpty()) {
                try {
                    double minPrice = Double.parseDouble(minPriceStr);
                    products.removeIf(p -> p.getSalePrice() < minPrice);
                } catch (NumberFormatException e) {
                    // Ignore invalid price
                }
            }
            if (maxPriceStr != null && !maxPriceStr.trim().isEmpty()) {
                try {
                    double maxPrice = Double.parseDouble(maxPriceStr);
                    products.removeIf(p -> p.getSalePrice() > maxPrice);
                } catch (NumberFormatException e) {
                    // Ignore invalid price
                }
            }
            
            // Sort if needed
            if ("price_asc".equals(sortBy)) {
                products.sort((p1, p2) -> Double.compare(p1.getSalePrice(), p2.getSalePrice()));
            } else if ("price_desc".equals(sortBy)) {
                products.sort((p1, p2) -> Double.compare(p2.getSalePrice(), p1.getSalePrice()));
            } else if ("name_asc".equals(sortBy)) {
                products.sort((p1, p2) -> p1.getName().compareToIgnoreCase(p2.getName()));
            } else if ("discount".equals(sortBy)) {
                products.sort((p1, p2) -> {
                    double d1 = (p1.getOriginalPrice() - p1.getSalePrice()) / p1.getOriginalPrice();
                    double d2 = (p2.getOriginalPrice() - p2.getSalePrice()) / p2.getOriginalPrice();
                    return Double.compare(d2, d1);
                });
            }
            
            // Set all categories
            request.setAttribute("categories", ProductCategory.values());
            request.setAttribute("products", products);
            request.setAttribute("currentCategory", category != null ? category : "");
            request.setAttribute("search", search != null ? search : "");
            request.setAttribute("currentSort", sortBy != null ? sortBy : "newest");
            
            request.getRequestDispatcher("/buyer/productsBuyer.jsp").forward(request, response);
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Không thể tải danh sách sản phẩm: " + e.getMessage());
            request.getRequestDispatcher("/buyer/productsBuyer.jsp").forward(request, response);
        }
    }
}
