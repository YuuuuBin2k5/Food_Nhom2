import { formatPrice } from '../../utils/format';
import { formatDate } from '../../utils/dateHelper';
import OrderStatusBadge from '../common/OrderStatusBadge';

/**
 * Seller Order Detail Modal Component
 */
const SellerOrderDetailModal = ({ order, onClose, onUpdateStatus }) => {
    if (!order) return null;

    return (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4">
            <div
                className="absolute inset-0 bg-black/60 backdrop-blur-sm"
                onClick={onClose}
            />
            <div className="relative bg-white rounded-2xl shadow-2xl w-full max-w-3xl max-h-[90vh] flex flex-col animate-in zoom-in-95 duration-200">
                {/* Modal Header */}
                <div className="p-6 border-b border-gray-100 flex justify-between items-center bg-gradient-to-r from-orange-50 to-amber-50 rounded-t-2xl">
                    <div>
                        <h3 className="text-2xl font-bold text-[#0f172a]">
                            Chi tiết đơn hàng #{order.orderId}
                        </h3>
                        <p className="text-sm text-[#334155] mt-1">
                            📅 {formatDate(order.orderDate)}
                        </p>
                    </div>
                    <button
                        onClick={onClose}
                        className="w-10 h-10 flex items-center justify-center rounded-full bg-white hover:bg-gray-100 text-gray-500 transition shadow-sm"
                    >
                        ✕
                    </button>
                </div>

                {/* Modal Content */}
                <div className="p-6 overflow-y-auto">
                    {/* Status & Customer Info */}
                    <div className="grid grid-cols-1 sm:grid-cols-2 gap-4 mb-6">
                        <div className="p-5 bg-gradient-to-r from-orange-50 to-amber-50 rounded-xl border border-orange-100">
                            <p className="text-xs text-[#334155] uppercase font-semibold mb-2">Khách hàng</p>
                            <p className="font-bold text-[#0f172a] text-lg">👤 {order.buyerName}</p>
                        </div>
                        <div className="p-5 bg-gradient-to-r from-orange-50 to-amber-50 rounded-xl border border-orange-100">
                            <p className="text-xs text-[#334155] uppercase font-semibold mb-2">Trạng thái</p>
                            <OrderStatusBadge status={order.status} />
                        </div>
                    </div>

                    {/* Item List */}
                    <h4 className="text-sm font-bold text-[#0f172a] mb-4 flex items-center gap-2">
                        <span className="text-lg">🛍️</span>
                        Danh sách sản phẩm
                    </h4>
                    <div className="space-y-3 mb-6">
                        {order.items?.map((item, idx) => (
                            <div
                                key={idx}
                                className="flex gap-4 p-4 border border-gray-100 rounded-xl hover:border-[#FF6B6B] transition bg-white shadow-sm"
                            >
                                <div className="w-16 h-16 bg-gradient-to-br from-orange-100 to-amber-100 rounded-xl flex items-center justify-center text-3xl">
                                    🍲
                                </div>
                                <div className="flex-1">
                                    <div className="flex justify-between items-start mb-2">
                                        <h5 className="font-bold text-[#0f172a] text-base">{item.name}</h5>
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

                {/* Modal Footer */}
                <div className="p-6 border-t border-gray-100 bg-gray-50 rounded-b-2xl flex justify-end gap-3">
                    <button
                        onClick={onClose}
                        className="px-6 py-3 bg-white border-2 border-gray-200 rounded-xl font-semibold text-[#334155] hover:bg-gray-50 transition"
                    >
                        Đóng
                    </button>
                    {order.status === 'PENDING' && (
                        <button
                            onClick={() => {
                                onUpdateStatus(order.orderId, 'CONFIRMED');
                                onClose();
                            }}
                            className="px-6 py-3 bg-gradient-to-r from-[#FF6B6B] via-[#FF8E53] to-[#FFC75F] text-white rounded-xl font-bold hover:opacity-90 transition shadow-lg"
                        >
                            ✅ Duyệt đơn ngay
                        </button>
                    )}
                    {order.status === 'CONFIRMED' && (
                        <div className="px-6 py-3 bg-blue-50 text-blue-600 border-2 border-blue-200 rounded-xl font-semibold flex items-center gap-2">
                            ⏳ Đơn hàng đang chờ shipper nhận
                        </div>
                    )}
                </div>
            </div>
        </div>
    );
};

export default SellerOrderDetailModal;
