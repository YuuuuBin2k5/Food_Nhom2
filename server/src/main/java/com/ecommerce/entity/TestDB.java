package com.ecommerce.entity;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class TestDB {
    public static void main(String[] args) {
        System.out.println("--- BẮT ĐẦU ---");
        try {
            EntityManagerFactory emf = Persistence.createEntityManagerFactory("FoodRescuePU");
            EntityManager em = emf.createEntityManager();
            
            // Mở một giao dịch để kích hoạt JPA
            em.getTransaction().begin();
           
            
            em.getTransaction().commit();
            
            System.out.println("--- ĐÃ CHẠY XONG ---");
            System.out.println("Kiểm tra console xem có dòng [EL Fine]: sql: CREATE TABLE... không?");
            
            em.close();
            emf.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}