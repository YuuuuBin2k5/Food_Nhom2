import api from './api';

/**
 * Get all products with filters
 * @param {Object} params - Query parameters
 * @returns {Promise} API response
 */
export const getProducts = async (params = {}) => {
    const queryParams = new URLSearchParams();
    
    if (params.search) queryParams.append('search', params.search);
    if (params.category) queryParams.append('category', params.category);
    if (params.minPrice) queryParams.append('minPrice', params.minPrice);
    if (params.maxPrice) queryParams.append('maxPrice', params.maxPrice);
    if (params.sortBy) queryParams.append('sortBy', params.sortBy);
    if (params.sellerId) queryParams.append('sellerId', params.sellerId);
    if (params.hasDiscount) queryParams.append('hasDiscount', params.hasDiscount);
    if (params.inStock) queryParams.append('inStock', params.inStock);
    
    queryParams.append('page', params.page || 0);
    queryParams.append('size', params.size || 12);

    const response = await api.get(`/products?${queryParams}`);
    return response.data;
};

/**
 * Get product by ID
 * @param {number} productId - Product ID
 * @returns {Promise} Product data
 */
export const getProductById = async (productId) => {
    const response = await api.get(`/products/${productId}`);
    return response.data;
};

/**
 * Get recommended products
 * @param {number} currentProductId - Current product ID
 * @param {number} limit - Number of products to return
 * @returns {Promise} Products array
 */
export const getRecommendedProducts = async (currentProductId, limit = 4) => {
    // TODO: Implement recommendation API
    // For now, get random products
    const response = await api.get(`/products?page=0&size=${limit}&sortBy=newest`);
    const allProducts = response.data?.data || [];
    return allProducts.filter(p => p.productId !== parseInt(currentProductId));
};
