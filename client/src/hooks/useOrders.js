import { useState, useCallback, useRef } from 'react';
import orderService from '../services/orderService';
import { showToast } from '../utils/toast';

export const useOrders = (role = 'buyer') => {
    const [orders, setOrders] = useState([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);
    const abortControllerRef = useRef(null);
    const cacheRef = useRef({ data: null, timestamp: 0 });
    const fetchingRef = useRef(false); // Prevent duplicate calls
    const CACHE_DURATION = 2 * 60 * 1000; // 2 phút cache

    const fetchOrders = useCallback(async (forceRefresh = false) => {
        // Prevent duplicate calls (React StrictMode issue)
        if (fetchingRef.current && !forceRefresh) {
            console.log('[useOrders] Already fetching, skipping...');
            return orders;
        }

        // Kiểm tra cache
        const now = Date.now();
        if (!forceRefresh && cacheRef.current.data && (now - cacheRef.current.timestamp) < CACHE_DURATION) {
            console.log('[useOrders] Using cached data');
            setOrders(cacheRef.current.data);
            setLoading(false);
            return cacheRef.current.data;
        }

        // Hủy request cũ
        if (abortControllerRef.current) {
            abortControllerRef.current.abort();
        }

        abortControllerRef.current = new AbortController();
        fetchingRef.current = true;
        setLoading(true);
        setError(null);
        
        try {
            console.log(`[useOrders] Fetching ${role} orders...`);
            const startTime = performance.now();
            
            let data;
            
            switch (role) {
                case 'seller':
                    data = await orderService.getSellerOrders(abortControllerRef.current.signal);
                    break;
                case 'shipper':
                    data = await orderService.getShipperOrders(abortControllerRef.current.signal);
                    break;
                case 'buyer':
                default:
                    data = await orderService.getBuyerOrders(abortControllerRef.current.signal);
                    break;
            }

            // Sort by date (newest first)
            const sortedOrders = (data || []).sort(
                (a, b) => new Date(b.orderDate) - new Date(a.orderDate)
            );
            
            // Cập nhật cache
            cacheRef.current = {
                data: sortedOrders,
                timestamp: Date.now()
            };
            
            setOrders(sortedOrders);
            setLoading(false);
            fetchingRef.current = false;
            
            const loadTime = performance.now() - startTime;
            console.log(`[useOrders] ✅ Loaded ${sortedOrders.length} orders in ${loadTime.toFixed(0)}ms`);
            
            return sortedOrders;
        } catch (err) {
            fetchingRef.current = false; // Reset flag on error!
            setLoading(false);
            
            if (err.name === 'AbortError' || err.code === 'ERR_CANCELED') {
                console.log('[useOrders] Request aborted');
                return orders;
            }
            
            const errorMessage = err.response?.data?.message || 'Không thể tải danh sách đơn hàng';
            setError(errorMessage);
            console.error('[useOrders] ❌ Error:', errorMessage);
            showToast.error(errorMessage);
            return [];
        }
    }, [role, orders]);

    /**
     * Cancel order (buyer only)
     */
    const cancelOrder = useCallback(async (orderId) => {
        try {
            await orderService.cancelOrder(orderId);
            showToast.success('Đã hủy đơn hàng thành công');
            // Clear cache và refresh
            cacheRef.current = { data: null, timestamp: 0 };
            await fetchOrders(true);
            return true;
        } catch (err) {
            const errorMessage = err.response?.data?.message || 'Lỗi khi hủy đơn hàng';
            showToast.error(errorMessage);
            return false;
        }
    }, [fetchOrders]);

    /**
     * Update order status (seller/shipper)
     */
    const updateOrderStatus = useCallback(async (orderId, status) => {
        try {
            if (role === 'seller') {
                await orderService.updateSellerOrderStatus(orderId, status);
            } else if (role === 'shipper') {
                await orderService.updateShipperOrderStatus(orderId, status);
            }
            
            showToast.success('Đã cập nhật trạng thái đơn hàng');
            // Clear cache và refresh
            cacheRef.current = { data: null, timestamp: 0 };
            await fetchOrders(true);
            return true;
        } catch (err) {
            const errorMessage = err.response?.data?.message || 'Lỗi khi cập nhật trạng thái';
            showToast.error(errorMessage);
            return false;
        }
    }, [role, fetchOrders]);

    return {
        orders,
        loading,
        error,
        fetchOrders,
        cancelOrder,
        updateOrderStatus
    };
};
