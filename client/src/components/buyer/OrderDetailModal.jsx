import { formatPrice } from '../../utils/format';
import OrderStatusBadge from '../common/OrderStatusBadge';

/**
 * Order Detail Modal Component
 */
const OrderDetailModal = ({ order, onClose, onCancel }) => {
    if (!order) return null;

    const formatDate = (dateStr) => {
        return new Date(dateStr).toLocaleString('vi-VN', {
            day: '2-digit',
            month: '2-digit',
            year: 'numeric',
            hour: '2-digit',
            minute: '2-digit'
        });
    };

    const handleCancel = () => {
        if (window.confirm('Bạn có chắc chắn muốn hủy đơn hàng này?')) {
            onCancel(order.orderId);
        }
    };

    // Get shop name from first item (all items in one order are from same seller)
    const shopName = order.items?.[0]?.shopName || 'Cửa hàng';

    return (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4">
            {/* Backdrop */}
            <div
                className="absolute inset-0 bg-black/60 backdrop-blur-sm"
                onClick={onClose}
            />

            {/* Modal Content */}
            <div className="relative bg-white rounded-2xl shadow-2xl w-full max-w-3xl max-h-[90vh] flex flex-col animate-in zoom-in-95 duration-200">
                {/* Header */}
                <div className="p-6 border-b border-gray-100 flex justify-between items-start bg-gradient-to-r from-orange-50 to-amber-50 rounded-t-2xl">
                    <div className="flex-1">
                        <h3 className="text-2xl font-bold text-[#0f172a]">
                            Chi tiết đơn hàng #{order.orderId}
                        </h3>
                        <p className="text-sm text-[#334155] mt-1">
                            📅 {formatDate(order.orderDate)}
                        </p>
                        <div className="mt-3 inline-flex items-center gap-2 px-4 py-2 bg-white rounded-full shadow-sm border border-orange-200">
                            <span className="text-lg">🏪</span>
                            <span className="font-semibold text-[#FF6B6B]">{shopName}</span>
                        </div>
                    </div>
                    <button
                        onClick={onClose}
                        className="w-10 h-10 flex items-center justify-center rounded-full bg-white hover:bg-gray-100 text-gray-500 transition shadow-sm"
                    >
                        ✕
                    </button>
                </div>

                {/* Body */}
                <div className="p-6 overflow-y-auto">
                    {/* Status */}
                    <div className="flex justify-between items-center mb-6 p-5 bg-gradient-to-r from-orange-50 to-amber-50 rounded-xl border border-orange-100">
                        <span className="text-sm font-bold text-[#0f172a] uppercase tracking-wide">
                            Trạng thái đơn hàng
                        </span>
                        <OrderStatusBadge status={order.status} />
                    </div>

                    {/* Info Grid */}
                    <div className="grid grid-cols-1 sm:grid-cols-2 gap-6 mb-8">
                        <div>
                            <h4 className="text-sm font-bold text-[#0f172a] mb-3 flex items-center gap-2">
                                <span className="text-lg">📍</span>
                                Địa chỉ nhận hàng
                            </h4>
                            <p className="text-[#334155] text-sm leading-relaxed bg-gray-50 p-4 rounded-xl border border-gray-100">
                                {order.shippingAddress}
                            </p>
                        </div>
                        <div>
                            <h4 className="text-sm font-bold text-[#0f172a] mb-3 flex items-center gap-2">
                                <span className="text-lg">💳</span>
                                Thanh toán
                            </h4>
                            <p className="text-[#334155] text-sm leading-relaxed bg-gray-50 p-4 rounded-xl border border-gray-100">
                                {order.paymentMethod === 'COD'
                                    ? '💵 Thanh toán khi nhận hàng (COD)'
                                    : '🏦 Chuyển khoản ngân hàng'}
                            </p>
                        </div>
                    </div>

                    {/* Items List */}
                    <h4 className="text-sm font-bold text-[#0f172a] mb-4 flex items-center gap-2">
                        <span className="text-lg">🛍️</span>
                        Danh sách sản phẩm từ {shopName}
                    </h4>
                    <div className="space-y-3 mb-6">
                        {order.items?.map((item, idx) => (
                            <div
                                key={idx}
                                className="flex gap-4 p-4 border border-gray-100 rounded-xl hover:border-[#FF6B6B] transition bg-white shadow-sm"
                            >
                                <img
                                    src={item.imageUrl || 'https://placehold.co/64x64/FF6B6B/FFFFFF?text=Food'}
                                    alt={item.name}
                                    className="w-20 h-20 rounded-xl object-cover bg-gray-100 border-2 border-white shadow-sm"
                                />
                                <div className="flex-1">
                                    <div className="flex justify-between items-start mb-2">
                                        <h5 className="font-bold text-[#0f172a] text-base">
                                            {item.name}
                                        </h5>
                                        <span className="font-bold text-[#FF6B6B] text-lg">
                                            {formatPrice(item.price * item.quantity)}
                                        </span>
                                    </div>
                                    <div className="text-sm text-[#334155]">
                                        Số lượng: <span className="font-semibold">{item.quantity}</span> x{' '}
                                        {formatPrice(item.price)}
                                    </div>
                                </div>
                            </div>
                        ))}
                    </div>

                    {/* Total */}
                    <div className="pt-6 border-t-2 border-gray-100 flex justify-between items-center">
                        <span className="text-xl font-bold text-[#0f172a]">Tổng cộng</span>
                        <span className="text-3xl font-bold text-[#FF6B6B]">
                            {formatPrice(order.totalAmount)}
                        </span>
                    </div>
                </div>

                {/* Footer */}
                <div className="p-6 border-t border-gray-100 bg-gray-50 rounded-b-2xl flex justify-end gap-3">
                    <button
                        onClick={onClose}
                        className="px-6 py-3 bg-white border-2 border-gray-200 rounded-xl font-semibold text-[#334155] hover:bg-gray-50 transition"
                    >
                        Đóng
                    </button>
                    {order.status === 'PENDING' && (
                        <button
                            onClick={handleCancel}
                            className="px-6 py-3 bg-red-50 text-red-600 border-2 border-red-200 rounded-xl font-bold hover:bg-red-100 hover:border-red-300 transition"
                        >
                            ❌ Hủy đơn hàng
                        </button>
                    )}
                </div>
            </div>
        </div>
    );
};

export default OrderDetailModal;
