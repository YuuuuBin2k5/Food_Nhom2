package com.ecommerce.service;

import com.ecommerce.dto.ProductDTO;
import com.ecommerce.dto.ProductFilter;
import com.ecommerce.dto.ProductPageResponse;
import com.ecommerce.dto.SellerDTO;
import com.ecommerce.entity.Product;
import com.ecommerce.entity.ProductStatus;
import com.ecommerce.util.DBUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Product Service - Handles product-related business logic
 * Uses JPQL (Query Method 2) for dynamic query building
 */
public class ProductService {
    
    private static final String BASE_JPQL = "SELECT p FROM Product p LEFT JOIN FETCH p.seller WHERE 1=1";
    private static final String BASE_COUNT_JPQL = "SELECT COUNT(p) FROM Product p WHERE 1=1";
    private static final int DEFAULT_PAGE_SIZE = 12;

    /**
     * Get paginated list of products with filters
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
     * Get product by ID
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
        dto.setStatus(product.getStatus().toString());
        
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