package com.mycompany.dao;

import com.mycompany.model.Buyer;
import com.mycompany.model.Seller;
import com.mycompany.model.Shipper;
import com.mycompany.util.JPAUtil;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    private EntityManager getEntityManager() {
        return JPAUtil.getEntityManager();
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
}
