/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ecommerce.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.UUID;


@MappedSuperclass
public abstract class User implements Serializable {

    @Id
    @Column(name = "user_id")
    protected String userId; 

    @Column(name = "full_name", nullable = false)
    protected String fullName;

    @Column(nullable = false, unique = true)
    protected String email;

    @Column(nullable = false)
    protected String password;

    @Column(name = "phone_number")
    protected String phoneNumber;

    @Column(name = "address", length = 200)
    protected String address;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    protected Role role;
    
    @Column(name = "is_banned")
    protected boolean isBanned;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_date")
    protected Date createdDate;

    public User() {
        this.userId = UUID.randomUUID().toString();
        this.fullName = null;
        this.email = null;
        this.password = null;
        this.phoneNumber = null;
        this.address = null;
        this.role = null;
        this.isBanned = false;
        this.createdDate = new Date();
    }
    public User(String fullName, String email, String password, String phoneNumber, String address, Role role) {
        this.userId = UUID.randomUUID().toString();
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.role = role;
        
        this.isBanned = false; // Mặc định
        this.createdDate = new Date();
    }

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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public boolean isBanned() {
        return isBanned;
    }

    public void setBanned(boolean banned) {
        isBanned = banned;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }
}