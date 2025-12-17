package com.ecommerce.servlet;

import com.ecommerce.dto.ProductDTO;
import com.ecommerce.dto.ProductFilter;
import com.ecommerce.dto.ProductPageResponse;
import com.ecommerce.service.ProductService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/api/products/*")
public class ProductServlet extends HttpServlet {
    
    private final ProductService productService = new ProductService();
    private final Gson gson = new GsonBuilder()
        .setDateFormat("yyyy-MM-dd")
        .create();
    
    @Override
    protected void doGet(HttpServletRequest request, 
                        HttpServletResponse response) 
            throws ServletException, IOException {
        
        String pathInfo = request.getPathInfo();
        
        if (pathInfo != null && pathInfo.length() > 1) {
            // GET /api/products/{id}
            String productId = pathInfo.substring(1);
            getProductById(productId, response);
        } else {
            // GET /api/products (list with filters)
            getProducts(request, response);
        }
    }
    
    private void getProducts(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        setAccessControlHeaders(response);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        try (PrintWriter out = response.getWriter()) {
            // Get query parameters
            String search = request.getParameter("search");
            String minPriceStr = request.getParameter("minPrice");
            String maxPriceStr = request.getParameter("maxPrice");
            String sortBy = request.getParameter("sortBy");
            String pageStr = request.getParameter("page");
            String sizeStr = request.getParameter("size");
            String sellerId = request.getParameter("sellerId");
            String hasDiscountStr = request.getParameter("hasDiscount");
            String inStockStr = request.getParameter("inStock");
            
            // Parse parameters
            Double minPrice = minPriceStr != null ? Double.parseDouble(minPriceStr) : null;
            Double maxPrice = maxPriceStr != null ? Double.parseDouble(maxPriceStr) : null;
            int page = pageStr != null ? Integer.parseInt(pageStr) : 0;
            int size = sizeStr != null ? Integer.parseInt(sizeStr) : 12;
            Boolean hasDiscount = hasDiscountStr != null ? Boolean.parseBoolean(hasDiscountStr) : null;
            Boolean inStock = inStockStr != null ? Boolean.parseBoolean(inStockStr) : null;
            
            // Create filter object
            ProductFilter filter = new ProductFilter();
            filter.setSearch(search);
            filter.setMinPrice(minPrice);
            filter.setMaxPrice(maxPrice);
            filter.setSortBy(sortBy);
            filter.setPage(page);
            filter.setSize(size);
            filter.setSellerId(sellerId);
            filter.setHasDiscount(hasDiscount);
            filter.setInStock(inStock);
            
            // Get products
            ProductPageResponse pageResponse = productService.getProducts(filter);
            
            // Return response
            response.setStatus(HttpServletResponse.SC_OK);
            out.print(gson.toJson(pageResponse));
            
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            JsonObject error = new JsonObject();
            error.addProperty("success", false);
            error.addProperty("message", e.getMessage());
            response.getWriter().print(gson.toJson(error));
        }
    }
    
private void getProductById(String productId, HttpServletResponse response) 
        throws IOException {
    
    setAccessControlHeaders(response);
    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");
    
    try (PrintWriter out = response.getWriter()) {
        Long id = Long.parseLong(productId);
        ProductDTO product = productService.getProductById(id);
        
        JsonObject jsonResponse = new JsonObject();
        jsonResponse.addProperty("success", true);
        jsonResponse.add("data", gson.toJsonTree(product));
        
        response.setStatus(HttpServletResponse.SC_OK);
        out.print(gson.toJson(jsonResponse));
        
    } catch (NumberFormatException e) {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        // Error response
    } catch (Exception e) {
        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        // Error response
    }
}
    
    private void setAccessControlHeaders(HttpServletResponse resp) {
        resp.setHeader("Access-Control-Allow-Origin", "http://localhost:5173");
        resp.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
        resp.setHeader("Access-Control-Allow-Credentials", "true");
    }
}