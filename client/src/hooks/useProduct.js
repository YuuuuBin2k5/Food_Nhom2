import { useState, useEffect } from 'react';
import * as productService from '../services/productService';

export const useProduct = (initialFilters = {}) => {
    const [products, setProducts] = useState([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);
    const [filters, setFilters] = useState({
        search: '',
        category: '',
        minPrice: 0,
        maxPrice: 1000000,
        sortBy: 'newest',
        sellerId: '',
        hasDiscount: false,
        inStock: false,
        page: 0,
        size: 12,
        ...initialFilters
    });
    const [pagination, setPagination] = useState({
        currentPage: 0,
        totalPages: 0,
        totalElements: 0
    });

    useEffect(() => {
        loadProducts();
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [
        filters.search,
        filters.category,
        filters.minPrice,
        filters.maxPrice,
        filters.sortBy,
        filters.sellerId,
        filters.hasDiscount,
        filters.inStock,
        filters.page
    ]);

    const loadProducts = async () => {
        setLoading(true);
        setError(null);

        try {
            const response = await productService.getProducts(filters);
            setProducts(response.data || []);
            setPagination({
                currentPage: response.currentPage || 0,
                totalPages: response.totalPages || 0,
                totalElements: response.totalElements || 0
            });
        } catch (err) {
            console.error('Error loading products:', err);
            setError(err.message || 'Không thể tải sản phẩm');
            setProducts([]);
        } finally {
            setLoading(false);
        }
    };

    const updateFilters = (newFilters) => {
        setFilters(prev => ({ ...prev, ...newFilters, page: 0 }));
    };

    const setPage = (page) => {
        setFilters(prev => ({ ...prev, page }));
    };

    const resetFilters = () => {
        setFilters({
            search: '',
            category: '',
            minPrice: 0,
            maxPrice: 1000000,
            sortBy: 'newest',
            sellerId: '',
            hasDiscount: false,
            inStock: false,
            page: 0,
            size: 12
        });
    };

    return {
        products,
        loading,
        error,
        filters,
        pagination,
        updateFilters,
        setPage,
        resetFilters,
        reload: loadProducts
    };
};
