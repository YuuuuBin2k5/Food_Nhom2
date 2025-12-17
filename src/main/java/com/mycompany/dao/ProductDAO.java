package com.mycompany.dao;

import com.mycompany.model.Product;
import com.mycompany.model.ProductStatus;
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
}
