package com.ecommerce.service;

import java.util.List;
import java.util.stream.Collectors;

import com.ecommerce.dto.ProductDTO;
import com.ecommerce.dto.ProductFilter;
import com.ecommerce.dto.ProductPageResponse;
import com.ecommerce.dto.SellerDTO;
import com.ecommerce.entity.Product;
import com.ecommerce.entity.ProductStatus;
import com.ecommerce.entity.ProductCategory;
import com.ecommerce.entity.Seller;
import com.ecommerce.util.DBUtil;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;

public class ProductService {

    private static final String BASE_JPQL = "SELECT p FROM Product p LEFT JOIN FETCH p.seller WHERE 1=1";
    private static final String BASE_COUNT_JPQL = "SELECT COUNT(p) FROM Product p WHERE 1=1";

    // 1. Thêm Product mới
    public void addProduct(String sellerId, ProductDTO dto) throws Exception {
        System.out.println("[ProductService] Starting addProduct for seller: " + sellerId);
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        EntityTransaction trans = em.getTransaction();
        try {
            trans.begin();
            System.out.println("[ProductService] Transaction started");

            // 1. Tìm người bán
            Seller seller = em.find(Seller.class, sellerId);
            if (seller == null) {
                System.out.println("[ProductService] ERROR: Seller not found: " + sellerId);
                throw new Exception("Không tìm thấy thông tin người bán");
            }
            System.out.println("[ProductService] Found seller: " + seller.getShopName());

            // 2. Tạo đối tượng Product từ DTO
            Product product = new Product();
            product.setName(dto.getName());
            product.setDescription(dto.getDescription());
            product.setOriginalPrice(dto.getOriginalPrice());
            product.setSalePrice(dto.getSalePrice());
            product.setQuantity(dto.getQuantity());
            product.setImageUrl(dto.getImageUrl());
            product.setExpirationDate(dto.getExpirationDate());

            // Set category từ DTO, mặc định OTHER nếu null
            if (dto.getCategory() != null) {
                product.setCategory(dto.getCategory());
            } else {
                product.setCategory(ProductCategory.OTHER);
            }

            // 3. Gán Seller cho Product
            product.setSeller(seller);

            // 4. QUAN TRỌNG: Mặc định là Chờ duyệt & Ngày tạo hiện tại
            product.setStatus(ProductStatus.PENDING_APPROVAL);
            product.setCreatedDate(new java.util.Date()); // Hoặc LocalDate.now() tùy entity của bạn

            System.out.println("[ProductService] Product created with status: " + product.getStatus());

            // 5. Lưu xuống DB
            em.persist(product);
            System.out.println("[ProductService] Product persisted");
            trans.commit(); // <-- Nếu thiếu dòng này, dữ liệu sẽ không được lưu!
            System.out.println("[ProductService] Transaction committed");

            System.out.println(">>> Đã lưu sản phẩm thành công: " + product.getName());

        } catch (Exception e) {
            if (trans.isActive())
                trans.rollback();
            System.out.println("[ProductService] ERROR: " + e.getMessage());
            e.printStackTrace(); // In lỗi ra console để debug
            throw e;
        } finally {
            em.close();
            System.out.println("[ProductService] EntityManager closed");
        }
    }

