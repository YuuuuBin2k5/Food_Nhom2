package com.ecommerce.service;

import com.ecommerce.dto.CartItemDTO;
import com.ecommerce.entity.Product;
import jakarta.servlet.http.HttpSession;

import java.util.ArrayList;
import java.util.List;

public class CartService {
    
    private final ProductService productService = new ProductService();
    
    /**
     * Get cart from session
     */
    @SuppressWarnings("unchecked")
    public List<CartItemDTO> getCart(HttpSession session) {
        List<CartItemDTO> cart = (List<CartItemDTO>) session.getAttribute("cart");
        return cart != null ? cart : new ArrayList<>();
    }
    
    /**
     * Add product to cart
     */
    public void addToCart(HttpSession session, Long productId, int quantity) throws Exception {
        if (quantity < 1) {
            throw new IllegalArgumentException("Số lượng phải lớn hơn 0");
        }
        
        Product product = productService.getActiveProductById(productId);
        if (product == null) {
            throw new Exception("Sản phẩm không tồn tại");
        }
        
        if (product.getQuantity() < quantity) {
            throw new Exception("Không đủ hàng trong kho");
        }
        
        List<CartItemDTO> cart = getCart(session);
        
        // Check if product already in cart
        boolean found = false;
        for (CartItemDTO item : cart) {
            if (item.getProduct().getProductId().equals(productId)) {
                int newQuantity = item.getQuantity() + quantity;
                if (product.getQuantity() < newQuantity) {
                    throw new Exception("Không đủ hàng trong kho");
                }
                item.setQuantity(newQuantity);
                found = true;
                break;
            }
        }
        
        if (!found) {
            CartItemDTO newItem = new CartItemDTO(product, quantity);
            cart.add(newItem);
        }
        
        session.setAttribute("cart", cart);
    }
    
    /**
     * Update cart item quantity
     */
    public void updateCartItem(HttpSession session, Long productId, int quantity) throws Exception {
        if (quantity < 1) {
            throw new IllegalArgumentException("Số lượng phải lớn hơn 0");
        }
        
        List<CartItemDTO> cart = getCart(session);
        
        if (cart.isEmpty()) {
            throw new Exception("Giỏ hàng trống");
        }
        
        boolean found = false;
        for (CartItemDTO item : cart) {
            if (item.getProduct().getProductId().equals(productId)) {
                // Check stock
                if (item.getProduct().getQuantity() < quantity) {
                    throw new Exception("Không đủ hàng trong kho");
                }
                item.setQuantity(quantity);
                found = true;
                break;
            }
        }
        
        if (!found) {
            throw new Exception("Sản phẩm không có trong giỏ hàng");
        }
        
        session.setAttribute("cart", cart);
    }
    
    /**
     * Remove item from cart
     */
    public void removeFromCart(HttpSession session, Long productId) throws Exception {
        List<CartItemDTO> cart = getCart(session);
        
        if (cart.isEmpty()) {
            throw new Exception("Giỏ hàng trống");
        }
        
        boolean removed = cart.removeIf(item -> item.getProduct().getProductId().equals(productId));
        
        if (!removed) {
            throw new Exception("Sản phẩm không có trong giỏ hàng");
        }
        
        session.setAttribute("cart", cart);
    }
    
    /**
     * Clear entire cart
     */
    public void clearCart(HttpSession session) {
        session.removeAttribute("cart");
    }
    
    /**
     * Get total items count in cart
     */
    public int getCartCount(HttpSession session) {
        List<CartItemDTO> cart = getCart(session);
        return cart.stream().mapToInt(CartItemDTO::getQuantity).sum();
    }
    
    /**
     * Calculate cart total
     */
    public double getCartTotal(HttpSession session) {
        List<CartItemDTO> cart = getCart(session);
        return cart.stream()
                .mapToDouble(item -> item.getProduct().getSalePrice() * item.getQuantity())
                .sum();
    }
}
