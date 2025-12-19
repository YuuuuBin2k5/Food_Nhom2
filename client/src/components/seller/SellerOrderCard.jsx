import { formatPrice } from '../../utils/format';
import { formatDate } from '../../utils/dateHelper';
import OrderStatusBadge from '../common/OrderStatusBadge';

const SellerOrderCard = ({ order, onViewDetail, onUpdateStatus }) => {
    return (
        <div className="bg-white rounded-2xl shadow-sm border border-gray-100 overflow-hidden hover:shadow-lg transition-all duration-300 hover:-translate-y-1">
            {/* Order Header */}
            <div className="p-6 flex flex-col lg:flex-row justify-between items-start lg:items-center gap-4 bg-gradient-to-r from-orange-50 to-amber-50 border-b border-gray-100">
                <div className="flex flex-col gap-2">
                    <div className="flex items-center gap-3 flex-wrap">
                        <span className="font-bold text-[#0f172a] text-xl">
                            #{order.orderId}
                        </span>
                        <OrderStatusBadge status={order.status} />
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
                        <span className="text-sm text-[#334155] block">Tổng tiền</span>
                        <span className="text-2xl font-bold text-[#FF6B6B]">
                            {formatPrice(order.totalAmount)}
                        </span>
                    </div>
                </div>
            </div>

            {/* Order Items Preview */}
            <div className="p-6">
                <div className="space-y-3 mb-4">
                    {order.items?.slice(0, 2).map((item, idx) => (
                        <div key={idx} className="flex items-center gap-4 p-3 rounded-xl bg-gray-50">
                            <div className="w-12 h-12 bg-gradient-to-br from-orange-100 to-amber-100 rounded-xl flex items-center justify-center text-2xl">
                                🍲
                            </div>
                            <div className="flex-1">
                                <h4 className="font-semibold text-[#0f172a]">{item.name}</h4>
                                <div className="text-sm text-[#334155] flex items-center gap-2 mt-1">
                                    <span className="font-medium">x{item.quantity}</span>
                                    <span>•</span>
                                    <span className="text-[#FF6B6B] font-semibold">
                                        {formatPrice(item.price)}
                                    </span>
                                </div>
                            </div>
                        </div>
                    ))}
                </div>
                {order.items?.length > 2 && (
                    <p className="text-sm text-[#334155] text-center italic">
                        ...và {order.items.length - 2} sản phẩm khác
                    </p>
                )}
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
                    {order.status === 'PENDING' && (
                        <>
                            <button
                                onClick={() => onUpdateStatus(order.orderId, 'CONFIRMED')}
                                className="px-4 py-2 bg-green-50 text-green-600 border-2 border-green-200 rounded-xl font-bold hover:bg-green-100 hover:border-green-300 transition text-sm flex items-center gap-1"
                            >
                                ✅ Duyệt đơn
                            </button>
                            <button
                                onClick={() => onUpdateStatus(order.orderId, 'CANCELLED')}
                                className="px-4 py-2 bg-red-50 text-red-600 border-2 border-red-200 rounded-xl font-bold hover:bg-red-100 hover:border-red-300 transition text-sm flex items-center gap-1"
                            >
                                ❌ Từ chối
                            </button>
                        </>
                    )}
                    {order.status === 'CONFIRMED' && (
                        <div className="px-4 py-2 bg-blue-50 text-blue-600 border-2 border-blue-200 rounded-xl font-semibold text-sm flex items-center gap-1">
                            ⏳ Chờ shipper nhận đơn
                        </div>
                    )}
                </div>
            </div>
        </div>
    );
};

export default SellerOrderCard;
