import { formatPrice } from '../../utils/format';

const OrderSummary = ({ cartItems, totals, error, loading, onSubmit }) => {
    return (
        <div className="lg:col-span-1">
            <div className="bg-white rounded-2xl shadow-sm p-6 sticky top-4">
                <h2 className="text-xl font-bold text-[#0f172a] mb-6 pb-4 border-b">
                    Đơn hàng ({cartItems.length} món)
                </h2>

                {/* Scrollable Product List */}
                <div className="max-h-[280px] overflow-y-auto mb-6 space-y-3 pr-2">
                    {cartItems.map((item, index) => (
                        <div key={item.product?.productId || index} className="flex gap-3 pb-3 border-b border-gray-100 last:border-0">
                            <div className="w-16 h-16 bg-gray-100 rounded-lg flex-shrink-0 overflow-hidden">
                                {item.product?.imageUrl ? (
                                    <img
                                        src={item.product.imageUrl}
                                        alt={item.product.name}
                                        className="w-full h-full object-cover"
                                    />
                                ) : (
                                    <div className="w-full h-full flex items-center justify-center text-xs text-gray-400">
                                        No Img
                                    </div>
                                )}
                            </div>
                            <div className="flex-1 min-w-0">
                                <p className="text-sm font-medium text-[#0f172a] line-clamp-2 mb-1">
                                    {item.product?.name}
                                </p>
                                <div className="flex justify-between items-center">
                                    <span className="text-xs text-[#334155]">x{item.quantity}</span>
                                    <span className="text-sm font-semibold text-[#FF6B6B]">
                                        {formatPrice((item.product?.salePrice || item.product?.originalPrice || 0) * item.quantity)}
                                    </span>
                                </div>
                            </div>
                        </div>
                    ))}
                </div>

                {/* Calculations */}
                <div className="space-y-3 mb-6">
                    <div className="flex justify-between text-[#334155]">
                        <span>Tạm tính</span>
                        <span className="font-medium">{formatPrice(totals.subtotal)}</span>
                    </div>
                    <div className="flex justify-between text-[#334155]">
                        <span>Phí vận chuyển</span>
                        <span className="font-medium">{formatPrice(totals.shipping)}</span>
                    </div>
                </div>

                <div className="border-t pt-4 mb-6">
                    <div className="flex justify-between items-center">
                        <span className="text-lg font-semibold text-[#0f172a]">Tổng cộng</span>
                        <span className="text-2xl font-bold text-[#FF6B6B]">
                            {formatPrice(totals.total)}
                        </span>
                    </div>
                </div>

                {/* Error Message */}
                {error && (
                    <div className="mb-4 p-3 bg-red-50 border border-red-200 rounded-xl text-red-600 text-sm">
                        ⚠️ {error}
                    </div>
                )}

                {/* Action Button */}
                <button
                    type="submit"
                    onClick={onSubmit}
                    disabled={loading || cartItems.length === 0}
                    className="w-full py-4 bg-gradient-to-r from-[#FF6B6B] via-[#FF8E53] to-[#FFC75F] text-white font-semibold rounded-xl hover:opacity-90 transition-all shadow-lg hover:shadow-xl disabled:opacity-50 disabled:cursor-not-allowed"
                >
                    {loading ? 'Đang xử lý...' : '🛒 ĐẶT HÀNG'}
                </button>

                <p className="text-center text-xs text-gray-400 mt-4">
                    Bạn có 30 phút để đến lấy đồ sau khi đặt
                </p>
            </div>
        </div>
    );
};

export default OrderSummary;
