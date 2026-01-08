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

    // ==================== SEARCH METHODS ====================

    /**
     * Tìm kiếm Seller theo tên, email hoặc shop
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
     * Tìm kiếm Shipper theo tên hoặc email
     */
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

    // ==================== GET ALL METHODS ====================

    /**
     * Lấy tất cả Seller (sắp xếp theo tên)
     */
    public List<Seller> getAllSellers() {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery(
                "SELECT s FROM Seller s " +
                "ORDER BY s.fullName", 
                Seller.class)
                     .getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        } finally {
            em.close();
        }
    }

    /**
     * Lấy tất cả Buyer (sắp xếp theo tên)
     */
    public List<Buyer> getAllBuyers() {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery(
                "SELECT b FROM Buyer b " +
                "ORDER BY b.fullName", 
                Buyer.class)
                     .getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        } finally {
            em.close();
        }
    }

    /**
     * Lấy tất cả Shipper (sắp xếp theo tên)
     */
    public List<Shipper> getAllShippers() {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery(
                "SELECT s FROM Shipper s " +
                "ORDER BY s.fullName", 
                Shipper.class)
                     .getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        } finally {
            em.close();
        }
    }

    // ==================== GET BANNED METHODS ====================

    /**
     * Lấy tất cả Seller đã bị ban
     */
    public List<Seller> getBannedSellers() {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery(
                "SELECT s FROM Seller s " +
                "WHERE s.isBanned = true " +
                "ORDER BY s.fullName", 
                Seller.class)
                     .getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        } finally {
            em.close();
        }
    }

    /**
     * Lấy tất cả Buyer đã bị ban
     */
    public List<Buyer> getBannedBuyers() {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery(
                "SELECT b FROM Buyer b " +
                "WHERE b.isBanned = true " +
                "ORDER BY b.fullName", 
                Buyer.class)
                     .getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        } finally {
            em.close();
        }
    }

    /**
     * Lấy tất cả Shipper đã bị ban
     */
    public List<Shipper> getBannedShippers() {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery(
                "SELECT s FROM Shipper s " +
                "WHERE s.isBanned = true " +
                "ORDER BY s.fullName", 
                Shipper.class)
                     .getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        } finally {
            em.close();
        }
    }

    // ==================== SORT METHODS ====================

    /**
     * Lấy tất cả Seller sắp xếp theo email A-Z
     */
    public List<Seller> getAllSellersSortByEmail() {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery(
                "SELECT s FROM Seller s " +
                "ORDER BY s.email ASC", 
                Seller.class)
                     .getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Lấy tất cả Seller sắp xếp theo shop A-Z
     */
    public List<Seller> getAllSellersSortByShop() {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery(
                "SELECT s FROM Seller s " +
                "ORDER BY s.shopName ASC", 
                Seller.class)
                     .getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Lấy tất cả Buyer sắp xếp theo email A-Z
     */
    public List<Buyer> getAllBuyersSortByEmail() {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery(
                "SELECT b FROM Buyer b " +
                "ORDER BY b.email ASC", 
                Buyer.class)
                     .getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Lấy tất cả Shipper sắp xếp theo email A-Z
     */
    public List<Shipper> getAllShippersSortByEmail() {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery(
                "SELECT s FROM Shipper s " +
                "ORDER BY s.email ASC", 
                Shipper.class)
                     .getResultList();
        } finally {
            em.close();
        }
    }

    // ==================== BAN/UNBAN METHODS ====================

    /**
     * Ban Seller
     * @return 0: thành công, 1: seller không tồn tại, 2: đã bị ban rồi
     */
    public int banSeller(String sellerId) {
        return updateSellerBanStatus(sellerId, true);
    }

    /**
     * Unban Seller
     * @return 0: thành công, 1: seller không tồn tại, 2: chưa bị ban
     */
    public int unbanSeller(String sellerId) {
        return updateSellerBanStatus(sellerId, false);
    }

    /**
     * Ban Buyer
     * @return 0: thành công, 1: buyer không tồn tại, 2: đã bị ban rồi
     */
    public int banBuyer(String buyerId) {
        return updateBuyerBanStatus(buyerId, true);
    }

    /**
     * Unban Buyer
     * @return 0: thành công, 1: buyer không tồn tại, 2: chưa bị ban
     */
    public int unbanBuyer(String buyerId) {
        return updateBuyerBanStatus(buyerId, false);
    }

    /**
     * Ban Shipper
     * @return 0: thành công, 1: shipper không tồn tại, 2: đã bị ban rồi
     */
    public int banShipper(String shipperId) {
        return updateShipperBanStatus(shipperId, true);
    }

    /**
     * Unban Shipper
     * @return 0: thành công, 1: shipper không tồn tại, 2: chưa bị ban
     */
    public int unbanShipper(String shipperId) {
        return updateShipperBanStatus(shipperId, false);
    }

    /**
     * Cập nhật trạng thái ban của Seller với đồng bộ hóa
     * @return 0: thành công, 1: seller không tồn tại, 2: trạng thái đã giống như yêu cầu
     */
    private int updateSellerBanStatus(String sellerId, boolean banned) {
        EntityManager em = getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            // Sử dụng pessimistic lock để tránh race condition
            Seller seller = em.find(Seller.class, sellerId, LockModeType.PESSIMISTIC_WRITE);
            
            if (seller == null) {
                tx.rollback();
                return 1; // Seller không tồn tại
            }
            
            // Kiểm tra trạng thái hiện tại - tránh thao tác không cần thiết
            if (seller.isBanned() == banned) {
                tx.rollback();
                return 2; // Trạng thái đã giống như yêu cầu
            }
            
            seller.setBanned(banned);
            em.merge(seller);
            tx.commit();
            return 0; // Thành công
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
            return 1;
        } finally {
            em.close();
        }
    }

    /**
     * Cập nhật trạng thái ban của Buyer với đồng bộ hóa
     * @return 0: thành công, 1: buyer không tồn tại, 2: trạng thái đã giống như yêu cầu
     */
    private int updateBuyerBanStatus(String buyerId, boolean banned) {
        EntityManager em = getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            // Sử dụng pessimistic lock để tránh race condition
            Buyer buyer = em.find(Buyer.class, buyerId, LockModeType.PESSIMISTIC_WRITE);
            
            if (buyer == null) {
                tx.rollback();
                return 1; // Buyer không tồn tại
            }
            
            // Kiểm tra trạng thái hiện tại - tránh thao tác không cần thiết
            if (buyer.isBanned() == banned) {
                tx.rollback();
                return 2; // Trạng thái đã giống như yêu cầu
            }
            
            buyer.setBanned(banned);
            em.merge(buyer);
            tx.commit();
            return 0; // Thành công
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
            return 1;
        } finally {
            em.close();
        }
    }

    /**
     * Cập nhật trạng thái ban của Shipper với đồng bộ hóa
     * @return 0: thành công, 1: shipper không tồn tại, 2: trạng thái đã giống như yêu cầu
     */
    private int updateShipperBanStatus(String shipperId, boolean banned) {
        EntityManager em = getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            // Sử dụng pessimistic lock để tránh race condition
            Shipper shipper = em.find(Shipper.class, shipperId, LockModeType.PESSIMISTIC_WRITE);
            
            if (shipper == null) {
                tx.rollback();
                return 1; // Shipper không tồn tại
            }
            
            // Kiểm tra trạng thái hiện tại - tránh thao tác không cần thiết
            if (shipper.isBanned() == banned) {
                tx.rollback();
                return 2; // Trạng thái đã giống như yêu cầu
            }
            
            shipper.setBanned(banned);
            em.merge(shipper);
            tx.commit();
            return 0; // Thành công
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
            return 1;
        } finally {
            em.close();
        }
    }
}