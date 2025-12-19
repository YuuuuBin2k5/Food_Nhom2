import { useState, useMemo, useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../services/api';
import { showToast } from '../utils/toast';

export const useCheckout = (cartItems = []) => {
    const navigate = useNavigate();
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');
    const [fieldErrors, setFieldErrors] = useState({});
    const [successData, setSuccessData] = useState(null);

    const totals = useMemo(() => {
        const subtotal = cartItems.reduce(
            (sum, item) =>
                sum + (item.product?.salePrice || item.product?.originalPrice || 0) * item.quantity,
            0
        );
        const shipping = subtotal > 0 ? 30000 : 0;
        const total = subtotal + shipping;
        return { subtotal, shipping, total };
    }, [cartItems]);

    /**
     * Validate form data
     */
    const validateForm = useCallback((formData) => {
        const errors = {};

        // Validate recipient name
        if (!formData.recipientName || !formData.recipientName.trim()) {
            errors.recipientName = 'Vui lòng nhập tên người nhận';
        } else if (formData.recipientName.trim().length < 2) {
            errors.recipientName = 'Tên phải có ít nhất 2 ký tự';
        } else if (formData.recipientName.trim().length > 100) {
            errors.recipientName = 'Tên quá dài (tối đa 100 ký tự)';
        }

        // Validate phone number
        const phone = (formData.recipientPhone || '').replace(/[^0-9+]/g, '');
        const phoneRe = /^\+?[0-9]{9,14}$/;
        if (!phone) {
            errors.recipientPhone = 'Vui lòng nhập số điện thoại';
        } else if (!phoneRe.test(phone)) {
            errors.recipientPhone = 'Số điện thoại không hợp lệ (9-14 số)';
        }

        // Validate street address
        if (!formData.street || formData.street.trim().length < 6) {
            errors.street = 'Vui lòng nhập địa chỉ chi tiết (tối thiểu 6 ký tự)';
        } else if (formData.street.trim().length > 200) {
            errors.street = 'Địa chỉ quá dài (tối đa 200 ký tự)';
        }

        // Validate payment method
        if (!formData.paymentMethod || !['COD', 'BANKING'].includes(formData.paymentMethod)) {
            errors.paymentMethod = 'Vui lòng chọn phương thức thanh toán';
        }

        setFieldErrors(errors);
        return Object.keys(errors).length === 0;
    }, []);

    /**
     * Clear field error
     */
    const clearFieldError = useCallback((fieldName) => {
        setFieldErrors(prev => {
            const newErrors = { ...prev };
            delete newErrors[fieldName];
            return newErrors;
        });
    }, []);

    /**
     * Clear cart from localStorage
     */
    const clearCart = useCallback(() => {
        localStorage.removeItem('cart');
        window.dispatchEvent(new CustomEvent('cartUpdated', {
            detail: { cart: [], count: 0 }
        }));
    }, []);

    /**
     * Submit checkout
     */
    const submitCheckout = useCallback(async (formData) => {
        // Validate form
        if (!validateForm(formData)) {
            showToast.error('Vui lòng kiểm tra lại thông tin');
            return false;
        }

        // Check cart not empty
        if (!cartItems || cartItems.length === 0) {
            showToast.error('Giỏ hàng đang trống');
            return false;
        }

        // Validate all items have valid quantity
        const invalidItems = cartItems.filter(item => !item.quantity || item.quantity <= 0);
        if (invalidItems.length > 0) {
            showToast.error('Số lượng sản phẩm không hợp lệ');
            return false;
        }

        setLoading(true);
        setError('');
        setFieldErrors({});

        try {
            // Build full address string
            const addressParts = [
                formData.street,
                formData.ward,
                formData.district,
                formData.city,
            ].filter(Boolean);
            const fullAddress = addressParts.join(', ');

            if (!fullAddress || fullAddress.trim().length < 10) {
                throw new Error('Địa chỉ giao hàng không hợp lệ');
            }

            // Build checkout payload
            const checkoutPayload = {
                shippingAddress: fullAddress,
                paymentMethod: formData.paymentMethod,
                items: cartItems.map((item) => ({
                    productId: String(item.product.productId),
                    quantity: item.quantity,
                })),
            };

            const response = await api.post('/checkout', checkoutPayload);

            if (response.data && response.data.success) {
                showToast.success('Đặt hàng thành công!');
                setSuccessData({
                    orderId: response.data.orderId || 'MỚI',
                    total: totals.total,
                    paymentMethod: formData.paymentMethod
                });
                clearCart();
                return true;
            } else {
                throw new Error(response.data?.message || 'Đặt hàng thất bại');
            }
        } catch (err) {
            console.error('[Checkout] Error:', err);

            const serverData = err.response?.data;

            // Handle validation errors from backend
            if (serverData && serverData.errors) {
                const fe = {};
                Object.keys(serverData.errors).forEach((k) => {
                    fe[k] = Array.isArray(serverData.errors[k])
                        ? serverData.errors[k].join(' ')
                        : String(serverData.errors[k]);
                });
                setFieldErrors(fe);
                const errorMsg = serverData.message || 'Vui lòng kiểm tra thông tin';
                setError(errorMsg);
                showToast.error(errorMsg);
            } else {
                // Handle general errors
                const errorMsg = serverData?.message || err.message || 'Có lỗi xảy ra, vui lòng thử lại.';
                setError(errorMsg);
                showToast.error(errorMsg);
            }
            return false;
        } finally {
            setLoading(false);
        }
    }, [cartItems, totals.total, validateForm, clearCart]);

    /**
     * Reset success state
     */
    const resetSuccess = useCallback(() => {
        setSuccessData(null);
    }, []);

    return {
        loading,
        error,
        fieldErrors,
        successData,
        totals,
        submitCheckout,
        clearFieldError,
        resetSuccess
    };
};
