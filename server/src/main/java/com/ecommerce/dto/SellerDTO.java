package com.ecommerce.dto;

public class SellerDTO {
    private Long sellerId;
    private String shopName;
    private String email;
    private String phone;
    private String address;
    private float rating;
    
    // Getters and setters
    public Long getSellerId() { return sellerId; }
    public void setSellerId(Long sellerId) { this.sellerId = sellerId; }
    
    public String getShopName() { return shopName; }
    public void setShopName(String shopName) { this.shopName = shopName; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    
    public float getRating() { return rating; }
    public void setRating(float rating) { this.rating = rating; }
}
