package com.ecommerce.service;

import com.ecommerce.util.DBUtil;
import jakarta.persistence.EntityManager;

public class AdminService {

    private EntityManager getEntityManager() {
        return DBUtil.getEmFactory().createEntityManager();
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
