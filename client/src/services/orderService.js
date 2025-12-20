import api from './api';

const orderService = {
    /**
     * @returns {Promise<Array>} Orders array
     */
    getBuyerOrders: async () => {
        const response = await api.get('/buyer/orders', {
            headers: { 'Accept': 'application/json' }
        });
        return response.data || [];
    },

    /**
     * @param {number} orderId
     * @returns {Promise<Object>}
     */
    cancelOrder: async (orderId) => {
        const response = await api.put(`/buyer/orders/${orderId}/cancel`);
        return response.data;
    },

    /**
     * @param {Object} orderData
     * @returns {Promise<Object>}
     */
    createOrder: async (orderData) => {
        const response = await api.post('/checkout', orderData);
        return response.data;
    },

    /**
     * @returns {Promise<Array>} Orders array
     */
    getSellerOrders: async (signal) => {
        const response = await api.get('/seller/orders', {
            headers: { 'Accept': 'application/json' },
            signal
        });
        return response.data || [];
    },

    /**
     * Update seller order status
     * @param {number} orderId - Order ID
     * @param {string} status - New status (CONFIRMED, CANCELLED)
     * @returns {Promise<Object>} API response
     */
    updateSellerOrderStatus: async (orderId, status) => {
        const response = await api.put(`/seller/orders/${orderId}/status`, { status });
        return response.data;
    },

    // ========== SHIPPER ENDPOINTS ==========
    
    /**
     * Get shipper orders
     * @returns {Promise<Array>} Orders array
     */
    getShipperOrders: async (signal) => {
        const response = await api.get('/shipper/orders', {
            signal
        });
        return response.data || [];
    },

    /**
     * Update shipper order status
     * @param {number} orderId - Order ID
     * @param {string} status - New status (SHIPPING, DELIVERED)
     * @returns {Promise<Object>} API response
     */
    updateShipperOrderStatus: async (orderId, status) => {
        const response = await api.put(`/shipper/orders/${orderId}/status`, { status });
        return response.data;
    }
};

export default orderService;
