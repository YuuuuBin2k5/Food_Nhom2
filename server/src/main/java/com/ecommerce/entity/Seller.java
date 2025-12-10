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

    // Quan hệ: 1 Seller có nhiều Product
    @OneToMany(mappedBy = "seller", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Product> products;

    public Seller() {
        super(); // Gọi constructor User() -> đã tự set isBanned = false và createdDate
        this.role = Role.SELLER;
        this.products = new ArrayList<>();
        this.rating = 0.0f;
        this.revenue = 0.0;
    }

    public Seller(String fullName, String email, String password, String phoneNumber, String address, String shopName) {
        super(fullName, email, password, phoneNumber, address, Role.SELLER);
        this.shopName = shopName;
        this.rating = 0.0f;
        this.revenue = 0.0;
        this.products = new ArrayList<>();
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
}