    // 2. Cập nhật Product
    public void updateProduct(String sellerId, ProductDTO dto) throws Exception {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        EntityTransaction trans = em.getTransaction();
        try {
            trans.begin();
            Product product = em.find(Product.class, dto.getProductId());

            if (product == null)
                throw new Exception("Sản phẩm không tồn tại");

            if (!product.getSeller().getUserId().equals(sellerId)) {
                throw new Exception("Bạn không có quyền sửa sản phẩm này");
            }

            product.setName(dto.getName());
            product.setDescription(dto.getDescription());
            product.setOriginalPrice(dto.getOriginalPrice());
            product.setSalePrice(dto.getSalePrice());
            product.setQuantity(dto.getQuantity());
            product.setExpirationDate(dto.getExpirationDate());

            if (dto.getStatus() != null) {
                product.setStatus(dto.getStatus());
            }

            em.merge(product);
            trans.commit();
        } catch (Exception e) {
            if (trans.isActive())
                trans.rollback();
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
                em.remove(product);
            }
            trans.commit();
        } catch (Exception e) {
            if (trans.isActive())
                trans.rollback();
            throw new Exception("Không thể xóa sản phẩm (có thể do đã có đơn hàng). Hãy thử ẩn sản phẩm.");
        } finally {
            em.close();
        }
    }

    // 4. Lấy danh sách Product của Seller
    public List<Product> getProductsBySeller(String sellerId) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            // Tự động cập nhật sản phẩm hết hạn trước khi lấy danh sách
            updateExpiredProductsInTransaction(em);

            TypedQuery<Product> query = em.createQuery(
                    "SELECT p FROM Product p JOIN FETCH p.seller WHERE p.seller.userId = :sid", Product.class);
            query.setParameter("sid", sellerId);
            return query.getResultList();
        } catch (Exception e) {
            System.err.println("Error in getProductsBySeller: " + e.getMessage());
            e.printStackTrace();
            return List.of(); // Return empty list on error
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
            if (product == null)
                throw new Exception("Product not found");

            if (isApproved) {
                product.setStatus(ProductStatus.ACTIVE);
            } else {
                product.setStatus(ProductStatus.REJECTED);
            }
            em.merge(product);
            trans.commit();
        } finally {
            em.close();
        }
    }

    // 5. Lấy danh sách sản phẩm đang active
    public List<Product> getActiveProducts() {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            // Tự động cập nhật sản phẩm hết hạn trước khi lấy danh sách
            updateExpiredProductsInTransaction(em);

            TypedQuery<Product> query = em.createQuery(
                    "SELECT p FROM Product p LEFT JOIN FETCH p.seller WHERE p.status = :st",
                    Product.class);
            query.setParameter("st", ProductStatus.ACTIVE);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    // 6. Lấy chi tiết 1 product nếu đang active
    public Product getActiveProductById(Long productId) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            TypedQuery<Product> query = em.createQuery(
                    "SELECT p FROM Product p WHERE p.productId = :id AND p.status = :st",
                    Product.class);
            query.setParameter("id", productId);
            query.setParameter("st", ProductStatus.ACTIVE);
            return query.getResultStream().findFirst().orElse(null);
        } finally {
            em.close();
        }
    }

    /**
     * Get paginated list of products with filters (for Buyer)
     */
    public ProductPageResponse getProducts(ProductFilter filter) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();

        try {
            String whereClause = buildWhereClause(filter);
            String orderClause = buildOrderClause(filter);

            String jpql = BASE_JPQL + whereClause + orderClause;
            TypedQuery<Product> query = em.createQuery(jpql, Product.class);
            setQueryParameters(query, filter);

            String countJpql = BASE_COUNT_JPQL + whereClause;
            TypedQuery<Long> countQuery = em.createQuery(countJpql, Long.class);
            setQueryParameters(countQuery, filter);

            Long totalElements = countQuery.getSingleResult();

            query.setFirstResult(filter.getPage() * filter.getSize());
            query.setMaxResults(filter.getSize());

            List<Product> products = query.getResultList();

            List<ProductDTO> productDTOs = products.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());

            return buildPageResponse(productDTOs, filter, totalElements);

        } finally {
            em.close();
        }
    }

    /**
     * Get product by ID (for Buyer)
     */
    public ProductDTO getProductById(Long productId) throws Exception {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();

        try {
            Product product = em.find(Product.class, productId);

            if (product == null) {
                throw new Exception("Sản phẩm không tồn tại: " + productId);
            }

            if (product.getSeller() != null) {
                product.getSeller().getShopName();
            }

            return convertToDTO(product);

        } finally {
            em.close();
        }
    }

    private String buildWhereClause(ProductFilter filter) {
        StringBuilder where = new StringBuilder();

        if (filter.getSearch() != null && !filter.getSearch().isEmpty()) {
            where.append(" AND (LOWER(p.name) LIKE :search OR LOWER(p.description) LIKE :search)");
        }

        if (filter.getMinPrice() != null) {
            where.append(" AND p.salePrice >= :minPrice");
        }
        if (filter.getMaxPrice() != null) {
            where.append(" AND p.salePrice <= :maxPrice");
        }

        if (filter.getHasDiscount() != null && filter.getHasDiscount()) {
            where.append(" AND p.originalPrice > p.salePrice");
        }
        if (filter.getInStock() != null && filter.getInStock()) {
            where.append(" AND p.quantity > 0");
        }

        if (filter.getSellerId() != null && !filter.getSellerId().isEmpty()) {
            where.append(" AND p.seller.userId = :sellerId");
        }

        if (filter.getCategory() != null && !filter.getCategory().isEmpty()) {
            try {
                // Validate category exists before adding to query
                ProductCategory.valueOf(filter.getCategory().toUpperCase());
                where.append(" AND p.category = :category");
            } catch (IllegalArgumentException e) {
                // Invalid category, ignore filter
                System.out.println("[ProductService] Invalid category: " + filter.getCategory());
            }
        }

        where.append(" AND p.status = :status");

        return where.toString();
    }

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
            default:
                return " ORDER BY p.productId DESC";
        }
    }

    private <T> void setQueryParameters(TypedQuery<T> query, ProductFilter filter) {
        if (filter.getSearch() != null && !filter.getSearch().isEmpty()) {
            query.setParameter("search", "%" + filter.getSearch().toLowerCase() + "%");
        }

        if (filter.getMinPrice() != null) {
            query.setParameter("minPrice", filter.getMinPrice());
        }
        if (filter.getMaxPrice() != null) {
            query.setParameter("maxPrice", filter.getMaxPrice());
        }

        if (filter.getCategory() != null && !filter.getCategory().isEmpty()) {
            try {
                ProductCategory category = ProductCategory.valueOf(filter.getCategory().toUpperCase());
                query.setParameter("category", category);
                System.out.println("[ProductService] Filtering by category: " + category);
            } catch (IllegalArgumentException e) {
                // Invalid category value, skip parameter
                System.out.println("[ProductService] Invalid category value: " + filter.getCategory());
            }
        }

        if (filter.getSellerId() != null && !filter.getSellerId().isEmpty()) {
            query.setParameter("sellerId", filter.getSellerId());
        }

        query.setParameter("status", ProductStatus.ACTIVE);
    }

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
        dto.setCategory(product.getCategory());

        if (product.getSeller() != null) {
            SellerDTO sellerDTO = new SellerDTO();
            sellerDTO.setShopName(product.getSeller().getShopName());
            sellerDTO.setRating(product.getSeller().getRating());
            dto.setSeller(sellerDTO);
        }

        return dto;
    }

    /**
     * Get pending products (waiting for approval)
     */
    public List<Product> getPendingProducts() {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            TypedQuery<Product> query = em.createQuery(
                    "SELECT p FROM Product p LEFT JOIN FETCH p.seller WHERE p.status = :status ORDER BY p.productId ASC",
                    Product.class);
            query.setParameter("status", ProductStatus.PENDING_APPROVAL);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Search products by name or description
     */
    public List<Product> searchProducts(String searchTerm) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            // Tự động cập nhật sản phẩm hết hạn trước khi tìm kiếm
            updateExpiredProductsInTransaction(em);

            TypedQuery<Product> query = em.createQuery(
                    "SELECT p FROM Product p LEFT JOIN FETCH p.seller " +
                            "WHERE (LOWER(p.name) LIKE :search OR LOWER(p.description) LIKE :search) " +
                            "AND p.status = :st",
                    Product.class);
            query.setParameter("search", "%" + searchTerm.toLowerCase() + "%");
            query.setParameter("st", ProductStatus.ACTIVE);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Get products by category
     */
    public List<Product> getProductsByCategory(String categoryStr) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            // Tự động cập nhật sản phẩm hết hạn trước khi lấy danh sách
            updateExpiredProductsInTransaction(em);

            ProductCategory category = ProductCategory.valueOf(categoryStr.toUpperCase());
            TypedQuery<Product> query = em.createQuery(
                    "SELECT p FROM Product p LEFT JOIN FETCH p.seller " +
                            "WHERE p.category = :cat AND p.status = :st",
                    Product.class);
            query.setParameter("cat", category);
            query.setParameter("st", ProductStatus.ACTIVE);
            return query.getResultList();
        } catch (IllegalArgumentException e) {
            // Invalid category, return empty list
            return List.of();
        } finally {
            em.close();
        }
    }

    // Khi "Hiện lại" sản phẩm đang ẩn $\rightarrow$ chuyển về PENDING_APPROVAL
    public void toggleProductStatus(String sellerId, Long productId, String action) throws Exception {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        EntityTransaction trans = em.getTransaction();
        try {
            trans.begin();
            Product product = em.find(Product.class, productId);

            // Check quyền sở hữu
            if (product == null || !product.getSeller().getUserId().equals(sellerId)) {
                throw new Exception("Sản phẩm không tồn tại hoặc không thuộc quyền quản lý");
            }

            if ("HIDE".equals(action) && product.getStatus() == ProductStatus.ACTIVE) {
                // Đang hiện -> Ẩn (Bình thường)
                product.setStatus(ProductStatus.HIDDEN);
            } else if ("SHOW".equals(action) && product.getStatus() == ProductStatus.HIDDEN) {
                // Đang ẩn -> Hiện lại (Phải chờ Admin duyệt lại)
                product.setStatus(ProductStatus.PENDING_APPROVAL);
            }

            em.merge(product);
            trans.commit();
        } catch (Exception e) {
            if (trans.isActive())
                trans.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    /**
     * Kiểm tra và cập nhật trạng thái sản phẩm hết hạn trong cùng EntityManager
     * Method này được gọi tự động trong các method khác
     */
    private void updateExpiredProductsInTransaction(EntityManager em) {
        EntityTransaction trans = em.getTransaction();
        boolean shouldCommit = false;

        try {
            if (!trans.isActive()) {
                trans.begin();
                shouldCommit = true;
            }

            // Tìm tất cả sản phẩm ACTIVE hoặc SOLD_OUT có ngày hết hạn < ngày hiện tại
            TypedQuery<Product> query = em.createQuery(
                    "SELECT p FROM Product p WHERE (p.status = :active OR p.status = :soldOut) " +
                            "AND p.expirationDate < :currentDate",
                    Product.class);
            query.setParameter("active", ProductStatus.ACTIVE);
            query.setParameter("soldOut", ProductStatus.SOLD_OUT);
            query.setParameter("currentDate", new java.util.Date());

            List<Product> expiredProducts = query.getResultList();

            // Cập nhật trạng thái thành EXPIRED
            for (Product product : expiredProducts) {
                product.setStatus(ProductStatus.EXPIRED);
                em.merge(product);
            }

            if (shouldCommit && !expiredProducts.isEmpty()) {
                trans.commit();
                System.out.println("[ProductService] Auto-updated " + expiredProducts.size() + " expired products");
            }

        } catch (Exception e) {
            if (shouldCommit && trans.isActive()) {
                trans.rollback();
            }
            System.err.println("Error auto-updating expired products: " + e.getMessage());
        }
    }

    /**
     * Kiểm tra và cập nhật trạng thái sản phẩm hết hạn
     * Method này sẽ được gọi định kỳ hoặc khi load danh sách sản phẩm
     */
    public void updateExpiredProducts() {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        EntityTransaction trans = em.getTransaction();
        try {
            trans.begin();

            // Tìm tất cả sản phẩm ACTIVE hoặc SOLD_OUT có ngày hết hạn < ngày hiện tại
            TypedQuery<Product> query = em.createQuery(
                    "SELECT p FROM Product p WHERE (p.status = :active OR p.status = :soldOut) " +
                            "AND p.expirationDate < :currentDate",
                    Product.class);
            query.setParameter("active", ProductStatus.ACTIVE);
            query.setParameter("soldOut", ProductStatus.SOLD_OUT);
            query.setParameter("currentDate", new java.util.Date());

            List<Product> expiredProducts = query.getResultList();

            // Cập nhật trạng thái thành EXPIRED
            for (Product product : expiredProducts) {
                product.setStatus(ProductStatus.EXPIRED);
                em.merge(product);
                System.out.println("[ProductService] Updated product #" + product.getProductId() +
                        " (" + product.getName() + ") to EXPIRED status");
            }

            trans.commit();

            if (!expiredProducts.isEmpty()) {
                System.out.println("[ProductService] Updated " + expiredProducts.size() + " expired products");
            }

        } catch (Exception e) {
            if (trans.isActive()) {
                trans.rollback();
            }
            System.err.println("Error updating expired products: " + e.getMessage());
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    /**
     * Lấy danh sách sản phẩm sắp hết hạn (trong vòng 3 ngày) của seller
     */
    public List<Product> getExpiringSoonProducts(String sellerId) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            // Tính ngày hiện tại + 3 ngày
            java.util.Calendar cal = java.util.Calendar.getInstance();
            cal.add(java.util.Calendar.DAY_OF_MONTH, 3);
            java.util.Date threeDaysLater = cal.getTime();

            TypedQuery<Product> query = em.createQuery(
                    "SELECT p FROM Product p WHERE p.seller.userId = :sellerId " +
                            "AND (p.status = :active OR p.status = :soldOut) " +
                            "AND p.expirationDate BETWEEN :currentDate AND :threeDaysLater " +
                            "ORDER BY p.expirationDate ASC",
                    Product.class);
            query.setParameter("sellerId", sellerId);
            query.setParameter("active", ProductStatus.ACTIVE);
            query.setParameter("soldOut", ProductStatus.SOLD_OUT);
            query.setParameter("currentDate", new java.util.Date());
            query.setParameter("threeDaysLater", threeDaysLater);

            return query.getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Đếm số lượng sản phẩm sắp hết hạn của seller
     */
    public long countExpiringSoonProducts(String sellerId) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            // Tính ngày hiện tại + 3 ngày
            java.util.Calendar cal = java.util.Calendar.getInstance();
            cal.add(java.util.Calendar.DAY_OF_MONTH, 3);
            java.util.Date threeDaysLater = cal.getTime();

            TypedQuery<Long> query = em.createQuery(
                    "SELECT COUNT(p) FROM Product p WHERE p.seller.userId = :sellerId " +
                            "AND (p.status = :active OR p.status = :soldOut) " +
                            "AND p.expirationDate BETWEEN :currentDate AND :threeDaysLater",
                    Long.class);
            query.setParameter("sellerId", sellerId);
            query.setParameter("active", ProductStatus.ACTIVE);
            query.setParameter("soldOut", ProductStatus.SOLD_OUT);
            query.setParameter("currentDate", new java.util.Date());
            query.setParameter("threeDaysLater", threeDaysLater);

            return query.getSingleResult();
        } finally {
            em.close();
        }
    }
}