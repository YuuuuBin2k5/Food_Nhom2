import { useState, useCallback, useRef } from 'react';
import api from '../services/api';
import { showToast } from '../utils/toast';

export const useProducts = () => {
    const [products, setProducts] = useState([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);
    const abortControllerRef = useRef(null);
    const cacheRef = useRef({ data: null, timestamp: 0 });
    const fetchingRef = useRef(false); // Prevent duplicate calls
    const CACHE_DURATION = 5 * 60 * 1000; // 5 phút cache

    const fetchProducts = useCallback(async (forceRefresh = false) => {
        // Prevent duplicate calls (React StrictMode issue)
        if (fetchingRef.current && !forceRefresh) {
            console.log('[useProducts] Already fetching, skipping...');
            return products;
        }

        // Kiểm tra cache
        const now = Date.now();
        if (!forceRefresh && cacheRef.current.data && (now - cacheRef.current.timestamp) < CACHE_DURATION) {
            console.log('[useProducts] Using cached data');
            setProducts(cacheRef.current.data);
            setLoading(false);
            return cacheRef.current.data;
        }

        // Hủy request cũ nếu đang pending
        if (abortControllerRef.current) {
            abortControllerRef.current.abort();
        }

        abortControllerRef.current = new AbortController();
        fetchingRef.current = true;
        setLoading(true);
        setError(null);
        
        try {
            console.log('[useProducts] Fetching...');
            const startTime = performance.now();
            
            const res = await api.get('/seller/products', {
                signal: abortControllerRef.current.signal
            });
            const productsData = res.data || [];
            
            // Cập nhật cache
            cacheRef.current = {
                data: productsData,
                timestamp: Date.now()
            };
            
            setProducts(productsData);
            setLoading(false);
            fetchingRef.current = false;
            
            const loadTime = performance.now() - startTime;
            console.log(`[useProducts] ✅ ${productsData.length} products in ${loadTime.toFixed(0)}ms`);
            
            return productsData;
        } catch (err) {
            fetchingRef.current = false; // Reset flag on error!
            setLoading(false);
            
            if (err.name === 'AbortError' || err.code === 'ERR_CANCELED') {
                console.log('[useProducts] Aborted');
                return products;
            }
            
            const errorMessage = err.response?.data?.message || 'Không thể tải danh sách sản phẩm';
            setError(errorMessage);
            console.error('[useProducts] ❌', errorMessage);
            showToast.error(errorMessage);
            return [];
        }
    }, []);

    /**
     * Sanitize product data before sending to API
     */
    const sanitizeProductData = useCallback((data) => {
        return {
            ...data,
            originalPrice: data.originalPrice === '' ? 0 : Number(data.originalPrice),
            salePrice: data.salePrice === '' ? 0 : Number(data.salePrice),
            quantity: data.quantity === '' ? 0 : parseInt(data.quantity, 10)
        };
    }, []);

    /**
     * Create new product
     */
    const createProduct = useCallback(async (productData) => {
        try {
            const sanitized = sanitizeProductData(productData);
            const res = await api.post('/seller/products', sanitized);
            
            if (res.status === 200 || res.status === 201) {
                showToast.success('Thêm sản phẩm thành công!');
                // Clear cache và reload
                cacheRef.current = { data: null, timestamp: 0 };
                await fetchProducts(true);
                return true;
            }
            return false;
        } catch (err) {
            const errorMessage = err.response?.data?.message || 'Không thể thêm sản phẩm';
            showToast.error(errorMessage);
            return false;
        }
    }, [sanitizeProductData, fetchProducts]);

    /**
     * Update product
     */
    const updateProduct = useCallback(async (productData) => {
        try {
            const sanitized = sanitizeProductData(productData);
            const payload = { ...sanitized, productId: productData.productId };
            const res = await api.put('/seller/products', payload);
            
            if (res.status === 200) {
                showToast.success('Cập nhật sản phẩm thành công!');
                // Clear cache và reload
                cacheRef.current = { data: null, timestamp: 0 };
                await fetchProducts(true);
                return true;
            }
            return false;
        } catch (err) {
            const errorMessage = err.response?.data?.message || 'Không thể cập nhật sản phẩm';
            showToast.error(errorMessage);
            return false;
        }
    }, [sanitizeProductData, fetchProducts]);

    /**
     * Delete product
     */
    const deleteProduct = useCallback(async (productId) => {
        if (!window.confirm('Xóa vĩnh viễn sản phẩm này?')) return false;
        
        try {
            await api.delete(`/seller/products/${productId}`);
            showToast.success('Đã xóa sản phẩm');
            // Clear cache và reload
            cacheRef.current = { data: null, timestamp: 0 };
            await fetchProducts(true);
            return true;
        } catch (err) {
            showToast.error('Không thể xóa sản phẩm');
            return false;
        }
    }, [fetchProducts]);

    /**
     * Toggle product status (ACTIVE <-> HIDDEN)
     */
    const toggleProductStatus = useCallback(async (product) => {
        if (product.status === 'PENDING_APPROVAL' || product.status === 'REJECTED') {
            showToast.warning('Sản phẩm chưa được duyệt');
            return false;
        }
        
        const newStatus = product.status === 'ACTIVE' ? 'HIDDEN' : 'ACTIVE';
        
        try {
            const payload = { ...product, status: newStatus };
            await api.put('/seller/products', payload);
            showToast.success(`Đã ${newStatus === 'HIDDEN' ? 'ẩn' : 'hiện'} sản phẩm`);
            // Clear cache và reload
            cacheRef.current = { data: null, timestamp: 0 };
            await fetchProducts(true);
            return true;
        } catch (err) {
            showToast.error('Không thể thay đổi trạng thái');
            return false;
        }
    }, [fetchProducts]);

    return {
        products,
        loading,
        error,
        fetchProducts,
        createProduct,
        updateProduct,
        deleteProduct,
        toggleProductStatus
    };
};
