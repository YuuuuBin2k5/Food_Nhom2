package com.ecommerce.service;

import com.ecommerce.dto.ProductDTO;
import com.ecommerce.dto.SellerDTO;
import com.ecommerce.entity.*;
import com.ecommerce.util.DBUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;

import java.util.ArrayList;
import java.util.List;

public class AdminService {
    
    private final NotificationService notificationService = new NotificationService();

    // ==================== USER MANAGEMENT ====================
    
    public List<User> getAllUsers() {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            List<User> allUsers = new ArrayList<>();
            
            // Get all Buyers
            TypedQuery<Buyer> buyerQuery = em.createQuery("SELECT b FROM Buyer b ORDER BY b.fullName", Buyer.class);
            allUsers.addAll(buyerQuery.getResultList());
            
            // Get all Sellers
            TypedQuery<Seller> sellerQuery = em.createQuery("SELECT s FROM Seller s ORDER BY s.fullName", Seller.class);
            allUsers.addAll(sellerQuery.getResultList());
            
            // Get all Shippers
            TypedQuery<Shipper> shipperQuery = em.createQuery("SELECT sh FROM Shipper sh ORDER BY sh.fullName", Shipper.class);
            allUsers.addAll(shipperQuery.getResultList());
            
            return allUsers;
        } finally {
            em.close();
        }
    }
    
    public List<User> searchUsers(String keyword) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            List<User> allUsers = new ArrayList<>();
            String lowerKeyword = "%" + keyword.toLowerCase() + "%";
            
            // Search Sellers
            TypedQuery<Seller> sellerQuery = em.createQuery(
                "SELECT s FROM Seller s WHERE LOWER(s.fullName) LIKE :keyword OR LOWER(s.email) LIKE :keyword OR LOWER(s.shopName) LIKE :keyword",
                Seller.class
            );
            sellerQuery.setParameter("keyword", lowerKeyword);
            allUsers.addAll(sellerQuery.getResultList());
            
            // Search Buyers
            TypedQuery<Buyer> buyerQuery = em.createQuery(
                "SELECT b FROM Buyer b WHERE LOWER(b.fullName) LIKE :keyword OR LOWER(b.email) LIKE :keyword",
                Buyer.class
            );
            buyerQuery.setParameter("keyword", lowerKeyword);
            allUsers.addAll(buyerQuery.getResultList());
            
            // Search Shippers
            TypedQuery<Shipper> shipperQuery = em.createQuery(
                "SELECT sh FROM Shipper sh WHERE LOWER(sh.fullName) LIKE :keyword OR LOWER(sh.email) LIKE :keyword",
                Shipper.class
            );
            shipperQuery.setParameter("keyword", lowerKeyword);
            allUsers.addAll(shipperQuery.getResultList());
            
            return allUsers;
        } finally {
            em.close();
        }
    }
    
    public List<User> getBannedUsers() {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            List<User> allUsers = new ArrayList<>();
            
            // Get banned Sellers
            TypedQuery<Seller> sellerQuery = em.createQuery("SELECT s FROM Seller s WHERE s.banned = true", Seller.class);
            allUsers.addAll(sellerQuery.getResultList());
            
            // Get banned Buyers
            TypedQuery<Buyer> buyerQuery = em.createQuery("SELECT b FROM Buyer b WHERE b.banned = true", Buyer.class);
            allUsers.addAll(buyerQuery.getResultList());
            
            // Get banned Shippers
            TypedQuery<Shipper> shipperQuery = em.createQuery("SELECT sh FROM Shipper sh WHERE sh.banned = true", Shipper.class);
            allUsers.addAll(shipperQuery.getResultList());
            
            return allUsers;
        } finally {
            em.close();
        }
    }
    
    public List<Seller> getAllSellers() {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            return em.createQuery("SELECT s FROM Seller s ORDER BY s.fullName", Seller.class).getResultList();
        } finally {
            em.close();
        }
    }
    
    public List<Buyer> getAllBuyers() {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            return em.createQuery("SELECT b FROM Buyer b ORDER BY b.fullName", Buyer.class).getResultList();
        } finally {
            em.close();
        }
    }
    
    public List<Shipper> getAllShippers() {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            return em.createQuery("SELECT sh FROM Shipper sh ORDER BY sh.fullName", Shipper.class).getResultList();
        } finally {
            em.close();
        }
    }
    
    public void toggleUserBan(String userId, boolean banned) throws Exception {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        EntityTransaction trans = em.getTransaction();
        try {
            trans.begin();
            
            // Try to find in each user type
            Seller seller = em.find(Seller.class, userId);
            if (seller != null) {
                seller.setBanned(banned);
                em.merge(seller);
                trans.commit();
                return;
            }
            
            Buyer buyer = em.find(Buyer.class, userId);
            if (buyer != null) {
                buyer.setBanned(banned);
                em.merge(buyer);
                trans.commit();
                return;
            }
            
            Shipper shipper = em.find(Shipper.class, userId);
            if (shipper != null) {
                shipper.setBanned(banned);
                em.merge(shipper);
                trans.commit();
                return;
            }
            
            throw new Exception("User không tồn tại");
        } catch (Exception e) {
            if (trans.isActive()) trans.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    // ==================== SELLER APPROVAL ====================
    
    public SellerDTO getFirstPendingSeller() {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            TypedQuery<Seller> query = em.createQuery(
                "SELECT s FROM Seller s WHERE s.verificationStatus = :status ORDER BY s.licenseSubmittedDate ASC",
                Seller.class
            );
            query.setParameter("status", SellerStatus.PENDING);
            query.setMaxResults(1);
            List<Seller> results = query.getResultList();
            
            if (!results.isEmpty()) {
                Seller seller = results.get(0);
                return new SellerDTO(seller);
            }
            return null;
        } finally {
            em.close();
        }
    }
    
    public List<SellerDTO> getAllPendingSellers() {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            TypedQuery<Seller> query = em.createQuery(
                "SELECT s FROM Seller s WHERE s.verificationStatus = :status ORDER BY s.licenseSubmittedDate ASC",
                Seller.class
            );
            query.setParameter("status", SellerStatus.PENDING);
            List<Seller> sellers = query.getResultList();
            
            List<SellerDTO> dtoList = new ArrayList<>();
            for (Seller seller : sellers) {
                dtoList.add(new SellerDTO(seller));
            }
            return dtoList;
        } finally {
            em.close();
        }
    }
    
    public long countPendingSellers() {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            TypedQuery<Long> query = em.createQuery(
                "SELECT COUNT(s) FROM Seller s WHERE s.verificationStatus = :status",
                Long.class
            );
            query.setParameter("status", SellerStatus.PENDING);
            return query.getSingleResult();
        } finally {
            em.close();
        }
    }
    
    public void approveSeller(String sellerId) throws Exception {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        EntityTransaction trans = em.getTransaction();
        try {
            trans.begin();
            Seller seller = em.find(Seller.class, sellerId);
            
            if (seller == null) {
                throw new Exception("Seller không tồn tại");
            }
            
            seller.setVerificationStatus(SellerStatus.APPROVED);
            em.merge(seller);
            trans.commit();
            
            // Gửi thông báo cho seller
            notificationService.createNotification(
                seller.getUserId(),
                NotificationType.SELLER_APPROVED,
                "Tài khoản đã được duyệt",
                "Chúc mừng! Tài khoản seller của bạn đã được duyệt. Bạn có thể bắt đầu bán hàng.",
                null
            );
        } catch (Exception e) {
            if (trans.isActive()) trans.rollback();
            throw e;
        } finally {
            em.close();
        }
    }
    
    public void rejectSeller(String sellerId) throws Exception {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        EntityTransaction trans = em.getTransaction();
        try {
            trans.begin();
            Seller seller = em.find(Seller.class, sellerId);
            
            if (seller == null) {
                throw new Exception("Seller không tồn tại");
            }
            
            seller.setVerificationStatus(SellerStatus.REJECTED);
            em.merge(seller);
            trans.commit();
            
            // Gửi thông báo cho seller
            notificationService.createNotification(
                seller.getUserId(),
                NotificationType.SELLER_REJECTED,
                "Tài khoản bị từ chối",
                "Rất tiếc, tài khoản seller của bạn không được duyệt. Vui lòng liên hệ admin để biết thêm chi tiết.",
                null
            );
        } catch (Exception e) {
            if (trans.isActive()) trans.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    // ==================== PRODUCT APPROVAL ====================
    
    public ProductDTO getFirstPendingProduct() {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            TypedQuery<Product> query = em.createQuery(
                "SELECT p FROM Product p WHERE p.status = :status ORDER BY p.createdDate ASC",
                Product.class
            );
            query.setParameter("status", ProductStatus.PENDING_APPROVAL);
            query.setMaxResults(1);
            List<Product> results = query.getResultList();
            
            if (!results.isEmpty()) {
                Product product = results.get(0);
                return new ProductDTO(product);
            }
            return null;
        } finally {
            em.close();
        }
    }
    
    public List<ProductDTO> getAllPendingProducts() {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            TypedQuery<Product> query = em.createQuery(
                "SELECT p FROM Product p WHERE p.status = :status ORDER BY p.createdDate ASC",
                Product.class
            );
            query.setParameter("status", ProductStatus.PENDING_APPROVAL);
            List<Product> products = query.getResultList();
            
            List<ProductDTO> dtoList = new ArrayList<>();
            for (Product product : products) {
                dtoList.add(new ProductDTO(product));
            }
            return dtoList;
        } finally {
            em.close();
        }
    }
    
    public long countPendingProducts() {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            TypedQuery<Long> query = em.createQuery(
                "SELECT COUNT(p) FROM Product p WHERE p.status = :status",
                Long.class
            );
            query.setParameter("status", ProductStatus.PENDING_APPROVAL);
            return query.getSingleResult();
        } finally {
            em.close();
        }
    }
    
    public void approveProduct(Long productId) throws Exception {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        EntityTransaction trans = em.getTransaction();
        try {
            trans.begin();
            Product product = em.find(Product.class, productId);
            
            if (product == null) {
                throw new Exception("Sản phẩm không tồn tại");
            }
            
            product.setStatus(ProductStatus.ACTIVE);
            product.setVerified(true);
            em.merge(product);
            trans.commit();
            
            // Gửi thông báo cho seller
            notificationService.createNotification(
                product.getSeller().getUserId(),
                NotificationType.PRODUCT_APPROVED,
                "Sản phẩm đã được duyệt",
                "Sản phẩm '" + product.getName() + "' đã được duyệt và hiển thị trên cửa hàng.",
                productId
            );
        } catch (Exception e) {
            if (trans.isActive()) trans.rollback();
            throw e;
        } finally {
            em.close();
        }
    }
    
    public void rejectProduct(Long productId) throws Exception {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        EntityTransaction trans = em.getTransaction();
        try {
            trans.begin();
            Product product = em.find(Product.class, productId);
            
            if (product == null) {
                throw new Exception("Sản phẩm không tồn tại");
            }
            
            product.setStatus(ProductStatus.REJECTED);
            em.merge(product);
            trans.commit();
            
            // Gửi thông báo cho seller
            notificationService.createNotification(
                product.getSeller().getUserId(),
                NotificationType.PRODUCT_REJECTED,
                "Sản phẩm bị từ chối",
                "Sản phẩm '" + product.getName() + "' không được duyệt. Vui lòng kiểm tra lại thông tin.",
                productId
            );
        } catch (Exception e) {
            if (trans.isActive()) trans.rollback();
            throw e;
        } finally {
            em.close();
        }
    }
}
