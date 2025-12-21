import { formatPrice } from '../../utils/format';
import OrderStatusBadge from '../common/OrderStatusBadge';

const OrderCard = ({ order, onClick }) => {
    const formatDate = (dateStr) => {
        return new Date(dateStr).toLocaleString('vi-VN', {
            day: '2-digit',
            month: '2-digit',
            year: 'numeric',
            hour: '2-digit',
            minute: '2-digit'
        });
    };

    // Get shop name from first item (all items in one order are from same seller)
    const shopName = order.items?.[0]?.shopName || 'Cửa hàng';

    return (
        <div
            className="bg-white rounded-2xl shadow-sm border border-gray-100 overflow-hidden hover:shadow-lg transition-all duration-300 hover:-translate-y-1 cursor-pointer"
            onClick={onClick}
        >
            {/* Order Header */}
            <div className="p-6 flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4 bg-gradient-to-r from-orange-50 to-amber-50 border-b border-gray-100">
                <div className="flex flex-col gap-2">
                    <div className="flex items-center gap-3 flex-wrap">
                        <span className="font-bold text-[#0f172a] text-xl">
                            #{order.orderId}
                        </span>
                        <OrderStatusBadge status={order.status} />
                    </div>
                    <div className="flex items-center gap-2 text-sm">
                        <span className="text-[#334155]">📅 {formatDate(order.orderDate)}</span>
                    </div>
                    <div className="flex items-center gap-2 mt-1">
                        <span className="px-3 py-1 bg-white rounded-full text-sm font-semibold text-[#FF6B6B] border border-orange-200 shadow-sm">
                            🏪 {shopName}
                        </span>
                    </div>
                </div>
                <div className="flex flex-col items-end gap-1">
                    <span className="text-sm text-[#334155]">Tổng tiền</span>
                    <span className="text-2xl font-bold text-[#FF6B6B]">
                        {formatPrice(order.totalAmount)}
                    </span>
                </div>
            </div>

            {/* Order Items Preview */}
            <div className="p-6">
                <div className="space-y-3">
                    {order.items?.slice(0, 2).map((item, idx) => (
                        <div
                            key={idx}
                            className="flex items-center gap-4 p-3 rounded-xl bg-gray-50 hover:bg-gray-100 transition"
                        >
                            <img
                                src={item.imageUrl || 'https://placehold.co/64x64/FF6B6B/FFFFFF?text=Food'}
                                alt={item.name}
                                className="w-16 h-16 rounded-xl object-cover border-2 border-white shadow-sm"
                            />
                            <div className="flex-1">
                                <h4 className="font-semibold text-[#0f172a] line-clamp-1">
                                    {item.name}
                                </h4>
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
                {order.items && order.items.length > 2 && (
                    <p className="text-sm text-[#334155] mt-3 text-center italic">
                        ...và {order.items.length - 2} sản phẩm khác từ {shopName}
                    </p>
                )}
            </div>

            {/* Footer */}
            <div className="px-6 py-4 bg-gray-50 border-t border-gray-100 flex justify-end">
                <button className="text-[#FF6B6B] font-semibold text-sm hover:underline flex items-center gap-1">
                    Xem chi tiết
                    <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 5l7 7-7 7" />
                    </svg>
                </button>
            </div>
        </div>
    );
};

export default OrderCard;
