package com.mycompany.service;

import com.mycompany.util.JPAUtil;
import jakarta.persistence.EntityManager;

public class AdminDAO {

    private EntityManager getEntityManager() {
        return JPAUtil.getEntityManager();
    }

    // Đếm tổng số Buyer
    public long countBuyers() {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery("SELECT COUNT(b) FROM Buyer b", Long.class).getSingleResult();
        } catch (Exception e) {
            return 0;
        } finally {
            em.close();
        }
    }

    // Đếm tổng số Seller
    public long countSellers() {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery("SELECT COUNT(s) FROM Seller s", Long.class).getSingleResult();
        } catch (Exception e) {
            return 0;
        } finally {
            em.close();
        }
    }

    // Đếm tổng số Shipper
    public long countShippers() {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery("SELECT COUNT(s) FROM Shipper s", Long.class).getSingleResult();
        } catch (Exception e) {
            return 0;
        } finally {
            em.close();
        }
    }

    // Đếm tổng số Order
    public long countOrders() {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery("SELECT COUNT(o) FROM Order o", Long.class).getSingleResult();
        } catch (Exception e) {
            return 0;
        } finally {
            em.close();
        }
    }

    // Đếm tổng số Product
    public long countProducts() {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery("SELECT COUNT(p) FROM Product p", Long.class).getSingleResult();
        } catch (Exception e) {
            return 0;
        } finally {
            em.close();
        }
    }
}
