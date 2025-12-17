package com.ecommerce.dto;

import com.ecommerce.entity.Product;
import com.ecommerce.entity.ProductStatus;
import java.util.Date;

/**
 * DTO for Product to avoid LazyInitializationException when serializing to JSON
 */
public class ProductDTO {
    private Long productId;
    private String name;
    private String description;
    private double originalPrice;
    private double salePrice;
    private Date expirationDate;
    private Date manufactureDate;
    private int quantity;
    private String imageUrl;
    private Date createdDate;
    private ProductStatus status;
    private boolean isVerified;
    
    // Seller info
    private String sellerId;
    private String sellerName;
    private String shopName;

    public ProductDTO() {
    }

    // Constructor from Product entity
    public ProductDTO(Product product) {
        this.productId = product.getProductId();
        this.name = product.getName();
        this.description = product.getDescription();
        this.originalPrice = product.getOriginalPrice();
        this.salePrice = product.getSalePrice();
        this.expirationDate = product.getExpirationDate();
        this.manufactureDate = product.getManufactureDate();
        this.quantity = product.getQuantity();
        this.imageUrl = product.getImageUrl();
        this.createdDate = product.getCreatedDate();
        this.status = product.getStatus();
        this.isVerified = product.isVerified();
        
        // Get seller info without triggering lazy load issues
        if (product.getSeller() != null) {
            this.sellerId = product.getSeller().getUserId();
            this.sellerName = product.getSeller().getFullName();
            this.shopName = product.getSeller().getShopName();
        }
    }

    // Getters and Setters
    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(double originalPrice) {
        this.originalPrice = originalPrice;
    }

    public double getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(double salePrice) {
        this.salePrice = salePrice;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    public Date getManufactureDate() {
        return manufactureDate;
    }

    public void setManufactureDate(Date manufactureDate) {
        this.manufactureDate = manufactureDate;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public ProductStatus getStatus() {
        return status;
    }

    public void setStatus(ProductStatus status) {
        this.status = status;
    }

    public boolean isVerified() {
        return isVerified;
    }

    public void setVerified(boolean verified) {
        isVerified = verified;
    }

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }
}
