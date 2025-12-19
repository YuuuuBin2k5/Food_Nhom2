/**
 * @param {number} price 
 * @returns {string}
 */
export const formatPrice = (price) => {
    return new Intl.NumberFormat('vi-VN', {
        style: 'currency',
        currency: 'VND'
    }).format(price);
};

/**
 * @param {string|Date} dateString 
 * @returns {string}
 */
export const formatDate = (dateString) => {
    if (!dateString) return '';
    return new Date(dateString).toLocaleString('vi-VN', {
        day: '2-digit',
        month: '2-digit',
        year: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
    });
};

/**
 * @param {string|Date} dateString 
 * @returns {string}
 */
export const formatDateShort = (dateString) => {
    if (!dateString) return '';
    return new Date(dateString).toLocaleDateString('vi-VN');
};

/**
 * @param {number} originalPrice 
 * @param {number} salePrice 
 * @returns {number}
 */
export const calculateDiscount = (originalPrice, salePrice) => {
    if (!originalPrice || !salePrice || originalPrice <= salePrice) {
        return 0;
    }
    return Math.round(((originalPrice - salePrice) / originalPrice) * 100);
};

/**
 * @param {string|Date} expirationDate 
 * @returns {object|null}
 */
export const getExpiryStatus = (expirationDate) => {
    if (!expirationDate) return null;
    
    const days = Math.ceil((new Date(expirationDate) - new Date()) / (1000 * 60 * 60 * 24));
    
    if (days <= 1) {
        return { text: 'Hết hạn hôm nay', color: 'bg-red-100 text-red-700' };
    }
    if (days <= 3) {
        return { text: `Còn ${days} ngày`, color: 'bg-orange-100 text-orange-700' };
    }
    if (days <= 7) {
        return { text: `Còn ${days} ngày`, color: 'bg-amber-100 text-amber-700' };
    }
    
    return {
        text: `HSD: ${formatDateShort(expirationDate)}`,
        color: 'bg-gray-100 text-gray-600'
    };
};
