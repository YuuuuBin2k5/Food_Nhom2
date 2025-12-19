export const PRODUCT_CATEGORIES = [
    { id: 'all', label: 'Tất cả', value: '' },
    { id: 'com', label: 'Cơm', value: 'Cơm' },
    { id: 'banh-mi', label: 'Bánh mì', value: 'Bánh mì' },
    { id: 'do-uong', label: 'Đồ uống', value: 'Đồ uống' },
    { id: 'trai-cay', label: 'Trái cây', value: 'Trái cây' },
    { id: 'rau-cu', label: 'Rau củ', value: 'Rau củ' },
    { id: 'thit', label: 'Thịt', value: 'Thịt' },
    { id: 'trang-mieng', label: 'Tráng miệng', value: 'Tráng miệng' },
    { id: 'khac', label: 'Khác', value: 'Khác' }
];

export const ORDER_STATUS = {
    PENDING: 'PENDING',
    CONFIRMED: 'CONFIRMED',
    SHIPPING: 'SHIPPING',
    DELIVERED: 'DELIVERED',
    CANCELLED: 'CANCELLED'
};

export const ORDER_STATUS_LABELS = {
    PENDING: 'Chờ xác nhận',
    CONFIRMED: 'Đã xác nhận',
    SHIPPING: 'Đang giao',
    DELIVERED: 'Đã giao',
    CANCELLED: 'Đã hủy'
};

export const ORDER_STATUS_BADGE_CONFIG = {
    PENDING: {
        label: 'Chờ xác nhận',
        className: 'bg-yellow-100 text-yellow-700 border-yellow-200',
        icon: '⏳'
    },
    CONFIRMED: {
        label: 'Đã xác nhận',
        className: 'bg-blue-100 text-blue-700 border-blue-200',
        icon: '👨‍🍳'
    },
    SHIPPING: {
        label: 'Đang giao',
        className: 'bg-purple-100 text-purple-700 border-purple-200',
        icon: '🚚'
    },
    DELIVERED: {
        label: 'Giao thành công',
        className: 'bg-green-100 text-green-700 border-green-200',
        icon: '✅'
    },
    CANCELLED: {
        label: 'Đã hủy',
        className: 'bg-red-100 text-red-700 border-red-200',
        icon: '❌'
    }
};

export const PAYMENT_METHODS = {
    COD: 'COD',
    BANKING: 'BANKING'
};

export const SORT_OPTIONS = [
    { value: 'newest', label: '🆕 Mới nhất' },
    { value: 'price_asc', label: '💰 Giá thấp → cao' },
    { value: 'price_desc', label: '💎 Giá cao → thấp' },
    { value: 'discount_desc', label: '🔥 Giảm giá nhiều' }
];

export const PLACEHOLDER_COLORS = [
    '2ECC71', '3498DB', 'E74C3C', 'F39C12', '9B59B6', '1ABC9C'
];

export const DEFAULT_SHIPPING_FEE = 30000;
export const CART_UPDATED_EVENT = 'cartUpdated';

export const NOTIFICATION_TYPES = {
    ORDER_CONFIRMED: 'ORDER_CONFIRMED',
    ORDER_SHIPPING: 'ORDER_SHIPPING',
    ORDER_DELIVERED: 'ORDER_DELIVERED',
    ORDER_CANCELLED: 'ORDER_CANCELLED',
    NEW_ORDER: 'NEW_ORDER',
    PRODUCT_APPROVED: 'PRODUCT_APPROVED',
    PRODUCT_REJECTED: 'PRODUCT_REJECTED',
    SELLER_APPROVED: 'SELLER_APPROVED',
    SELLER_REJECTED: 'SELLER_REJECTED',
    NEW_DELIVERY: 'NEW_DELIVERY',
    NEW_SELLER_REGISTRATION: 'NEW_SELLER_REGISTRATION',
    NEW_PRODUCT_PENDING: 'NEW_PRODUCT_PENDING'
};

export const NOTIFICATION_CONFIG = {
    ORDER_CONFIRMED: {
        icon: '✅',
        color: 'text-green-600',
        bgColor: 'bg-green-50'
    },
    ORDER_SHIPPING: {
        icon: '🚚',
        color: 'text-purple-600',
        bgColor: 'bg-purple-50'
    },
    ORDER_DELIVERED: {
        icon: '📦',
        color: 'text-blue-600',
        bgColor: 'bg-blue-50'
    },
    ORDER_CANCELLED: {
        icon: '❌',
        color: 'text-red-600',
        bgColor: 'bg-red-50'
    },
    NEW_ORDER: {
        icon: '🔔',
        color: 'text-orange-600',
        bgColor: 'bg-orange-50'
    },
    PRODUCT_APPROVED: {
        icon: '✅',
        color: 'text-green-600',
        bgColor: 'bg-green-50'
    },
    PRODUCT_REJECTED: {
        icon: '❌',
        color: 'text-red-600',
        bgColor: 'bg-red-50'
    },
    SELLER_APPROVED: {
        icon: '🎉',
        color: 'text-green-600',
        bgColor: 'bg-green-50'
    },
    SELLER_REJECTED: {
        icon: '❌',
        color: 'text-red-600',
        bgColor: 'bg-red-50'
    },
    NEW_DELIVERY: {
        icon: '🚚',
        color: 'text-blue-600',
        bgColor: 'bg-blue-50'
    },
    NEW_SELLER_REGISTRATION: {
        icon: '👤',
        color: 'text-purple-600',
        bgColor: 'bg-purple-50'
    },
    NEW_PRODUCT_PENDING: {
        icon: '📦',
        color: 'text-yellow-600',
        bgColor: 'bg-yellow-50'
    }
};
