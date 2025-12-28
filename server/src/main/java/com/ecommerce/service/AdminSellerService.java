package com.ecommerce.service;

import com.ecommerce.entity.Seller;
import com.ecommerce.entity.SellerStatus;
import com.ecommerce.util.DBUtil;
import jakarta.persistence.*;
import java.util.List;

public class AdminSellerService {

    private EntityManager getEntityManager() {
        return DBUtil.getEmFactory().createEntityManager();
    }

    /**
     * Lấy Seller cũ nhất cần duyệt (nộp giấy phép sớm nhất - ai đến trước duyệt
     * trước)
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
     * Duyệt Seller - Chuyển status sang APPROVED (chỉ khi đang PENDING)
     * 
     * @return 0: thành công, 1: seller không tồn tại, 2: đã được xử lý bởi admin
     *         khác
     */
    public int approveSeller(String sellerId) {
        return updateSellerStatus(sellerId, SellerStatus.APPROVED);
    }

    /**
     * Từ chối Seller - Chuyển status sang REJECTED (chỉ khi đang PENDING)
     * 
     * @return 0: thành công, 1: seller không tồn tại, 2: đã được xử lý bởi admin
     *         khác
     */
    public int rejectSeller(String sellerId) {
        return updateSellerStatus(sellerId, SellerStatus.REJECTED);
    }

    /**
     * Đếm số lượng Seller theo status
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
     * Đếm tổng số Seller
     */
    public long countAll() {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Long> query = em.createQuery(
                    "SELECT COUNT(s) FROM Seller s", Long.class);
            return query.getSingleResult();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        } finally {
            em.close();
        }
    }

    /**
     * Lấy danh sách Seller theo trạng thái (sắp xếp cũ nhất trước - ai đến trước
     * duyệt trước)
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

    /**
     * Cập nhật status của Seller với pessimistic locking
     */
    private int updateSellerStatus(String sellerId, SellerStatus newStatus) {
        EntityManager em = getEntityManager();
        EntityTransaction trans = em.getTransaction();
        try {
            trans.begin();

            // Sử dụng pessimistic lock để tránh race condition
            Seller seller = em.find(Seller.class, sellerId, LockModeType.PESSIMISTIC_WRITE);

            if (seller == null) {
                trans.rollback();
                return 1; // Seller không tồn tại
            }

            // Chỉ cho phép thay đổi từ PENDING
            if (seller.getVerificationStatus() != SellerStatus.PENDING) {
                trans.rollback();
                return 2; // Đã được xử lý bởi admin khác
            }

            seller.setVerificationStatus(newStatus);
            if (newStatus == SellerStatus.APPROVED) {
                seller.setLicenseApprovedDate(new java.util.Date());
            }

            em.merge(seller);
            trans.commit();
            return 0; // Thành công

        } catch (Exception e) {
            if (trans.isActive())
                trans.rollback();
            e.printStackTrace();
            return 1; // Lỗi
        } finally {
            em.close();
        }
    }
}