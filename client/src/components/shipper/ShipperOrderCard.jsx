import { formatPrice } from '../../utils/format';
import { formatDate } from '../../utils/dateHelper';

const ShipperOrderCard = ({ order, onViewDetail, onUpdateStatus }) => {
    const getStatusBadge = (status) => {
        const config = {
            CONFIRMED: { label: 'Chờ giao', className: 'bg-blue-100 text-blue-700 border-blue-200', icon: '📦' },
            SHIPPING: { label: 'Đang giao', className: 'bg-purple-100 text-purple-700 border-purple-200', icon: '🚚' },
            DELIVERED: { label: 'Đã giao', className: 'bg-green-100 text-green-700 border-green-200', icon: '✅' },
        };
        const item = config[status] || { label: status, className: 'bg-gray-100 text-gray-700 border-gray-200', icon: '●' };

        return (
            <span className={`px-3 py-1 rounded-full text-xs font-bold border flex items-center gap-1 w-fit ${item.className}`}>
                <span>{item.icon}</span> {item.label}
            </span>
        );
    };

    return (
        <div className="bg-white rounded-2xl shadow-sm border border-gray-100 overflow-hidden hover:shadow-lg transition-all duration-300 hover:-translate-y-1">
            {/* Order Header */}
            <div className="p-6 flex flex-col lg:flex-row justify-between items-start lg:items-center gap-4 bg-gradient-to-r from-orange-50 to-amber-50 border-b border-gray-100">
                <div className="flex flex-col gap-2">
                    <div className="flex items-center gap-3 flex-wrap">
                        <span className="font-bold text-[#0f172a] text-xl">
                            #{order.orderId}
                        </span>
                        {getStatusBadge(order.status)}
                    </div>
                    <div className="flex flex-col sm:flex-row sm:items-center gap-2 sm:gap-4 text-sm text-[#334155]">
                        <span className="flex items-center gap-1">
                            📅 {formatDate(order.orderDate)}
                        </span>
                        <span className="flex items-center gap-1">
                            👤 {order.buyerName}
                        </span>
                    </div>
                </div>
                <div className="flex items-center gap-4">
                    <div className="text-right">
                        <span className="text-sm text-[#334155] block">Phí giao hàng</span>
                        <span className="text-2xl font-bold text-[#10B981]">
                            {formatPrice(order.shippingFee || 15000)}
                        </span>
                    </div>
                </div>
            </div>

            {/* Shipping Address */}
            <div className="p-6 bg-blue-50 border-b border-blue-100">
                <h4 className="text-sm font-bold text-[#0f172a] mb-2 flex items-center gap-2">
                    <span className="text-lg">📍</span>
                    Địa chỉ giao hàng
                </h4>
                <p className="text-[#334155] text-sm">{order.shippingAddress}</p>
            </div>

            {/* Actions Footer */}
            <div className="px-6 py-4 bg-gray-50 border-t border-gray-100 flex flex-wrap justify-between items-center gap-3">
                <button
                    onClick={() => onViewDetail(order)}
                    className="text-[#FF6B6B] font-semibold text-sm hover:underline flex items-center gap-1"
                >
                    Xem chi tiết
                    <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 5l7 7-7 7" />
                    </svg>
                </button>
                <div className="flex gap-2">
                    {order.status === 'CONFIRMED' && (
                        <button
                            onClick={() => onUpdateStatus(order.orderId, 'SHIPPING')}
                            className="px-4 py-2 bg-gradient-to-r from-purple-500 to-purple-600 text-white border-2 border-purple-600 rounded-xl font-bold hover:opacity-90 transition text-sm flex items-center gap-1 shadow-lg"
                        >
                            ✋ Nhận đơn này
                        </button>
                    )}
                    {order.status === 'SHIPPING' && (
                        <button
                            onClick={() => onUpdateStatus(order.orderId, 'DELIVERED')}
                            className="px-4 py-2 bg-green-50 text-green-600 border-2 border-green-200 rounded-xl font-bold hover:bg-green-100 hover:border-green-300 transition text-sm flex items-center gap-1"
                        >
                            ✅ Đã giao
                        </button>
                    )}
                </div>
            </div>
        </div>
    );
};

export default ShipperOrderCard;
