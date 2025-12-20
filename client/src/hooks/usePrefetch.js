import { useEffect } from 'react';
import { getProducts } from '../services/productService';
import { getCategories } from '../services/categoryService';

/**
 * Hook to prefetch data in the background
 */
export const usePrefetch = () => {
    useEffect(() => {
        // Prefetch categories on mount (they're cached for 30 min)
        const prefetchCategories = async () => {
            try {
                await getCategories();
            } catch (error) {
                console.warn('Prefetch categories failed:', error);
            }
        };

        // Prefetch first page of products
        const prefetchProducts = async () => {
            try {
                await getProducts({ page: 0, size: 12, sortBy: 'newest' });
            } catch (error) {
                console.warn('Prefetch products failed:', error);
            }
        };

        // Run prefetch after a short delay to not block initial render
        const timer = setTimeout(() => {
            prefetchCategories();
            prefetchProducts();
        }, 100);

        return () => clearTimeout(timer);
    }, []);
};

/**
 * Prefetch next page of products
 */
export const prefetchNextPage = async (currentFilters, currentPage) => {
    try {
        const nextPage = currentPage + 1;
        await getProducts({ ...currentFilters, page: nextPage });
        console.log(`[Prefetch] Loaded page ${nextPage} in background`);
    } catch (error) {
        console.warn('Prefetch next page failed:', error);
    }
};
