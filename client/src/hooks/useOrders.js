import { useState, useCallback } from 'react';
import orderService from '../services/orderService';
import { showToast } from '../utils/toast';

export const useOrders = (role = 'buyer') => {
    const [orders, setOrders] = useState([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);

    const fetchOrders = useCallback(async () => {
        setLoading(true);
        setError(null);
        
        try {
            let data;
            
            switch (role) {
                case 'seller':
                    data = await orderService.getSellerOrders();
                    break;
                case 'shipper':
                    data = await orderService.getShipperOrders();
                    break;
                case 'buyer':
                default:
                    data = await orderService.getBuyerOrders();
                    break;
            }

            // Sort by date (newest first)
            const sortedOrders = (data || []).sort(
                (a, b) => new Date(b.orderDate) - new Date(a.orderDate)
            );
            
            setOrders(sortedOrders);
            return sortedOrders;
        } catch (err) {
            const errorMessage = err.response?.data?.message || 'Không thể tải danh sách đơn hàng';
            setError(errorMessage);
            showToast.error(errorMessage);
            return [];
        } finally {
            setLoading(false);
        }
    }, [role]);

    /**
     * Cancel order (buyer only)
     */
    const cancelOrder = useCallback(async (orderId) => {
        try {
            await orderService.cancelOrder(orderId);
            showToast.success('Đã hủy đơn hàng thành công');
            await fetchOrders(); // Refresh list
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
            await fetchOrders(); // Refresh list
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
