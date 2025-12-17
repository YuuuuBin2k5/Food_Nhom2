import { useLocation, useNavigate } from 'react-router-dom';
import { useEffect } from 'react';

function OrderSuccessPage() {
    const location = useLocation();
    const navigate = useNavigate();
    const { order } = location.state || {};

    // Mock data if no state (for testing/preview)
    const orderData = order || {
        orderId: 'FS-6IYNQ6',
        items: [
            {
                name: 'Trái cây tổng hợp',
                quantity: 1,
                shopName: 'Fresh Fruit Corner',
                image: 'https://images.unsplash.com/photo-1610832958506-aa56368176cf?q=80&w=2670&auto=format&fit=crop'
            }
        ]
    };

    useEffect(() => {
        // Scroll to top on load
        window.scrollTo(0, 0);
    }, []);

    const handleCopyCode = () => {
        navigator.clipboard.writeText(orderData.orderId);
        // Could add a toast here
    };

    return (
        <div className="min-h-screen bg-[#FFFBF7] py-12 px-4 sm:px-6 lg:px-8 flex items-center justify-center">
            <div className="max-w-xl w-full text-center">
                {/* Success Icon */}
                <div className="w-24 h-24 mx-auto mb-6 bg-[#D1F7C4] rounded-full flex items-center justify-center relative">
                    <div className="absolute top-1/2 left-1/2 transform -translate-x-1/2 -translate-y-1/2 text-4xl">
                        🎉
                    </div>
                    {/* Confetti decoration could be an image or SVG */}
                    <svg className="w-12 h-12 text-green-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        {/* Simple confetti icon representation */}
                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M5 13l4 4L19 7" />
                    </svg>
                </div>

                <h1 className="text-3xl font-bold text-gray-900 mb-2">
                    Đặt hàng thành công!
                    <span className="ml-2 inline-block animate-bounce">🎇</span>
                </h1>

                <p className="text-gray-500 mb-8">
                    Hãy mang mã này đến cửa hàng để nhận đồ
                </p>

                {/* Ticket Card */}
                <div className="bg-[#F3F9F1] border border-[#E0EEDC] rounded-2xl p-8 mb-8 relative overflow-hidden">
                    <div className="relative z-10">
                        <div className="flex items-center justify-center gap-2 text-[#4A6445] mb-2 font-medium">
                            <svg className="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 4v1m6 11h2m-6 0h-2v4m0-11v3m0 0h.01M12 12h4.01M16 20h4M4 12h4m12 0h.01M5 8h2a1 1 0 001-1V5a1 1 0 00-1-1H5a1 1 0 00-1 1v2a1 1 0 001 1zm12 0h2a1 1 0 001-1V5a1 1 0 00-1-1h-2a1 1 0 00-1 1v2a1 1 0 001 1zM5 20h2a1 1 0 001-1v-2a1 1 0 00-1-1H5a1 1 0 00-1 1v2a1 1 0 001 1z" />
                            </svg>
                            Mã đặt hàng
                        </div>

                        <div className="flex items-center justify-center gap-3 mb-2">
                            <span className="text-4xl font-bold text-[#3E2E20] tracking-wider">
                                {orderData.orderId}
                            </span>
                            <button
                                onClick={handleCopyCode}
                                className="text-gray-400 hover:text-gray-600 transition-colors"
                            >
                                <svg className="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M8 16H6a2 2 0 01-2-2V6a2 2 0 012-2h8a2 2 0 012 2v2m-6 12h8a2 2 0 002-2v-8a2 2 0 00-2-2h-8a2 2 0 00-2 2v8a2 2 0 002 2z" />
                                </svg>
                            </button>
                        </div>

                        <p className="text-[#8D7F73] text-sm">
                            Hết hạn sau 30 phút
                        </p>
                    </div>
                </div>

                {/* Ordered Items */}
                <div className="text-left mb-8">
                    <h3 className="font-bold text-gray-900 mb-4">Các món đã đặt:</h3>
                    <div className="bg-white rounded-xl shadow-sm border border-gray-100 divide-y divide-gray-100">
                        {orderData.items.map((item, index) => (
                            <div key={index} className="p-4 flex items-center gap-4">
                                <img
                                    src={item.image || 'https://via.placeholder.com/64'}
                                    alt={item.name}
                                    className="w-12 h-12 rounded-lg object-cover"
                                />
                                <div>
                                    <h4 className="font-medium text-gray-900">{item.name}</h4>
                                    <p className="text-sm text-gray-500">
                                        x{item.quantity} • {item.shopName}
                                    </p>
                                </div>
                            </div>
                        ))}
                    </div>
                </div>

                {/* Action Button */}
                <button
                    onClick={() => navigate('/products')}
                    className="w-full py-3.5 bg-white border border-[#E5E7EB] text-gray-700 font-medium rounded-full hover:bg-gray-50 transition-colors"
                >
                    Tiếp tục mua sắm
                </button>
            </div>
        </div>
    );
}

export default OrderSuccessPage;
