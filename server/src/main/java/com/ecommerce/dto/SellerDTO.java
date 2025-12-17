package com.ecommerce.dto;

import com.ecommerce.entity.Seller;
import com.ecommerce.entity.SellerStatus;
import java.util.Date;

/**
 * DTO for Seller to avoid LazyInitializationException when serializing to JSON
 */
public class SellerDTO {
    private String userId;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String address;
    private String shopName;
    private float rating;
    private double revenue;
    private boolean banned;
    private String businessLicenseUrl;
    private Date licenseSubmittedDate;
    private SellerStatus verificationStatus;

    public SellerDTO() {
    }

    // Constructor from Seller entity
    public SellerDTO(Seller seller) {
        this.userId = seller.getUserId();
        this.fullName = seller.getFullName();
        this.email = seller.getEmail();
        this.phoneNumber = seller.getPhoneNumber();
        this.address = seller.getAddress();
        this.shopName = seller.getShopName();
        this.rating = seller.getRating();
        this.revenue = seller.getRevenue();
        this.banned = seller.isBanned();
        this.businessLicenseUrl = seller.getBusinessLicenseUrl();
        this.licenseSubmittedDate = seller.getLicenseSubmittedDate();
        this.verificationStatus = seller.getVerificationStatus();
    }

    // Getters and Setters
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public boolean isBanned() {
        return banned;
    }

    public void setBanned(boolean banned) {
        this.banned = banned;
    }

    public String getBusinessLicenseUrl() {
        return businessLicenseUrl;
    }

    public void setBusinessLicenseUrl(String businessLicenseUrl) {
        this.businessLicenseUrl = businessLicenseUrl;
    }

    public Date getLicenseSubmittedDate() {
        return licenseSubmittedDate;
    }

    public void setLicenseSubmittedDate(Date licenseSubmittedDate) {
        this.licenseSubmittedDate = licenseSubmittedDate;
    }

    public SellerStatus getVerificationStatus() {
        return verificationStatus;
    }

    public void setVerificationStatus(SellerStatus verificationStatus) {
        this.verificationStatus = verificationStatus;
    }
}
