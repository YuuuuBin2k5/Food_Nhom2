package com.ecommerce.service;

import com.ecommerce.entity.Product;
import com.ecommerce.entity.ProductStatus;
import com.ecommerce.util.DBUtil;
import jakarta.persistence.*;
import java.util.List;

public class AdminProductService {

    private EntityManager getEntityManager() {
        return DBUtil.getEmFactory().createEntityManager();
    }

    /**
     * Lấy Product đầu tiên cần duyệt
     */
    public Product getFirstPendingProduct() {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Product> query = em.createQuery(
                "SELECT p FROM Product p LEFT JOIN FETCH p.seller WHERE p.status = :status ORDER BY p.createdDate ASC", 
                Product.class);
            query.setParameter("status", ProductStatus.PENDING_APPROVAL);
            query.setMaxResults(1);
            List<Product> result = query.getResultList();
            return result.isEmpty() ? null : result.get(0);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            em.close();
        }
    }

    /**
     * Đếm số lượng Product đang chờ duyệt
     */
    public long countPendingProducts() {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Long> query = em.createQuery(
                "SELECT COUNT(p) FROM Product p WHERE p.status = :status", Long.class);
            query.setParameter("status", ProductStatus.PENDING_APPROVAL);
            return query.getSingleResult();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        } finally {
            em.close();
        }
    }

    /**
     * Tìm Product theo ID
     */
    public Product findById(Long productId) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Product> query = em.createQuery(
                "SELECT p FROM Product p LEFT JOIN FETCH p.seller WHERE p.productId = :id", 
                Product.class);
            query.setParameter("id", productId);
            List<Product> result = query.getResultList();
            return result.isEmpty() ? null : result.get(0);
        } finally {
            em.close();
        }
    }

    /**
     * Duyệt Product - Chuyển status sang ACTIVE (chỉ khi đang PENDING_APPROVAL)
     * @return 0: thành công, 1: product không tồn tại, 2: đã được xử lý bởi admin khác
     */
    public int approveProduct(Long productId) {
        return updateProductStatus(productId, ProductStatus.ACTIVE);
    }

    /**
     * Từ chối Product - Chuyển status sang REJECTED (chỉ khi đang PENDING_APPROVAL)
     * @return 0: thành công, 1: product không tồn tại, 2: đã được xử lý bởi admin khác
     */
    public int rejectProduct(Long productId) {
        return updateProductStatus(productId, ProductStatus.REJECTED);
    }

    /**
     * Cập nhật trạng thái Product với đồng bộ hóa (pessimistic lock)
     * Chỉ cho phép cập nhật nếu product đang ở trạng thái PENDING_APPROVAL
     * @return 0: thành công, 1: product không tồn tại, 2: đã được xử lý bởi admin khác
     */
    private int updateProductStatus(Long productId, ProductStatus newStatus) {
        EntityManager em = getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            // Sử dụng pessimistic lock để tránh race condition
            Product product = em.find(Product.class, productId, LockModeType.PESSIMISTIC_WRITE);
            
            if (product == null) {
                tx.rollback();
                return 1; // Product không tồn tại
            }
            
            // Kiểm tra trạng thái hiện tại - chỉ cho phép duyệt/từ chối nếu đang PENDING_APPROVAL
            if (product.getStatus() != ProductStatus.PENDING_APPROVAL) {
                tx.rollback();
                return 2; // Đã được xử lý bởi admin khác
            }
            
            product.setStatus(newStatus);
            if (newStatus == ProductStatus.ACTIVE) {
                product.setApprovedDate(new java.util.Date());
            } else {
                product.setApprovedDate(null);
            }
            em.merge(product);
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
     * Lấy tất cả Product (sắp xếp mới nhất trước)
     */
    public List<Product> getAllProducts() {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Product> query = em.createQuery(
                "SELECT DISTINCT p FROM Product p LEFT JOIN FETCH p.seller ORDER BY p.createdDate DESC", 
                Product.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Lấy danh sách Product theo trạng thái (sắp xếp cũ nhất trước - ai đăng trước duyệt trước)
     */
    public List<Product> getProductsByStatus(ProductStatus status) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Product> query = em.createQuery(
                "SELECT DISTINCT p FROM Product p LEFT JOIN FETCH p.seller WHERE p.status = :status ORDER BY p.createdDate ASC", 
                Product.class);
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
     * Lấy product đang chờ duyệt
     */
    public List<Product> getPendingProducts() {
        return getProductsByStatus(ProductStatus.PENDING_APPROVAL);
    }

    /**
     * Lấy product bị từ chối
     */
    public List<Product> getRejectedProducts() {
        return getProductsByStatus(ProductStatus.REJECTED);
    }

    /**
     * Lấy product đã duyệt (ACTIVE)
     */
    public List<Product> getActiveProducts() {
        return getProductsByStatus(ProductStatus.ACTIVE);
    }

    // ==================== PHÂN TRANG ====================

    /**
     * Đếm tổng số product theo trạng thái
     */
    public long countByStatus(ProductStatus status) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Long> query = em.createQuery(
                "SELECT COUNT(p) FROM Product p WHERE p.status = :status", Long.class);
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
     * Đếm tổng số product
     */
    public long countAll() {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery("SELECT COUNT(p) FROM Product p", Long.class).getSingleResult();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        } finally {
            em.close();
        }
    }

    /**
     * Lấy danh sách Product theo trạng thái có phân trang
     */
    public List<Product> getProductsByStatus(ProductStatus status, int page, int pageSize) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Product> query = em.createQuery(
                "SELECT DISTINCT p FROM Product p LEFT JOIN FETCH p.seller WHERE p.status = :status ORDER BY p.createdDate ASC", 
                Product.class);
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
     * Lấy tất cả Product có phân trang
     */
    public List<Product> getAllProducts(int page, int pageSize) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Product> query = em.createQuery(
                "SELECT DISTINCT p FROM Product p LEFT JOIN FETCH p.seller ORDER BY p.createdDate DESC", 
                Product.class);
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
