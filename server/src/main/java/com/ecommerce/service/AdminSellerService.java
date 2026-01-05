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
     * @return 0: thành công, 1: seller không tồn tại, 2: đã được xử lý bởi admin khác
     */
    public int approveSeller(String sellerId) {
        return updateSellerStatus(sellerId, SellerStatus.APPROVED);
    }

    /**
     * Từ chối Seller - Chuyển status sang REJECTED (chỉ khi đang PENDING)
     * @return 0: thành công, 1: seller không tồn tại, 2: đã được xử lý bởi admin khác
     */
    public int rejectSeller(String sellerId) {
        return updateSellerStatus(sellerId, SellerStatus.REJECTED);
    }

    /**
     * Cập nhật trạng thái Seller với đồng bộ hóa (pessimistic lock)
     * Chỉ cho phép cập nhật nếu seller đang ở trạng thái PENDING
     * @return 0: thành công, 1: seller không tồn tại, 2: đã được xử lý bởi admin khác
     */
    private int updateSellerStatus(String sellerId, SellerStatus newStatus) {
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
            
            // Kiểm tra trạng thái hiện tại - chỉ cho phép duyệt/từ chối nếu đang PENDING
            if (seller.getVerificationStatus() != SellerStatus.PENDING) {
                tx.rollback();
                return 2; // Đã được xử lý bởi admin khác
            }
            
            seller.setVerificationStatus(newStatus);
            // Lưu ngày kiểm duyệt cho cả duyệt và từ chối
            seller.setLicenseApprovedDate(new java.util.Date());
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
     * Lấy tất cả Seller theo trạng thái (sắp xếp cũ nhất trước - FIFO cho pending)
     */
    public List<Seller> getAllSellersByStatus(SellerStatus status) {
        EntityManager em = getEntityManager();
        try {
            // Pending: cũ nhất trước (FIFO), Others: mới nhất trước
            String orderBy = (status == SellerStatus.PENDING) ? 
                "ORDER BY s.licenseSubmittedDate ASC" : 
                "ORDER BY s.licenseSubmittedDate DESC";
                
            TypedQuery<Seller> query = em.createQuery(
                "SELECT s FROM Seller s WHERE s.verificationStatus = :status " + orderBy, 
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

    // ==================== SORT METHODS ====================

    /**
     * Lấy sellers theo trạng thái, sắp xếp theo ngày cũ nhất trước
     */
    public List<Seller> getAllSellersByStatusSortOldest(SellerStatus status) {
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
     * Lấy sellers theo trạng thái, sắp xếp theo ngày mới nhất trước
     */
    public List<Seller> getAllSellersByStatusSortNewest(SellerStatus status) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Seller> query = em.createQuery(
                "SELECT s FROM Seller s WHERE s.verificationStatus = :status ORDER BY s.licenseSubmittedDate DESC", 
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
     * Lấy sellers theo trạng thái, sắp xếp theo tên chủ shop A-Z
     */
    public List<Seller> getAllSellersByStatusSortByName(SellerStatus status) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Seller> query = em.createQuery(
                "SELECT s FROM Seller s WHERE s.verificationStatus = :status ORDER BY s.fullName ASC", 
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
     * Lấy sellers theo trạng thái, sắp xếp theo tên shop A-Z
     */
    public List<Seller> getAllSellersByStatusSortByShop(SellerStatus status) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Seller> query = em.createQuery(
                "SELECT s FROM Seller s WHERE s.verificationStatus = :status ORDER BY s.shopName ASC", 
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
     * Lấy tất cả sellers, sắp xếp theo ngày cũ nhất trước
     */
    public List<Seller> getAllSellersSortOldest() {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Seller> query = em.createQuery(
                "SELECT s FROM Seller s ORDER BY s.licenseSubmittedDate ASC", 
                Seller.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Lấy tất cả sellers, sắp xếp theo ngày mới nhất trước
     */
    public List<Seller> getAllSellersSortNewest() {
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
     * Lấy tất cả sellers, sắp xếp theo tên chủ shop A-Z
     */
    public List<Seller> getAllSellersSortByName() {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Seller> query = em.createQuery(
                "SELECT s FROM Seller s ORDER BY s.fullName ASC", 
                Seller.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Lấy tất cả sellers, sắp xếp theo tên shop A-Z
     */
    public List<Seller> getAllSellersSortByShop() {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Seller> query = em.createQuery(
                "SELECT s FROM Seller s ORDER BY s.shopName ASC", 
                Seller.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    // ==================== THỐNG KÊ ====================

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
}