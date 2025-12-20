import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { useOrders } from "../../hooks/useOrders";
import LoadingSpinner from "../../components/common/LoadingSpinner";
import EmptyState from "../../components/common/EmptyState";
import OrderCard from "../../components/buyer/OrderCard";
import OrderDetailModal from "../../components/buyer/OrderDetailModal";

const OrderHistoryPage = () => {
    const navigate = useNavigate();
    const { orders, loading, fetchOrders, cancelOrder } = useOrders('buyer');
    const [selectedOrder, setSelectedOrder] = useState(null);

    // Load orders once on mount
    useEffect(() => {
        fetchOrders();
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, []);

    const handleCancelOrder = async (orderId) => {
        const success = await cancelOrder(orderId);
        if (success) {
            setSelectedOrder(null);
        }
    };

    if (loading) {
        return (
            <div className="min-h-screen bg-gradient-to-br from-orange-50 via-amber-50 to-yellow-50 flex items-center justify-center">
                <LoadingSpinner />
            </div>
        );
    }

    return (
        <div className="min-h-screen bg-gradient-to-br from-orange-50 via-amber-50 to-yellow-50 pb-10">
            {/* Header Banner */}
            <div className="bg-gradient-to-r from-[#FF6B6B] via-[#FF8E53] to-[#FFC75F] shadow-lg mb-8">
                <div className="max-w-7xl mx-auto px-4 py-8">
                    <h1 className="text-3xl font-bold text-white flex items-center gap-3">
                        <span className="text-4xl">📦</span>
                        Lịch sử mua hàng
                    </h1>
                    <p className="text-white/90 text-base mt-2">
                        Quản lý và theo dõi các đơn hàng của bạn
                    </p>
                </div>
            </div>

            <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
                {orders.length === 0 ? (
                    <EmptyState
                        icon="🛒"
                        title="Bạn chưa có đơn hàng nào"
                        description="Hãy dạo một vòng chợ và giải cứu những món ngon nhé!"
                        actionLabel="🛍️ Khám phá ngay"
                        onAction={() => navigate("/products")}
                    />
                ) : (
                    <div className="space-y-6">
                        {orders.map((order) => (
                            <OrderCard
                                key={order.orderId}
                                order={order}
                                onClick={() => setSelectedOrder(order)}
                            />
                        ))}
                    </div>
                )}
            </div>

            {/* Order Detail Modal */}
            <OrderDetailModal
                order={selectedOrder}
                onClose={() => setSelectedOrder(null)}
                onCancel={handleCancelOrder}
            />
        </div>
    );
};

export default OrderHistoryPage;
