package com.mycompany.dao;

import com.mycompany.model.Seller;
import com.mycompany.model.SellerStatus;
import com.mycompany.util.JPAUtil;
import jakarta.persistence.*;
import java.util.List;

public class SellerDAO {

    private EntityManager getEntityManager() {
        return JPAUtil.getEntityManager();
    }

    /**
     * Lấy Seller đầu tiên cần duyệt (FIFO - ai nộp giấy phép trước duyệt trước)
     * Sắp xếp theo ngày nộp giấy phép tăng dần
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
     * Lấy tất cả Seller
     */
    public List<Seller> getAllSellers() {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Seller> query = em.createQuery("SELECT s FROM Seller s", Seller.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
}
