package com.ecommerce.dto;

import com.ecommerce.entity.Product;

public class CartItemDTO {
    
    private ProductDTO product;
    private int quantity;
    
    public CartItemDTO() {
    }
    
    public CartItemDTO(ProductDTO product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }
    
    // Constructor with Product entity (converts to DTO)
    public CartItemDTO(Product product, int quantity) {
        this.product = new ProductDTO(product);
        this.quantity = quantity;
    }
    
    public ProductDTO getProduct() {
        return product;
    }
    
    public void setProduct(ProductDTO product) {
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
