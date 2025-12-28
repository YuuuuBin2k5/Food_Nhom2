package com.ecommerce.service;

import com.ecommerce.entity.Buyer;
import com.ecommerce.entity.Seller;
import com.ecommerce.entity.Shipper;
import com.ecommerce.util.DBUtil;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

public class AdminUserService {

    private EntityManager getEntityManager() {
        return DBUtil.getEmFactory().createEntityManager();
    }

    /**
     * Tìm kiếm Seller theo tên hoặc email
     */
    public List<Seller> searchSellers(String keyword) {
        EntityManager em = getEntityManager();
        try {
            String jpql = "SELECT s FROM Seller s WHERE " +
                          "LOWER(s.fullName) LIKE :keyword OR " +
                          "LOWER(s.email) LIKE :keyword OR " +
                          "LOWER(s.shopName) LIKE :keyword";
            TypedQuery<Seller> query = em.createQuery(jpql, Seller.class);
            query.setParameter("keyword", "%" + keyword.toLowerCase() + "%");
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        } finally {
            em.close();
        }
    }

    /**
     * Tìm kiếm Buyer theo tên hoặc email
     */
    public List<Buyer> searchBuyers(String keyword) {
        EntityManager em = getEntityManager();
        try {
            String jpql = "SELECT b FROM Buyer b WHERE " +
                          "LOWER(b.fullName) LIKE :keyword OR " +
                          "LOWER(b.email) LIKE :keyword";
            TypedQuery<Buyer> query = em.createQuery(jpql, Buyer.class);
            query.setParameter("keyword", "%" + keyword.toLowerCase() + "%");
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        } finally {
            em.close();
        }
    }

