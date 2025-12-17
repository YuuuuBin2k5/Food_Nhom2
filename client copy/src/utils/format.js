/**
 * Format number to Vietnamese currency style
 * @param {number} price 
 * @returns {string} Formatted string (e.g., "100.000 ₫")
 */
export const formatPrice = (price) => {
    return new Intl.NumberFormat('vi-VN', {
        style: 'currency',
        currency: 'VND'
    }).format(price);
};
