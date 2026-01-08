package com.ecommerce.service;

import com.ecommerce.util.DBUtil;
import jakarta.persistence.EntityManager;
import java.util.HashMap;
import java.util.Map;

public class AdminService {

    private EntityManager getEntityManager() {
        return DBUtil.getEmFactory().createEntityManager();
    }

    // Tối ưu: Lấy tất cả statistics trong 1 lần
    public Map<String, Object> getAllStatistics() {
        EntityManager em = null;
        Map<String, Object> stats = new HashMap<>();
        
        try {
            em = getEntityManager();
            System.out.println("AdminService: Loading statistics...");
            
            stats.put("buyers", em.createQuery("SELECT COUNT(b) FROM Buyer b", Long.class).getSingleResult());
            stats.put("sellers", em.createQuery("SELECT COUNT(s) FROM Seller s", Long.class).getSingleResult());
            stats.put("shippers", em.createQuery("SELECT COUNT(s) FROM Shipper s", Long.class).getSingleResult());
            stats.put("orders", em.createQuery("SELECT COUNT(o) FROM Order o", Long.class).getSingleResult());
            stats.put("products", em.createQuery("SELECT COUNT(p) FROM Product p", Long.class).getSingleResult());
            
            // Tính tổng doanh thu từ tất cả payments
            Double totalRevenue = em.createQuery(
                "SELECT COALESCE(SUM(p.amount), 0.0) FROM Payment p", 
                Double.class).getSingleResult();
            stats.put("revenue", totalRevenue);
            
            System.out.println("AdminService: Total revenue calculated: " + totalRevenue);
            
            System.out.println("AdminService: Statistics loaded successfully");
        } catch (Exception e) {
            System.err.println("AdminService ERROR: " + e.getMessage());
            e.printStackTrace();
            // Return zeros if error
            stats.put("buyers", 0L);
            stats.put("sellers", 0L);
            stats.put("shippers", 0L);
            stats.put("orders", 0L);
            stats.put("products", 0L);
            stats.put("revenue", 0.0);
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
        
        return stats;
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