    /**
     * Lấy danh sách tất cả Seller
     */
    public List<Seller> getAllSellers() {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery("SELECT s FROM Seller s ORDER BY s.fullName", Seller.class)
                     .getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        } finally {
            em.close();
        }
    }

    /**
     * Lấy danh sách tất cả Buyer
     */
    public List<Buyer> getAllBuyers() {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery("SELECT b FROM Buyer b ORDER BY b.fullName", Buyer.class)
                     .getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        } finally {
            em.close();
        }
    }

    /**
     * Lấy danh sách user đã bị ban
     */
    public List<Seller> getBannedSellers() {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery("SELECT s FROM Seller s WHERE s.isBanned = true", Seller.class)
                     .getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        } finally {
            em.close();
        }
    }

    public List<Buyer> getBannedBuyers() {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery("SELECT b FROM Buyer b WHERE b.isBanned = true", Buyer.class)
                     .getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        } finally {
            em.close();
        }
    }

    /**
     * Ban Seller
     */
    public boolean banSeller(String sellerId) {
        return updateSellerBanStatus(sellerId, true);
    }

    /**
     * Unban Seller
     */
    public boolean unbanSeller(String sellerId) {
        return updateSellerBanStatus(sellerId, false);
    }

    /**
     * Ban Buyer
     */
    public boolean banBuyer(String visitorId) {
        return updateBuyerBanStatus(visitorId, true);
    }

    /**
     * Unban Buyer
     */
    public boolean unbanBuyer(String visitorId) {
        return updateBuyerBanStatus(visitorId, false);
    }

    private boolean updateSellerBanStatus(String sellerId, boolean banned) {
        EntityManager em = getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Seller seller = em.find(Seller.class, sellerId);
            if (seller != null) {
                seller.setBanned(banned);
                em.merge(seller);
                tx.commit();
                return true;
            }
            tx.rollback();
            return false;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
            return false;
        } finally {
            em.close();
        }
    }

    private boolean updateBuyerBanStatus(String visitorId, boolean banned) {
        EntityManager em = getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Buyer buyer = em.find(Buyer.class, visitorId);
            if (buyer != null) {
                buyer.setBanned(banned);
                em.merge(buyer);
                tx.commit();
                return true;
            }
            tx.rollback();
            return false;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
            return false;
        } finally {
            em.close();
        }
    }

    // ========== SHIPPER ==========

    public List<Shipper> searchShippers(String keyword) {
        EntityManager em = getEntityManager();
        try {
            String jpql = "SELECT s FROM Shipper s WHERE " +
                          "LOWER(s.fullName) LIKE :keyword OR " +
                          "LOWER(s.email) LIKE :keyword";
            TypedQuery<Shipper> query = em.createQuery(jpql, Shipper.class);
            query.setParameter("keyword", "%" + keyword.toLowerCase() + "%");
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        } finally {
            em.close();
        }
    }

    public List<Shipper> getAllShippers() {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery("SELECT s FROM Shipper s ORDER BY s.fullName", Shipper.class)
                     .getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        } finally {
            em.close();
        }
    }

    public List<Shipper> getBannedShippers() {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery("SELECT s FROM Shipper s WHERE s.isBanned = true", Shipper.class)
                     .getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        } finally {
            em.close();
        }
    }

    public boolean banShipper(String shipperId) {
        return updateShipperBanStatus(shipperId, true);
    }

    public boolean unbanShipper(String shipperId) {
        return updateShipperBanStatus(shipperId, false);
    }

    private boolean updateShipperBanStatus(String shipperId, boolean banned) {
        EntityManager em = getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Shipper shipper = em.find(Shipper.class, shipperId);
            if (shipper != null) {
                shipper.setBanned(banned);
                em.merge(shipper);
                tx.commit();
                return true;
            }
            tx.rollback();
            return false;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
            return false;
        } finally {
            em.close();
        }
    }

    // ==================== PHÂN TRANG ====================

    /**
     * Đếm tổng số user theo loại và filter
     */
    public long countUsers(String filter) {
        EntityManager em = getEntityManager();
        try {
            long count = 0;
            if ("sellers".equals(filter)) {
                count = em.createQuery("SELECT COUNT(s) FROM Seller s", Long.class).getSingleResult();
            } else if ("buyers".equals(filter)) {
                count = em.createQuery("SELECT COUNT(b) FROM Buyer b", Long.class).getSingleResult();
            } else if ("shippers".equals(filter)) {
                count = em.createQuery("SELECT COUNT(s) FROM Shipper s", Long.class).getSingleResult();
            } else if ("banned".equals(filter)) {
                long sellers = em.createQuery("SELECT COUNT(s) FROM Seller s WHERE s.isBanned = true", Long.class).getSingleResult();
                long buyers = em.createQuery("SELECT COUNT(b) FROM Buyer b WHERE b.isBanned = true", Long.class).getSingleResult();
                long shippers = em.createQuery("SELECT COUNT(s) FROM Shipper s WHERE s.isBanned = true", Long.class).getSingleResult();
                count = sellers + buyers + shippers;
            } else { // all
                long sellers = em.createQuery("SELECT COUNT(s) FROM Seller s", Long.class).getSingleResult();
                long buyers = em.createQuery("SELECT COUNT(b) FROM Buyer b", Long.class).getSingleResult();
                long shippers = em.createQuery("SELECT COUNT(s) FROM Shipper s", Long.class).getSingleResult();
                count = sellers + buyers + shippers;
            }
            return count;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        } finally {
            em.close();
        }
    }

    /**
     * Lấy danh sách Seller có phân trang
     */
    public List<Seller> getAllSellers(int page, int pageSize) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Seller> query = em.createQuery(
                "SELECT s FROM Seller s ORDER BY s.fullName", Seller.class);
            query.setFirstResult((page - 1) * pageSize);
            query.setMaxResults(pageSize);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        } finally {
            em.close();
        }
    }

    /**
     * Lấy danh sách Buyer có phân trang
     */
    public List<Buyer> getAllBuyers(int page, int pageSize) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Buyer> query = em.createQuery(
                "SELECT b FROM Buyer b ORDER BY b.fullName", Buyer.class);
            query.setFirstResult((page - 1) * pageSize);
            query.setMaxResults(pageSize);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        } finally {
            em.close();
        }
    }

    /**
     * Lấy danh sách Shipper có phân trang
     */
    public List<Shipper> getAllShippers(int page, int pageSize) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Shipper> query = em.createQuery(
                "SELECT s FROM Shipper s ORDER BY s.fullName", Shipper.class);
            query.setFirstResult((page - 1) * pageSize);
            query.setMaxResults(pageSize);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        } finally {
            em.close();
        }
    }

    /**
     * Lấy danh sách Seller đã bị ban có phân trang
     */
    public List<Seller> getBannedSellers(int page, int pageSize) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Seller> query = em.createQuery(
                "SELECT s FROM Seller s WHERE s.isBanned = true ORDER BY s.fullName", Seller.class);
            query.setFirstResult((page - 1) * pageSize);
            query.setMaxResults(pageSize);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        } finally {
            em.close();
        }
    }

    /**
     * Lấy danh sách Buyer đã bị ban có phân trang
     */
    public List<Buyer> getBannedBuyers(int page, int pageSize) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Buyer> query = em.createQuery(
                "SELECT b FROM Buyer b WHERE b.isBanned = true ORDER BY b.fullName", Buyer.class);
            query.setFirstResult((page - 1) * pageSize);
            query.setMaxResults(pageSize);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        } finally {
            em.close();
        }
    }

    /**
     * Lấy danh sách Shipper đã bị ban có phân trang
     */
    public List<Shipper> getBannedShippers(int page, int pageSize) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Shipper> query = em.createQuery(
                "SELECT s FROM Shipper s WHERE s.isBanned = true ORDER BY s.fullName", Shipper.class);
            query.setFirstResult((page - 1) * pageSize);
            query.setMaxResults(pageSize);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        } finally {
            em.close();
        }
    }
}
