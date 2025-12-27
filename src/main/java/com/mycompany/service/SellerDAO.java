package com.mycompany.service;

import com.mycompany.entity.Seller;
import com.mycompany.entity.SellerStatus;
import com.mycompany.util.JPAUtil;
import jakarta.persistence.*;
import java.util.List;

public class SellerDAO {

    private EntityManager getEntityManager() {
        return JPAUtil.getEntityManager();
    }

    /**
     * Lấy Seller cũ nhất cần duyệt (nộp giấy phép sớm nhất - ai đến trước duyệt trước)
     */
    public Seller getFirstPendingSeller() {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Seller> query = em.createQuery(
                "SELECT s FROM Seller s WHERE s.verificationStatus = :status ORDER BY s.licenseSubmittedDate ASC", 
                Seller.class);
            query.setParameter("status", SellerStatus.PENDING);
            query.setMaxResults(1);
            List<Seller> result = query.getResultList();
            return result.isEmpty() ? null : result.get(0);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            em.close();
        }
    }

    /**
     * Đếm số lượng Seller đang chờ duyệt
     */
    public long countPendingSellers() {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Long> query = em.createQuery(
                "SELECT COUNT(s) FROM Seller s WHERE s.verificationStatus = :status", Long.class);
            query.setParameter("status", SellerStatus.PENDING);
            return query.getSingleResult();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        } finally {
            em.close();
        }
    }

    /**
     * Tìm Seller theo ID
     */
    public Seller findById(String sellerId) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Seller.class, sellerId);
        } finally {
            em.close();
        }
    }

    /**
     * Duyệt Seller - Chuyển status sang APPROVED
     */
    public boolean approveSeller(String sellerId) {
        return updateSellerStatus(sellerId, SellerStatus.APPROVED);
    }

    /**
     * Từ chối Seller - Chuyển status sang REJECTED
     */
    public boolean rejectSeller(String sellerId) {
        return updateSellerStatus(sellerId, SellerStatus.REJECTED);
    }

    /**
     * Cập nhật trạng thái Seller
     */
    private boolean updateSellerStatus(String sellerId, SellerStatus newStatus) {
        EntityManager em = getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Seller seller = em.find(Seller.class, sellerId);
            if (seller != null) {
                seller.setVerificationStatus(newStatus);
                // Chỉ set ngày duyệt khi APPROVED, còn lại set NULL
                if (newStatus == SellerStatus.APPROVED) {
                    seller.setLicenseApprovedDate(new java.util.Date());
                } else {
                    seller.setLicenseApprovedDate(null);
                }
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

    /**
     * Lấy tất cả Seller (sắp xếp mới nhất trước)
     */
    public List<Seller> getAllSellers() {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Seller> query = em.createQuery(
                "SELECT s FROM Seller s ORDER BY s.licenseSubmittedDate DESC", 
                Seller.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Lấy danh sách Seller theo trạng thái (sắp xếp cũ nhất trước - ai đến trước duyệt trước)
     */
    public List<Seller> getSellersByStatus(SellerStatus status) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Seller> query = em.createQuery(
                "SELECT s FROM Seller s WHERE s.verificationStatus = :status ORDER BY s.licenseSubmittedDate ASC", 
                Seller.class);
            query.setParameter("status", status);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return new java.util.ArrayList<>();
        } finally {
            em.close();
        }
    }

    /**
     * Lấy seller đã duyệt
     */
    public List<Seller> getApprovedSellers() {
        return getSellersByStatus(SellerStatus.APPROVED);
    }

    /**
     * Lấy seller đang chờ duyệt
     */
    public List<Seller> getPendingSellers() {
        return getSellersByStatus(SellerStatus.PENDING);
    }

    /**
     * Lấy seller bị từ chối
     */
    public List<Seller> getRejectedSellers() {
        return getSellersByStatus(SellerStatus.REJECTED);
    }

    /**
     * Lấy seller chưa đăng giấy phép
     */
    public List<Seller> getUnverifiedSellers() {
        return getSellersByStatus(SellerStatus.UNVERIFIED);
    }

    // ==================== PHÂN TRANG ====================

    /**
     * Đếm tổng số seller theo trạng thái
     */
    public long countByStatus(SellerStatus status) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Long> query = em.createQuery(
                "SELECT COUNT(s) FROM Seller s WHERE s.verificationStatus = :status", Long.class);
            query.setParameter("status", status);
            return query.getSingleResult();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        } finally {
            em.close();
        }
    }

    /**
     * Đếm tổng số seller
     */
    public long countAll() {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery("SELECT COUNT(s) FROM Seller s", Long.class).getSingleResult();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        } finally {
            em.close();
        }
    }

    /**
     * Lấy danh sách Seller theo trạng thái có phân trang
     */
    public List<Seller> getSellersByStatus(SellerStatus status, int page, int pageSize) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Seller> query = em.createQuery(
                "SELECT s FROM Seller s WHERE s.verificationStatus = :status ORDER BY s.licenseSubmittedDate ASC", 
                Seller.class);
            query.setParameter("status", status);
            query.setFirstResult((page - 1) * pageSize);
            query.setMaxResults(pageSize);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return new java.util.ArrayList<>();
        } finally {
            em.close();
        }
    }

    /**
     * Lấy tất cả Seller có phân trang
     */
    public List<Seller> getAllSellers(int page, int pageSize) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Seller> query = em.createQuery(
                "SELECT s FROM Seller s ORDER BY s.licenseSubmittedDate DESC", 
                Seller.class);
            query.setFirstResult((page - 1) * pageSize);
            query.setMaxResults(pageSize);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return new java.util.ArrayList<>();
        } finally {
            em.close();
        }
    }
}
