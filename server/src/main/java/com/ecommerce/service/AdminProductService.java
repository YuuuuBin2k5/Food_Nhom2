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
     * Lấy Product đầu tiên cần duyệt (đăng trước duyệt trước - FIFO)
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
            // Lưu ngày kiểm duyệt cho cả duyệt và từ chối
            product.setApprovedDate(new java.util.Date());
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
     * Lấy danh sách Product theo trạng thái (sắp xếp cũ nhất trước - FIFO cho pending)
     */
    public List<Product> getProductsByStatus(ProductStatus status) {
        EntityManager em = getEntityManager();
        try {
            // Pending: cũ nhất trước (FIFO), Others: mới nhất trước
            String orderBy = (status == ProductStatus.PENDING_APPROVAL) ? 
                "ORDER BY p.createdDate ASC" : 
                "ORDER BY p.createdDate DESC";
                
            TypedQuery<Product> query = em.createQuery(
                "SELECT DISTINCT p FROM Product p LEFT JOIN FETCH p.seller WHERE p.status = :status " + orderBy, 
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

    // ==================== SORT METHODS ====================

    /**
     * Lấy products theo trạng thái, sắp xếp theo ngày cũ nhất trước
     */
    public List<Product> getProductsByStatusSortOldest(ProductStatus status) {
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
     * Lấy products theo trạng thái, sắp xếp theo ngày mới nhất trước
     */
    public List<Product> getProductsByStatusSortNewest(ProductStatus status) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Product> query = em.createQuery(
                "SELECT DISTINCT p FROM Product p LEFT JOIN FETCH p.seller WHERE p.status = :status ORDER BY p.createdDate DESC", 
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
     * Lấy products theo trạng thái, sắp xếp theo tên sản phẩm A-Z
     */
    public List<Product> getProductsByStatusSortByName(ProductStatus status) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Product> query = em.createQuery(
                "SELECT DISTINCT p FROM Product p LEFT JOIN FETCH p.seller WHERE p.status = :status ORDER BY p.name ASC", 
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
     * Lấy products theo trạng thái, sắp xếp theo tên shop A-Z
     */
    public List<Product> getProductsByStatusSortByShop(ProductStatus status) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Product> query = em.createQuery(
                "SELECT DISTINCT p FROM Product p LEFT JOIN FETCH p.seller WHERE p.status = :status ORDER BY p.seller.shopName ASC", 
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
     * Lấy tất cả products, sắp xếp theo ngày cũ nhất trước
     */
    public List<Product> getAllProductsSortOldest() {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Product> query = em.createQuery(
                "SELECT DISTINCT p FROM Product p LEFT JOIN FETCH p.seller ORDER BY p.createdDate ASC", 
                Product.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Lấy tất cả products, sắp xếp theo ngày mới nhất trước
     */
    public List<Product> getAllProductsSortNewest() {
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
     * Lấy tất cả products, sắp xếp theo tên sản phẩm A-Z
     */
    public List<Product> getAllProductsSortByName() {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Product> query = em.createQuery(
                "SELECT DISTINCT p FROM Product p LEFT JOIN FETCH p.seller ORDER BY p.name ASC", 
                Product.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Lấy tất cả products, sắp xếp theo tên shop A-Z
     */
    public List<Product> getAllProductsSortByShop() {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Product> query = em.createQuery(
                "SELECT DISTINCT p FROM Product p LEFT JOIN FETCH p.seller ORDER BY p.seller.shopName ASC", 
                Product.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    // ==================== THỐNG KÊ ====================

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
}