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
@Table(name = "buyers")
public class Buyer extends User implements Serializable {

    @ElementCollection
    @CollectionTable(name = "buyer_addresses", joinColumns = @JoinColumn(name = "buyer_id"))
    @Column(name = "address")
    private List<String> savedAddresses;

    // Quan hệ 1-n với Order (Lịch sử mua hàng)
    @OneToMany(mappedBy = "buyer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Order> orders;

    public Buyer() {
        super();
        this.role = Role.BUYER;
        
        this.savedAddresses = new ArrayList<>();
        this.orders = new ArrayList<>();
    }

    public Buyer(String fullName, String email, String password, String phoneNumber, String address) {
        super(fullName, email, password, phoneNumber, address, Role.BUYER);
        this.savedAddresses = new ArrayList<>();
        this.orders = new ArrayList<>();

        if (address != null && !address.isEmpty()) {
            this.savedAddresses.add(address);
        }
    }

    public List<String> getSavedAddresses() {
        return savedAddresses;
    }

    public void setSavedAddresses(List<String> savedAddresses) {
        this.savedAddresses = savedAddresses;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }
}