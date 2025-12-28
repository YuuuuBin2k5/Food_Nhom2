/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ecommerce.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "sellers")
public class Seller extends User implements Serializable {

    @Column(name = "shop_name", nullable = false)
    private String shopName;

    @Column(name = "rating")
    private float rating;

    @Column(name = "revenue")
    private double revenue;

    @Column(name = "business_license_url")
    private String businessLicenseUrl;

    // Ngày nộp giấy phép kinh doanh (để xác định thứ tự duyệt FIFO)
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "license_submitted_date")
    private java.util.Date licenseSubmittedDate;

    // Ngày duyệt giấy phép
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "license_approved_date")
    private java.util.Date licenseApprovedDate;

    // Trạng thái duyệt
    @Enumerated(EnumType.STRING)
    @Column(name = "verification_status", nullable = false)
    private SellerStatus verificationStatus;
    
    // Quan hệ: 1 Seller có nhiều Product
    @OneToMany(mappedBy = "seller", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Product> products;

    public Seller() {
        super(); // Gọi constructor User() -> đã tự set isBanned = false và createdDate
        this.role = Role.SELLER;
        this.products = new ArrayList<>();
        this.rating = 0.0f;
        this.revenue = 0.0;
        this.verificationStatus = SellerStatus.UNVERIFIED;
    }

    @SuppressWarnings("static-access")
    public Seller(String fullName, String email, String password, String phoneNumber, String address, String shopName) {
        super(fullName, email, password, phoneNumber, address, Role.SELLER);
        this.shopName = shopName;
        this.rating = 0.0f;
        this.revenue = 0.0;
        this.products = new ArrayList<>();
        this.verificationStatus = SellerStatus.UNVERIFIED;
    }
    
    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public double getRevenue() {
        return revenue;
    }

    public void setRevenue(double revenue) {
        this.revenue = revenue;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }
    
    public String getBusinessLicenseUrl() {
        return businessLicenseUrl;
    }

    public void setBusinessLicenseUrl(String businessLicenseUrl) {
this.businessLicenseUrl = businessLicenseUrl;
        // Khi seller upload ảnh, tự động set ngày nộp và chuyển status sang PENDING
        this.licenseSubmittedDate = new java.util.Date();
        this.verificationStatus = SellerStatus.PENDING; 
    }

    public java.util.Date getLicenseSubmittedDate() {
        return licenseSubmittedDate;
    }

    public void setLicenseSubmittedDate(java.util.Date licenseSubmittedDate) {
        this.licenseSubmittedDate = licenseSubmittedDate;
    }

    public java.util.Date getLicenseApprovedDate() {
        return licenseApprovedDate;
    }

    public void setLicenseApprovedDate(java.util.Date licenseApprovedDate) {
        this.licenseApprovedDate = licenseApprovedDate;
    }

    public SellerStatus getVerificationStatus() {
        return verificationStatus;
    }

    public void setVerificationStatus(SellerStatus verificationStatus) {
        this.verificationStatus = verificationStatus;
    }
    
}