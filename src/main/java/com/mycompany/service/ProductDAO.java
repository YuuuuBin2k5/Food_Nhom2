package com.mycompany.service;

import com.mycompany.entity.Product;
import com.mycompany.entity.ProductStatus;
import com.mycompany.util.JPAUtil;
import jakarta.persistence.*;
import java.util.List;

public class ProductDAO {

    private EntityManager getEntityManager() {
        return JPAUtil.getEntityManager();
    }

    /**
     * Lấy Product đầu tiên cần duyệt (FIFO - sản phẩm nào đăng trước duyệt trước)
     */
    public Product getFirstPendingProduct() {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Product> query = em.createQuery(
                "SELECT p FROM Product p WHERE p.status = :status ORDER BY p.createdDate ASC", 
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
            return em.find(Product.class, productId);
        } finally {
            em.close();
        }
    }

    /**
     * Duyệt Product - Chuyển status sang ACTIVE
     */
    public boolean approveProduct(Long productId) {
        return updateProductStatus(productId, ProductStatus.ACTIVE);
    }

    /**
     * Từ chối Product - Chuyển status sang REJECTED
     */
    public boolean rejectProduct(Long productId) {
        return updateProductStatus(productId, ProductStatus.REJECTED);
    }

    /**
     * Cập nhật trạng thái Product
     */
    private boolean updateProductStatus(Long productId, ProductStatus newStatus) {
        EntityManager em = getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Product product = em.find(Product.class, productId);
            if (product != null) {
                product.setStatus(newStatus);
                // Chỉ set ngày duyệt khi ACTIVE, còn lại set NULL
                if (newStatus == ProductStatus.ACTIVE) {
                    product.setApprovedDate(new java.util.Date());
                } else {
                    product.setApprovedDate(null);
                }
                em.merge(product);
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
     * Lấy tất cả Product (sắp xếp mới nhất trước)
     */
    public List<Product> getAllProducts() {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Product> query = em.createQuery(
                "SELECT p FROM Product p ORDER BY p.createdDate DESC", 
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
                "SELECT p FROM Product p WHERE p.status = :status ORDER BY p.createdDate ASC", 
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
                "SELECT p FROM Product p WHERE p.status = :status ORDER BY p.createdDate ASC", 
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
                "SELECT p FROM Product p ORDER BY p.createdDate DESC", 
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
