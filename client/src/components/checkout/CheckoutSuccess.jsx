import { useNavigate } from 'react-router-dom';
import { formatPrice } from '../../utils/format';

const CheckoutSuccess = ({ orderId, total, paymentMethod }) => {
    const navigate = useNavigate();

    return (
        <div className="min-h-screen bg-gradient-to-br from-orange-50 via-amber-50 to-yellow-50 flex items-center justify-center p-4">
            <div className="bg-white rounded-3xl shadow-xl p-12 text-center max-w-lg">
                <div className="w-24 h-24 mx-auto mb-6 bg-gradient-to-br from-orange-100 to-amber-100 rounded-full flex items-center justify-center">
                    <svg className="w-12 h-12 text-[#FF6B6B]" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M5 13l4 4L19 7" />
                    </svg>
                </div>

                <h2 className="text-3xl font-bold bg-gradient-to-r from-[#FF6B6B] via-[#FF8E53] to-[#FFC75F] bg-clip-text text-transparent mb-2">
                    Đặt hàng thành công! 🎉
                </h2>
                <p className="text-[#334155] mb-8">
                    Cảm ơn bạn đã mua sắm tại FoodNhom2
                </p>

                <div className="bg-gradient-to-br from-orange-50 to-amber-50 rounded-2xl p-6 mb-8 border border-orange-100">
                    <div className="flex justify-between mb-3 text-sm">
                        <span className="text-[#334155]">Mã đơn hàng:</span>
                        <span className="font-bold text-[#0f172a]">#{orderId}</span>
                    </div>
                    <div className="flex justify-between mb-3 text-sm">
                        <span className="text-[#334155]">Phương thức:</span>
                        <span className="font-medium text-[#0f172a]">
                            {paymentMethod === 'COD' ? '💵 Thanh toán khi nhận' : '🏦 Chuyển khoản'}
                        </span>
                    </div>
                    <div className="border-t border-orange-200 my-3"></div>
                    <div className="flex justify-between text-xl font-bold text-[#FF6B6B]">
                        <span>Tổng tiền:</span>
                        <span>{formatPrice(total)}</span>
                    </div>
                </div>

                <div className="space-y-3">
                    <button
                        onClick={() => navigate('/orders')}
                        className="w-full py-4 bg-gradient-to-r from-[#FF6B6B] via-[#FF8E53] to-[#FFC75F] text-white font-semibold rounded-xl hover:opacity-90 transition-all shadow-lg hover:shadow-xl"
                    >
                        Xem đơn hàng của tôi
                    </button>
                    <button
                        onClick={() => navigate('/products')}
                        className="w-full py-3 bg-white text-[#FF6B6B] font-medium rounded-xl border-2 border-orange-200 hover:bg-orange-50 transition-colors"
                    >
                        Tiếp tục mua sắm
                    </button>
                </div>
            </div>
        </div>
    );
};

export default CheckoutSuccess;
