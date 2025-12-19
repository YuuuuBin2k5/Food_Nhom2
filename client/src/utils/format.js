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

/**
 * Calculate discount percentage
 * @param {number} originalPrice 
 * @param {number} salePrice 
 * @returns {number} Discount percentage
 */
export const calculateDiscount = (originalPrice, salePrice) => {
    if (originalPrice && salePrice && originalPrice > salePrice) {
        return Math.round(((originalPrice - salePrice) / originalPrice) * 100);
    }
    return 0;
};

/**
 * Get expiry status with color
 * @param {string|Date} expirationDate 
 * @returns {Object|null} Status object with text and color
 */
export const getExpiryStatus = (expirationDate) => {
    if (!expirationDate) return null;
    const days = Math.ceil((new Date(expirationDate) - new Date()) / (1000 * 60 * 60 * 24));
    if (days <= 1) return { text: 'Hết hạn hôm nay', color: 'bg-red-100 text-red-700' };
    if (days <= 3) return { text: `Còn ${days} ngày`, color: 'bg-orange-100 text-orange-700' };
    if (days <= 7) return { text: `Còn ${days} ngày`, color: 'bg-amber-100 text-amber-700' };
    return { text: `HSD: ${new Date(expirationDate).toLocaleDateString('vi-VN')}`, color: 'bg-gray-100 text-gray-600' };
};

/**
 * Format date to short Vietnamese format
 * @param {string|Date} date 
 * @returns {string} Formatted date (e.g., "20/12/2024")
 */
export const formatDateShort = (date) => {
    if (!date) return '';
    return new Date(date).toLocaleDateString('vi-VN');
};

/**
 * Format date to long Vietnamese format
 * @param {string|Date} date 
 * @returns {string} Formatted date (e.g., "20 tháng 12, 2024")
 */
export const formatDateLong = (date) => {
    if (!date) return '';
    return new Date(date).toLocaleDateString('vi-VN', {
        day: 'numeric',
        month: 'long',
        year: 'numeric'
    });
};

/**
 * Format date with time
 * @param {string|Date} date 
 * @returns {string} Formatted date with time (e.g., "20/12/2024 14:30")
 */
export const formatDateTime = (date) => {
    if (!date) return '';
    const d = new Date(date);
    return `${d.toLocaleDateString('vi-VN')} ${d.toLocaleTimeString('vi-VN', { hour: '2-digit', minute: '2-digit' })}`;
};
