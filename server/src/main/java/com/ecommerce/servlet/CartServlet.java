package com.ecommerce.servlet;

import com.ecommerce.dto.CartItemDTO;
import com.ecommerce.dto.ProductDTO;
import com.ecommerce.service.ProductService;
import com.ecommerce.util.MenuHelper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

@WebServlet(name = "CartServlet", urlPatterns = {"/cart"})
public class CartServlet extends HttpServlet {

    private final ProductService productService = new ProductService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Set menu items for buyer
        MenuHelper.setMenuItems(request, "BUYER", "/cart");
        
        // Forward to cart.jsp
        request.getRequestDispatcher("/buyer/cart.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null) action = "";
        
        HttpSession session = request.getSession();
        List<CartItemDTO> cart = (List<CartItemDTO>) session.getAttribute("cart");
        if (cart == null) {
            cart = new ArrayList<>();
            session.setAttribute("cart", cart);
        }

        try {
            switch (action) {
                case "add":
                    addToCart(request, cart);
                    break;
                case "update":
                    updateCart(request, cart);
                    break;
                case "remove":
                    removeFromCart(request, cart);
                    break;
                case "clear":
                    cart.clear();
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Update cart size in session for header
        int totalItems = 0;
        for (CartItemDTO item : cart) {
            totalItems += item.getQuantity();
        }
        session.setAttribute("cartSize", totalItems);

        // Redirect back to cart or referer
        String referer = request.getHeader("Referer");
        if (action.equals("add") && referer != null && referer.contains("product")) {
             // If added from product page, stay there with message
             response.sendRedirect(referer + "&message=Added to cart");
        } else {
             response.sendRedirect(request.getContextPath() + "/cart");
        }
    }

    private void addToCart(HttpServletRequest request, List<CartItemDTO> cart) throws Exception {
        Long productId = Long.parseLong(request.getParameter("productId"));
        int quantity = Integer.parseInt(request.getParameter("quantity"));
        
        // Check if item exists
        boolean found = false;
        for (CartItemDTO item : cart) {
            if (item.getProduct().getProductId().equals(productId)) {
                item.setQuantity(item.getQuantity() + quantity);
                found = true;
                break;
            }
        }
        
        if (!found) {
            ProductDTO product = productService.getProductById(productId);
            if (product != null) {
                cart.add(new CartItemDTO(product, quantity));
            }
        }
    }

    private void updateCart(HttpServletRequest request, List<CartItemDTO> cart) {
        Long productId = Long.parseLong(request.getParameter("productId"));
        int quantity = Integer.parseInt(request.getParameter("quantity"));
        
        if (quantity <= 0) {
            removeFromCart(request, cart);
            return;
        }

        for (CartItemDTO item : cart) {
            if (item.getProduct().getProductId().equals(productId)) {
                item.setQuantity(quantity);
                break;
            }
        }
    }

    private void removeFromCart(HttpServletRequest request, List<CartItemDTO> cart) {
        Long productId = Long.parseLong(request.getParameter("productId"));
        
        Iterator<CartItemDTO> iterator = cart.iterator();
        while (iterator.hasNext()) {
            CartItemDTO item = iterator.next();
            if (item.getProduct().getProductId().equals(productId)) {
                iterator.remove();
                break;
            }
        }
    }
}
