/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ecommerce.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "products")
public class Product implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // <--- THÊM DÒNG NÀY: Để DB tự quản lý việc tăng ID
    @Column(name = "product_id")
    private Long productId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", length = 1000)
    private String description;

    @Column(name = "original_price")
    private double originalPrice;

    @Column(name = "sale_price")
    private double salePrice;

    @Temporal(TemporalType.DATE)
    @Column(name = "expiration_date")
    private Date expirationDate;

    @Temporal(TemporalType.DATE)
    @Column(name = "manufacture_date")
    private Date manufactureDate;

    @Column(name = "quantity")
    private int quantity;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ProductStatus status;

    @Column(name = "is_verified")
    private boolean isVerified;

    // --- QUAN HỆ (RELATIONSHIP) ---
    // Nhiều sản phẩm thuộc về 1 Seller
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false) // Khóa ngoại liên kết với bảng Sellers
    private Seller seller;

    // --- CONSTRUCTORS ---

    public Product() {
        this.name = null;
        this.description = null;
        this.expirationDate = null;
        this.manufactureDate = null;
        this.seller = null;
        this.status = null; 
        
        // Kiểu nguyên thủy (primitive) không thể null
        this.originalPrice = 0.0;
        this.salePrice = 0.0;
        this.quantity = 0;
        this.isVerified = false;
    }

    public Product(String name, String description, double originalPrice, double salePrice, 
                   Date expirationDate, Date manufactureDate, int quantity, Seller seller) {
        this.name = name;
        this.description = description;
        this.originalPrice = originalPrice;
        this.salePrice = salePrice;
        this.expirationDate = expirationDate;
        this.manufactureDate = manufactureDate;
        this.quantity = quantity;
        this.seller = seller;
        
        this.status = ProductStatus.INACTIVE;
        this.isVerified = false;
    }
    
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

    public Seller getSeller() {
        return seller;
    }

    public void setSeller(Seller seller) {
        this.seller = seller;
    }

}