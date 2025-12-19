package com.ecommerce.service;

import java.util.List;
import java.util.stream.Collectors;

import com.ecommerce.dto.ProductDTO;
import com.ecommerce.dto.ProductFilter;
import com.ecommerce.dto.ProductPageResponse;
import com.ecommerce.dto.SellerDTO;
import com.ecommerce.entity.Admin;
import com.ecommerce.entity.NotificationType;
import com.ecommerce.entity.Product;
import com.ecommerce.entity.ProductStatus;
import com.ecommerce.entity.Seller;
import com.ecommerce.util.DBUtil;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;

public class ProductService {
    
    private final NotificationService notificationService = new NotificationService();
    
    private static final String BASE_JPQL = "SELECT p FROM Product p LEFT JOIN FETCH p.seller WHERE 1=1";
    private static final String BASE_COUNT_JPQL = "SELECT COUNT(p) FROM Product p WHERE 1=1";

    // 1. Thêm Product mới
    public void addProduct(String sellerId, ProductDTO dto) throws Exception {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        EntityTransaction trans = em.getTransaction();
        try {
            trans.begin();
            Seller seller = em.find(Seller.class, sellerId);
            if (seller == null) throw new Exception("Seller not found");

            Product product = new Product();
            product.setName(dto.getName());
            product.setDescription(dto.getDescription());
            product.setOriginalPrice(dto.getOriginalPrice());
            product.setSalePrice(dto.getSalePrice());
            product.setQuantity(dto.getQuantity());
            product.setExpirationDate(dto.getExpirationDate());
            product.setManufactureDate(dto.getManufactureDate());
            product.setStatus(ProductStatus.PENDING_APPROVAL); // Mặc định hiển thị
            product.setSeller(seller);
            product.setVerified(false); // Chưa được xác minh

            em.persist(product);
            
            // Flush để lấy productId
            em.flush();
            
            // Gửi notification cho tất cả admin
            System.out.println("=== [ProductService] Product created, creating notifications for admins");
            
            TypedQuery<Admin> adminQuery = em.createQuery("SELECT a FROM Admin a", Admin.class);
            List<Admin> admins = adminQuery.getResultList();
            
            System.out.println("=== [ProductService] Found " + admins.size() + " admins");
            
            for (Admin admin : admins) {
                try {
                    System.out.println("=== [ProductService] Creating notification for admin: " + admin.getEmail());
                    notificationService.createNotification(
                        em,
                        admin.getUserId(),
                        NotificationType.NEW_PRODUCT_PENDING,
                        "Sản phẩm mới chờ duyệt",
                        "Sản phẩm '" + dto.getName() + "' từ seller '" + seller.getShopName() + "' chờ duyệt.",
                        product.getProductId()
                    );
                    System.out.println("=== [ProductService] ✅ Notification created successfully");
                } catch (Exception e) {
                    System.err.println("=== [ProductService] ❌ Failed to create notification: " + e.getMessage());
                    e.printStackTrace();
                }
            }
            
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
            Product product = em.find(Product.class, dto.getProductId());
            
            if (product == null) throw new Exception("Sản phẩm không tồn tại");
            
            // QUAN TRỌNG: Kiểm tra sản phẩm có thuộc về Seller đang đăng nhập không
            if (!product.getSeller().getUserId().equals(sellerId)) {
                throw new Exception("Bạn không có quyền sửa sản phẩm này");
            }

            // Cập nhật field
            product.setName(dto.getName());
            product.setDescription(dto.getDescription());
            product.setOriginalPrice(dto.getOriginalPrice());
            product.setSalePrice(dto.getSalePrice());
            product.setQuantity(dto.getQuantity());
            product.setExpirationDate(dto.getExpirationDate());
            
            // Cập nhật trạng thái (Ẩn/Hiện)
            if (dto.getStatus() != null) {
                product.setStatus(dto.getStatus());
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
            // Eager fetch seller để tránh LazyInitializationException
            TypedQuery<Product> query = em.createQuery(
                "SELECT p FROM Product p JOIN FETCH p.seller WHERE p.seller.userId = :sid", Product.class);
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

    // 5. Lấy danh sách sản phẩm đang active (dành cho trang mua hàng)
    public List<Product> getActiveProducts() {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            TypedQuery<Product> query = em.createQuery(
                "SELECT p FROM Product p WHERE p.status = :st AND p.isVerified = true", Product.class);
            query.setParameter("st", ProductStatus.ACTIVE);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    // 6. Lấy chi tiết 1 product nếu đang active và verified
    public Product getActiveProductById(Long productId) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            TypedQuery<Product> query = em.createQuery(
                "SELECT p FROM Product p WHERE p.productId = :id AND p.status = :st AND p.isVerified = true", Product.class);
            query.setParameter("id", productId);
            query.setParameter("st", ProductStatus.ACTIVE);
            return query.getResultStream().findFirst().orElse(null);
        } finally {
            em.close();
        }
    }

    /**
     * Get paginated list of products with filters (for Buyer)
     * @param filter ProductFilter containing search criteria and sorting options
     * @return ProductPageResponse with paginated results
     */
    public ProductPageResponse getProducts(ProductFilter filter) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        
        try {
            // Build WHERE clause
            String whereClause = buildWhereClause(filter);
            String orderClause = buildOrderClause(filter);
            
            // Main query with pagination
            String jpql = BASE_JPQL + whereClause + orderClause;
            TypedQuery<Product> query = em.createQuery(jpql, Product.class);
            setQueryParameters(query, filter);
            
            // Count query for pagination
            String countJpql = BASE_COUNT_JPQL + whereClause;
            TypedQuery<Long> countQuery = em.createQuery(countJpql, Long.class);
            setQueryParameters(countQuery, filter);
            
            Long totalElements = countQuery.getSingleResult();
            
            // Apply pagination
            query.setFirstResult(filter.getPage() * filter.getSize());
            query.setMaxResults(filter.getSize());
            
            List<Product> products = query.getResultList();
            
            // Convert to DTOs
            List<ProductDTO> productDTOs = products.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
            
            // Build response
            return buildPageResponse(productDTOs, filter, totalElements);
            
        } finally {
            em.close();
        }
    }

    /**
     * Get product by ID (for Buyer)
     * @param productId Product identifier
     * @return ProductDTO with full product information
     * @throws Exception if product not found
     */
    public ProductDTO getProductById(Long productId) throws Exception {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        
        try {
            Product product = em.find(Product.class, productId);
            
            if (product == null) {
                throw new Exception("Sản phẩm không tồn tại: " + productId);
            }
            
            // Initialize lazy-loaded seller to avoid LazyInitializationException
            if (product.getSeller() != null) {
                product.getSeller().getShopName();
            }
            
            return convertToDTO(product);
            
        } finally {
            em.close();
        }
    }

    /**
     * Build WHERE clause dynamically based on filters
     */
    private String buildWhereClause(ProductFilter filter) {
        StringBuilder where = new StringBuilder();
        
        // Search filter
        if (filter.getSearch() != null && !filter.getSearch().isEmpty()) {
            where.append(" AND (LOWER(p.name) LIKE :search OR LOWER(p.description) LIKE :search)");
        }
        
        // Price range filters
        if (filter.getMinPrice() != null) {
            where.append(" AND p.salePrice >= :minPrice");
        }
        if (filter.getMaxPrice() != null) {
            where.append(" AND p.salePrice <= :maxPrice");
        }
        
        // Product status filters
        if (filter.getHasDiscount() != null && filter.getHasDiscount()) {
            where.append(" AND p.originalPrice > p.salePrice");
        }
        if (filter.getInStock() != null && filter.getInStock()) {
            where.append(" AND p.quantity > 0");
        }
        
        // Seller filter
        if (filter.getSellerId() != null && !filter.getSellerId().isEmpty()) {
            where.append(" AND p.seller.userId = :sellerId");
        }
        
        // Always show only active and verified products
        where.append(" AND p.status = :status AND p.isVerified = :verified");
        
        return where.toString();
    }

    /**
     * Build ORDER BY clause based on sort option
     */
    private String buildOrderClause(ProductFilter filter) {
        String sortBy = filter.getSortBy() != null ? filter.getSortBy() : "newest";
        
        switch (sortBy) {
            case "price_asc":
                return " ORDER BY p.salePrice ASC";
            case "price_desc":
                return " ORDER BY p.salePrice DESC";
            case "name_asc":
                return " ORDER BY p.name ASC";
            case "expiration":
                return " ORDER BY p.expirationDate ASC";
            case "discount_desc":
                return " ORDER BY (p.originalPrice - p.salePrice) DESC";
            default: // newest
                return " ORDER BY p.productId DESC";
        }
    }

    /**
     * Set parameters for both main and count queries
     */
    private <T> void setQueryParameters(TypedQuery<T> query, ProductFilter filter) {
        // Search parameter
        if (filter.getSearch() != null && !filter.getSearch().isEmpty()) {
            query.setParameter("search", "%" + filter.getSearch().toLowerCase() + "%");
        }
        
        // Price parameters
        if (filter.getMinPrice() != null) {
            query.setParameter("minPrice", filter.getMinPrice());
        }
        if (filter.getMaxPrice() != null) {
            query.setParameter("maxPrice", filter.getMaxPrice());
        }
        
        // Seller parameter
        if (filter.getSellerId() != null && !filter.getSellerId().isEmpty()) {
            query.setParameter("sellerId", filter.getSellerId());
        }
        
        // Status parameters
        query.setParameter("status", ProductStatus.ACTIVE);
        query.setParameter("verified", true);
    }

    /**
     * Build ProductPageResponse with pagination information
     */
    private ProductPageResponse buildPageResponse(List<ProductDTO> productDTOs, 
                                                  ProductFilter filter, 
                                                  Long totalElements) {
        ProductPageResponse response = new ProductPageResponse();
        response.setData(productDTOs);
        response.setCurrentPage(filter.getPage());
        response.setPageSize(filter.getSize());
        response.setTotalElements(totalElements);
        response.setTotalPages((int) Math.ceil((double) totalElements / filter.getSize()));
        return response;
    }

    /**
     * Convert Product entity to ProductDTO
     */
    private ProductDTO convertToDTO(Product product) {
        ProductDTO dto = new ProductDTO();
        dto.setProductId(product.getProductId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setOriginalPrice(product.getOriginalPrice());
        dto.setSalePrice(product.getSalePrice());
        dto.setQuantity(product.getQuantity());
        dto.setExpirationDate(product.getExpirationDate());
        dto.setManufactureDate(product.getManufactureDate());
        dto.setStatus(product.getStatus());
        
        // Map seller information if present
        if (product.getSeller() != null) {
            SellerDTO sellerDTO = new SellerDTO();
            sellerDTO.setShopName(product.getSeller().getShopName());
            sellerDTO.setRating(product.getSeller().getRating());
            dto.setSeller(sellerDTO);
        }
        
        return dto;
    }
}