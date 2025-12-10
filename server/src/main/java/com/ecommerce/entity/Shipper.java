/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ecommerce.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "shippers")
public class Shipper extends User implements Serializable {

    @Column(name = "is_available")
    private boolean isAvailable;

    // 1 Shipper - N Order (Các đơn hàng shipper này nhận giao)
    @OneToMany(mappedBy = "shipper", fetch = FetchType.LAZY)
    private List<Order> assignedOrders;

    public Shipper() {
        super();
        this.role = Role.SHIPPER;
        
        this.isAvailable = true; 
        this.assignedOrders = new ArrayList<>();
    }

    public Shipper(String fullName, String email, String password, String phoneNumber, String address) {
        super(fullName, email, password, phoneNumber, address, Role.SHIPPER);

        this.isAvailable = true;
        this.assignedOrders = new ArrayList<>();
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    public List<Order> getAssignedOrders() {
        return assignedOrders;
    }

    public void setAssignedOrders(List<Order> assignedOrders) {
        this.assignedOrders = assignedOrders;
    }
}