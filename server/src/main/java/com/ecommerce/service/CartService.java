package com.ecommerce.service;

import com.ecommerce.dto.CartItemDTO;
import com.ecommerce.entity.Product;
import com.ecommerce.util.DBUtil;
import jakarta.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

/**
 * Cart Service - Manages shopping cart operations
 * 
 * Responsibilities:
 * - Add/update/remove items from cart
 * - Validate product existence and stock availability
 * - Calculate cart totals
 * - Validate entire cart
 */
public class CartService {
    
    // ========== ADD OPERATIONS ==========
    
    /**
     * Add item to cart or update quantity if product already exists
     * 
     * @param cartItems List of current cart items
     * @param productId Product ID to add
     * @param quantity Quantity to add
     * @return Updated cart items list
     * @throws IllegalArgumentException if quantity <= 0 or insufficient stock
     */
    public List<CartItemDTO> addToCart(List<CartItemDTO> cartItems, Long productId, int quantity) {
        validateQuantity(quantity, "Số lượng phải lớn hơn 0");
        
        Product product = validateAndGetProduct(productId, quantity);
        
        CartItemDTO existingItem = findCartItem(cartItems, productId);
        
        if (existingItem != null) {
            updateExistingItem(existingItem, quantity, product.getQuantity());
        } else {
            addNewItem(cartItems, productId, quantity);
        }
        
        return cartItems;
    }

    /**
     * Update cart item quantity
     * 
     * @param cartItems List of current cart items
     * @param productId Product ID to update
     * @param newQuantity New quantity (0 to remove)
     * @return Updated cart items list
     * @throws IllegalArgumentException if quantity < 0 or item not found
     */
    public List<CartItemDTO> updateQuantity(List<CartItemDTO> cartItems, Long productId, int newQuantity) {
        validateQuantity(newQuantity, "Số lượng không thể âm");
        
        CartItemDTO item = findCartItemOrThrow(cartItems, productId);
        
        if (newQuantity == 0) {
            cartItems.remove(item);
        } else {
            validateAndGetProduct(productId, newQuantity);
            item.setQuantity(newQuantity);
        }
        
        return cartItems;
    }

    // ========== REMOVE OPERATIONS ==========
    
    /**
     * Remove item from cart
     * 
     * @param cartItems List of current cart items
     * @param productId Product ID to remove
     * @return Updated cart items list
     */
    public List<CartItemDTO> removeFromCart(List<CartItemDTO> cartItems, Long productId) {
        cartItems.removeIf(item -> item.getProductId().equals(productId.toString()));
        return cartItems;
    }

    // ========== CALCULATION OPERATIONS ==========
    
    /**
     * Calculate total price of cart items
     * 
     * @param cartItems List of cart items
     * @return Total price in VND
     */
    public double calculateTotal(List<CartItemDTO> cartItems) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            double total = 0;
            for (CartItemDTO item : cartItems) {
                Product product = em.find(Product.class, Long.parseLong(item.getProductId()));
                if (product != null) {
                    total += product.getSalePrice() * item.getQuantity();
                }
            }
            return total;
        } finally {
            em.close();
        }
    }

    // ========== VALIDATION OPERATIONS ==========
    
    /**
     * Validate entire cart - check all items exist and have sufficient stock
     * 
     * @param cartItems List of cart items
     * @return List of validation errors (empty if valid)
     */
    public List<String> validateCart(List<CartItemDTO> cartItems) {
        List<String> errors = new ArrayList<>();
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        
        try {
            for (CartItemDTO item : cartItems) {
                Long productId = Long.parseLong(item.getProductId());
                Product product = em.find(Product.class, productId);
                
                if (product == null) {
                    errors.add("Sản phẩm không tồn tại");
                } else if (product.getQuantity() < item.getQuantity()) {
                    errors.add("Sản phẩm '" + product.getName() + 
                              "' chỉ còn " + product.getQuantity() + " cái");
                }
            }
        } finally {
            em.close();
        }
        
        return errors;
    }

    /**
     * Get all product details for cart items
     * 
     * @param cartItems List of cart items
     * @return List of [Product, Quantity] pairs
     */
    public List<Object[]> getCartWithProductDetails(List<CartItemDTO> cartItems) {
        List<Object[]> result = new ArrayList<>();
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        
        try {
            for (CartItemDTO item : cartItems) {
                Product product = em.find(Product.class, Long.parseLong(item.getProductId()));
                if (product != null) {
                    result.add(new Object[]{product, item.getQuantity()});
                }
            }
        } finally {
            em.close();
        }
        
        return result;
    }

    // ========== HELPER METHODS ==========
    
    /**
     * Validate product exists and has sufficient stock
     * 
     * @throws IllegalArgumentException if product not found or insufficient stock
     */
    private Product validateAndGetProduct(Long productId, int quantity) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            Product product = em.find(Product.class, productId);
            
            if (product == null) {
                throw new IllegalArgumentException("Sản phẩm không tồn tại");
            }
            
            if (product.getQuantity() < quantity) {
                throw new IllegalArgumentException(
                    "Chỉ còn " + product.getQuantity() + " sản phẩm trong kho"
                );
            }
            
            return product;
        } finally {
            em.close();
        }
    }

    /**
     * Find cart item by product ID
     */
    private CartItemDTO findCartItem(List<CartItemDTO> cartItems, Long productId) {
        return cartItems.stream()
            .filter(item -> item.getProductId().equals(productId.toString()))
            .findFirst()
            .orElse(null);
    }

    /**
     * Find cart item or throw exception if not found
     */
    private CartItemDTO findCartItemOrThrow(List<CartItemDTO> cartItems, Long productId) {
        return cartItems.stream()
            .filter(item -> item.getProductId().equals(productId.toString()))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Sản phẩm không có trong giỏ"));
    }

    /**
     * Update existing cart item quantity
     */
    private void updateExistingItem(CartItemDTO item, int additionalQuantity, int maxStock) {
        int newQuantity = item.getQuantity() + additionalQuantity;
        if (newQuantity > maxStock) {
            throw new IllegalArgumentException(
                "Chỉ còn " + maxStock + " sản phẩm trong kho"
            );
        }
        item.setQuantity(newQuantity);
    }

    /**
     * Add new item to cart
     */
    private void addNewItem(List<CartItemDTO> cartItems, Long productId, int quantity) {
        cartItems.add(new CartItemDTO(productId.toString(), quantity));
    }

    /**
     * Validate quantity value
     */
    private void validateQuantity(int quantity, String errorMessage) {
        if (quantity <= 0) {
            throw new IllegalArgumentException(errorMessage);
        }
    }
}
