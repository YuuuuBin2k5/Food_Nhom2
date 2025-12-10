/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ecommerce.entity;

import java.io.Serializable;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "admins")
public class Admin extends User implements Serializable {

    public Admin() {
        super();
        this.role = Role.ADMIN;
    }

    public Admin(String fullName, String email, String password, String phoneNumber, String address) {
        // Gọi constructor của lớp cha (User) để tái sử dụng code
        // Truyền thẳng Role.ADMIN vào
        super(fullName, email, password, phoneNumber, address, Role.ADMIN);
    }
}