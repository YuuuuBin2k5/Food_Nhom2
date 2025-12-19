import { useState, useCallback } from 'react';
import api from '../services/api';
import { showToast } from '../utils/toast';

export const useProducts = () => {
    const [products, setProducts] = useState([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);

    const fetchProducts = useCallback(async () => {
        setLoading(true);
        setError(null);
        
        try {
            const res = await api.get('/seller/products');
            const productsData = res.data || [];
            setProducts(productsData);
            return productsData;
        } catch (err) {
            const errorMessage = err.response?.data?.message || 'Không thể tải danh sách sản phẩm';
            setError(errorMessage);
            showToast.error(errorMessage);
            return [];
        } finally {
            setLoading(false);
        }
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
                await fetchProducts();
                return true;
            }
            return false;
        } catch (err) {
            const errorMessage = err.response?.data?.message || 'Không thể thêm sản phẩm';
            showToast.error(errorMessage);
            return false;
        }
    }, [fetchProducts]);

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
                await fetchProducts();
                return true;
            }
            return false;
        } catch (err) {
            const errorMessage = err.response?.data?.message || 'Không thể cập nhật sản phẩm';
            showToast.error(errorMessage);
            return false;
        }
    }, [fetchProducts]);

    /**
     * Delete product
     */
    const deleteProduct = useCallback(async (productId) => {
        if (!window.confirm('Xóa vĩnh viễn sản phẩm này?')) return false;
        
        try {
            await api.delete(`/seller/products/${productId}`);
            showToast.success('Đã xóa sản phẩm');
            await fetchProducts();
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
            await fetchProducts();
            return true;
        } catch (err) {
            showToast.error('Không thể thay đổi trạng thái');
            return false;
        }
    }, [fetchProducts]);

    /**
     * Sanitize product data before sending to API
     */
    const sanitizeProductData = (data) => {
        return {
            ...data,
            originalPrice: data.originalPrice === '' ? 0 : Number(data.originalPrice),
            salePrice: data.salePrice === '' ? 0 : Number(data.salePrice),
            quantity: data.quantity === '' ? 0 : parseInt(data.quantity, 10)
        };
    };

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
