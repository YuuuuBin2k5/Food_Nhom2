import axios from 'axios';

// Base URL for API calls
const API_BASE_URL = 'http://localhost:8080/server/api';

export const productService = {
    async getProducts(filters) {
        try {
            const params = new URLSearchParams();

            if (filters.search) params.append('search', filters.search);
            if (filters.minPrice) params.append('minPrice', filters.minPrice);
            if (filters.maxPrice) params.append('maxPrice', filters.maxPrice);
            if (filters.sortBy) params.append('sortBy', filters.sortBy);
            if (filters.page !== undefined) params.append('page', filters.page);
            if (filters.size) params.append('size', filters.size);

            const response = await axios.get(`${API_BASE_URL}/products?${params}`);
            return response.data;
        } catch (error) {
            console.error('Error fetching products:', error);
            throw error;
        }
    },

    async getProductById(id) {
        try {
            const response = await axios.get(`${API_BASE_URL}/products/${id}`);
            return response.data;
        } catch (error) {
            console.error('Error fetching product:', error);
            throw error;
        }
    }
};
