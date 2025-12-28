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
import java.util.stream.Collectors;

/**
 * Handles product listing with filtering, searching, and sorting
 * Serves both /products page and homepage product display
 */
@WebServlet(name = "ProductListServlet", urlPatterns = {"/products"})
public class ProductListServlet extends HttpServlet {

    private final ProductService productService = new ProductService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Authentication check
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        User user = (User) session.getAttribute("user");
        String role = (String) session.getAttribute("role");
        
        // Set menu items
        MenuHelper.setMenuItems(request, role != null ? role : "BUYER", "/products");
        
        try {
            // Parse filter parameters
            FilterParams filter = parseFilterParams(request);
            
            // Fetch products based on filters
            List<Product> products = fetchProducts(filter);
            
            // Apply price filter (if specified)
            products = applyPriceFilter(products, filter);
            
            // Apply sorting
            products = applySorting(products, filter.sortBy);
            
            // Set attributes for JSP
            request.setAttribute("categories", ProductCategory.values());
            request.setAttribute("products", products);
            request.setAttribute("productsCount", products.size());
            request.setAttribute("currentCategory", filter.category != null ? filter.category : "");
            request.setAttribute("search", filter.search != null ? filter.search : "");
            request.setAttribute("currentSort", filter.sortBy != null ? filter.sortBy : "newest");
            request.setAttribute("minPrice", filter.minPrice != null ? filter.minPrice : "");
            request.setAttribute("maxPrice", filter.maxPrice != null ? filter.maxPrice : "");
            
            // Forward to JSP
            request.getRequestDispatcher("/buyer/products.jsp").forward(request, response);
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Không thể tải danh sách sản phẩm: " + e.getMessage());
            request.setAttribute("categories", ProductCategory.values());
            request.setAttribute("products", List.of());
            request.getRequestDispatcher("/buyer/products.jsp").forward(request, response);
        }
    }
    
    /**
     * Parse filter parameters from request
     */
    private FilterParams parseFilterParams(HttpServletRequest request) {
        FilterParams params = new FilterParams();
        params.category = request.getParameter("category");
        params.search = request.getParameter("search");
        params.sortBy = request.getParameter("sortBy");
        params.minPrice = request.getParameter("minPrice");
        params.maxPrice = request.getParameter("maxPrice");
        return params;
    }
    
    /**
     * Fetch products from database based on search/category filter
     */
    private List<Product> fetchProducts(FilterParams filter) {
        // Priority: search > category > all
        if (filter.search != null && !filter.search.trim().isEmpty()) {
            return productService.searchProducts(filter.search);
        } else if (filter.category != null && !filter.category.trim().isEmpty()) {
            return productService.getProductsByCategory(filter.category);
        } else {
            return productService.getActiveProducts();
        }
    }
    
    /**
     * Apply price range filtering
     */
    private List<Product> applyPriceFilter(List<Product> products, FilterParams filter) {
        return products.stream()
            .filter(p -> {
                try {
                    // Min price filter
                    if (filter.minPrice != null && !filter.minPrice.trim().isEmpty()) {
                        double minPrice = Double.parseDouble(filter.minPrice);
                        if (p.getSalePrice() < minPrice) return false;
                    }
                    
                    // Max price filter
                    if (filter.maxPrice != null && !filter.maxPrice.trim().isEmpty()) {
                        double maxPrice = Double.parseDouble(filter.maxPrice);
                        if (p.getSalePrice() > maxPrice) return false;
                    }
                    
                    return true;
                } catch (NumberFormatException e) {
                    // Invalid price format, keep the product
                    return true;
                }
            })
            .collect(Collectors.toList());
    }
    
    /**
     * Apply sorting to product list
     */
    private List<Product> applySorting(List<Product> products, String sortBy) {
        if (sortBy == null || sortBy.isEmpty()) {
            return products; // Default order from DB
        }
        
        switch (sortBy) {
            case "price_asc":
                products.sort((p1, p2) -> Double.compare(p1.getSalePrice(), p2.getSalePrice()));
                break;
                
            case "price_desc":
                products.sort((p1, p2) -> Double.compare(p2.getSalePrice(), p1.getSalePrice()));
                break;
                
            case "name_asc":
                products.sort((p1, p2) -> p1.getName().compareToIgnoreCase(p2.getName()));
                break;
                
            case "name_desc":
                products.sort((p1, p2) -> p2.getName().compareToIgnoreCase(p1.getName()));
                break;
                
            case "discount":
                products.sort((p1, p2) -> {
                    double d1 = calculateDiscount(p1);
                    double d2 = calculateDiscount(p2);
                    return Double.compare(d2, d1); // Highest discount first
                });
                break;
                
            default:
                // Unknown sort option, keep original order
                break;
        }
        
        return products;
    }
    
    /**
     * Calculate discount percentage for a product
     */
    private double calculateDiscount(Product product) {
        if (product.getOriginalPrice() <= 0) return 0;
        return ((product.getOriginalPrice() - product.getSalePrice()) / product.getOriginalPrice()) * 100;
    }
    
    /**
     * Inner class to hold filter parameters
     */
    private static class FilterParams {
        String category;
        String search;
        String sortBy;
        String minPrice;
        String maxPrice;
    }
}
