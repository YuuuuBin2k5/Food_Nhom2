package com.ecommerce.servlet;

import java.io.IOException;
import java.io.PrintWriter;

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

@WebServlet("/api/products/*")
public class PublicProductServlet extends HttpServlet {

    private final ProductService productService = new ProductService();
    private final Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        setHeaders(resp);
        setAccessControlHeaders(resp);
        
        String pathInfo = req.getPathInfo();
        
        if (pathInfo != null && pathInfo.length() > 1) {
            // GET /api/products/{id}
            String productId = pathInfo.substring(1);
            getProductById(productId, resp);
        } else {
            // GET /api/products (list with filters)
            getProducts(req, resp);
        }
    }
    
    private void getProducts(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
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
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        try (PrintWriter out = response.getWriter()) {
            Long id = Long.parseLong(productId);
            ProductDTO product = productService.getProductById(id);
            
            response.setStatus(HttpServletResponse.SC_OK);
            out.print(gson.toJson(product));
            
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            sendError(response, 400, "Invalid product ID");
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            sendError(response, 404, e.getMessage());
        }
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        setAccessControlHeaders(resp);
        resp.setStatus(HttpServletResponse.SC_OK);
    }

    private void setHeaders(HttpServletResponse resp) {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
    }
    
    private void setAccessControlHeaders(HttpServletResponse resp) {
        String origin = "http://localhost:5173";
        resp.setHeader("Access-Control-Allow-Origin", origin);
        resp.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
        resp.setHeader("Access-Control-Allow-Credentials", "true");
    }

    private void sendError(HttpServletResponse resp, int statusCode, String message) throws IOException {
        resp.setStatus(statusCode);
        JsonObject error = new JsonObject();
        error.addProperty("success", false);
        error.addProperty("message", message);
        resp.getWriter().print(gson.toJson(error));
    }
}
