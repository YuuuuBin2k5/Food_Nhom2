import api from './api';
import { apiCache } from '../utils/apiCache';

/**
 * Get all product categories (with caching)
 * @returns {Promise} Categories array
 */
export const getCategories = async () => {
    const url = '/categories';
    
    // Check cache first - categories rarely change
    const cached = apiCache.get(url);
    if (cached) {
        console.log('[CategoryService] Using cached categories');
        return cached;
    }
    
    const response = await api.get(url);
    
    // Cache categories for 30 minutes
    apiCache.set(url, {}, response.data, 30 * 60 * 1000);
    
    return response.data;
};
