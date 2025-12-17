/**
 * Toast Notification Utility
 * Centralized toast notifications using react-toastify
 */

import { toast } from 'react-toastify';

export const showToast = {
    /**
     * Show success toast
     * @param {string} message - Success message
     */
    success: (message) => {
        toast.success(message, {
            position: "top-right",
            autoClose: 3000,
            hideProgressBar: false,
            closeOnClick: true,
            pauseOnHover: true,
            draggable: true,
        });
    },

    /**
     * Show error toast
     * @param {string} message - Error message
     */
    error: (message) => {
        toast.error(message, {
            position: "top-right",
            autoClose: 4000,
            hideProgressBar: false,
            closeOnClick: true,
            pauseOnHover: true,
            draggable: true,
        });
    },

    /**
     * Show info toast
     * @param {string} message - Info message
     */
    info: (message) => {
        toast.info(message, {
            position: "top-right",
            autoClose: 3000,
            hideProgressBar: false,
            closeOnClick: true,
            pauseOnHover: true,
            draggable: true,
        });
    },

    /**
     * Show warning toast
     * @param {string} message - Warning message
     */
    warning: (message) => {
        toast.warning(message, {
            position: "top-right",
            autoClose: 3500,
            hideProgressBar: false,
            closeOnClick: true,
            pauseOnHover: true,
            draggable: true,
        });
    },

    /**
     * Show toast with custom options
     * @param {string} message - Message
     * @param {object} options - Custom options
     */
    custom: (message, options = {}) => {
        toast(message, {
            position: "top-right",
            autoClose: 3000,
            hideProgressBar: false,
            closeOnClick: true,
            pauseOnHover: true,
            draggable: true,
            ...options
        });
    },

    /**
     * Show promise toast (loading -> success/error)
     * @param {Promise} promise - Promise to track
     * @param {object} messages - Messages for pending, success, error states
     */
    promise: (promise, messages) => {
        return toast.promise(
            promise,
            {
                pending: messages.pending || 'Đang xử lý...',
                success: messages.success || 'Thành công!',
                error: messages.error || 'Có lỗi xảy ra!'
            },
            {
                position: "top-right",
            }
        );
    }
};
