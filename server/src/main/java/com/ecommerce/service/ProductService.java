package com.ecommerce.service;

import java.util.List;

import com.ecommerce.dto.ProductDTO;
import com.ecommerce.entity.Product;
import com.ecommerce.entity.ProductStatus;
import com.ecommerce.entity.Seller;
import com.ecommerce.util.DBUtil;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;

public class ProductService {

    // 1. Thêm Product mới
    public void addProduct(String sellerId, ProductDTO dto) throws Exception {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        EntityTransaction trans = em.getTransaction();
        try {
            trans.begin();
            Seller seller = em.find(Seller.class, sellerId);
            if (seller == null) throw new Exception("Seller not found");

            Product product = new Product();
            product.setName(dto.name);
            product.setDescription(dto.description);
            product.setOriginalPrice(dto.originalPrice);
            product.setSalePrice(dto.salePrice);
            product.setQuantity(dto.quantity);
            product.setExpirationDate(dto.expirationDate);
            product.setManufactureDate(dto.manufactureDate);
            product.setStatus(ProductStatus.PENDING_APPROVAL); // Mặc định hiển thị
            product.setSeller(seller);
            product.setVerified(false); // Chưa được xác minh

            em.persist(product);
            trans.commit();
        } catch (Exception e) {
            if (trans.isActive()) trans.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    // 2. Cập nhật Product (Sửa hoặc Ẩn)
    public void updateProduct(String sellerId, ProductDTO dto) throws Exception {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        EntityTransaction trans = em.getTransaction();
        try {
            trans.begin();
            Product product = em.find(Product.class, dto.productId);
            
            if (product == null) throw new Exception("Sản phẩm không tồn tại");
            
            // QUAN TRỌNG: Kiểm tra sản phẩm có thuộc về Seller đang đăng nhập không
            if (!product.getSeller().getUserId().equals(sellerId)) {
                throw new Exception("Bạn không có quyền sửa sản phẩm này");
            }

            // Cập nhật field
            product.setName(dto.name);
            product.setDescription(dto.description);
            product.setOriginalPrice(dto.originalPrice);
            product.setSalePrice(dto.salePrice);
            product.setQuantity(dto.quantity);
            product.setExpirationDate(dto.expirationDate);
            
            // Cập nhật trạng thái (Ẩn/Hiện)
            if (dto.status != null) {
                product.setStatus(dto.status);
            }

            em.merge(product);
            trans.commit();
        } catch (Exception e) {
            if (trans.isActive()) trans.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    // 3. Xóa Product
    public void deleteProduct(String sellerId, Long productId) throws Exception {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        EntityTransaction trans = em.getTransaction();
        try {
            trans.begin();
            Product product = em.find(Product.class, productId);
            
            if (product != null) {
                 if (!product.getSeller().getUserId().equals(sellerId)) {
                    throw new Exception("Bạn không có quyền xóa sản phẩm này");
                }
                // Hard delete (Lưu ý: sẽ lỗi nếu sản phẩm đã có trong OrderDetail. 
                // Nên dùng try-catch hoặc chuyển sang ẩn sản phẩm bằng updateProduct)
                em.remove(product); 
            }
            trans.commit();
        } catch (Exception e) {
            if (trans.isActive()) trans.rollback();
            throw new Exception("Không thể xóa sản phẩm (có thể do đã có đơn hàng). Hãy thử ẩn sản phẩm.");
        } finally {
            em.close();
        }
    }

    // 4. Lấy danh sách Product của Seller
    public List<Product> getProductsBySeller(String sellerId) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            TypedQuery<Product> query = em.createQuery(
                "SELECT p FROM Product p WHERE p.seller.userId = :sid", Product.class);
            query.setParameter("sid", sellerId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
    
    // Hàm dành cho ADMIN duyệt hoặc từ chối sản phẩm
    public void reviewProduct(Long productId, boolean isApproved) throws Exception {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        EntityTransaction trans = em.getTransaction();
        try {
            trans.begin();
            Product product = em.find(Product.class, productId);
            if (product == null) throw new Exception("Product not found");

            if (isApproved) {
                product.setStatus(ProductStatus.ACTIVE);
                product.setVerified(true);
            } else {
                product.setStatus(ProductStatus.REJECTED);
            }
            em.merge(product);
            trans.commit();
        } finally {
            em.close();
        }
    }
}