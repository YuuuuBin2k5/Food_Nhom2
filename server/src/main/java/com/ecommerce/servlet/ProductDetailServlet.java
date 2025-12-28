package com.ecommerce.servlet;

import com.ecommerce.dto.ProductDTO;
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

@WebServlet(name = "ProductDetailServlet", urlPatterns = {"/product"})
public class ProductDetailServlet extends HttpServlet {

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
        MenuHelper.setMenuItems(request, "BUYER", "/product");
        
        try {
            String productIdStr = request.getParameter("id");
            if (productIdStr == null || productIdStr.trim().isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/products");
                return;
            }
            
            Long productId = Long.parseLong(productIdStr);
            ProductDTO product = productService.getProductById(productId);
            
            if (product == null) {
                request.setAttribute("error", "Không tìm thấy sản phẩm");
                response.sendRedirect(request.getContextPath() + "/products");
                return;
            }
            
            request.setAttribute("product", product);
            request.getRequestDispatcher("/buyer/product-detail.jsp").forward(request, response);
            
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/products");
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Không thể tải thông tin sản phẩm: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/products");
        }
    }
}
