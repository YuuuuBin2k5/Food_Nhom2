package com.ecommerce.dto;

import com.ecommerce.entity.Product;

public class CartItemDTO {
    
    private Product product;
    private int quantity;
    
    public CartItemDTO() {
    }
    
    public CartItemDTO(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }
    
    public Product getProduct() {
        return product;
    }
    
    public void setProduct(Product product) {
        this.product = product;
    }
    
    public int getQuantity() {
        return quantity;
    }
    
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    
    // Helper methods
    public double getSubtotal() {
        return product != null ? product.getSalePrice() * quantity : 0;
    }
    
    public double getSavings() {
        if (product == null) return 0;
        return (product.getOriginalPrice() - product.getSalePrice()) * quantity;
    }
}
