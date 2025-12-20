import { useState, useEffect } from "react";
import { useOrders } from "../../hooks/useOrders";
import LoadingSpinner from "../../components/common/LoadingSpinner";
import EmptyState from "../../components/common/EmptyState";
import PageHeader from "../../components/common/PageHeader";
import StatusFilter from "../../components/common/StatusFilter";
import SellerOrderCard from "../../components/seller/SellerOrderCard";
import SellerOrderDetailModal from "../../components/seller/SellerOrderDetailModal";
import { ORDER_STATUS_LABELS } from "../../utils/constants";

const SellerOrders = () => {
    const { orders, loading, fetchOrders, updateOrderStatus } = useOrders('seller');
    const [filterStatus, setFilterStatus] = useState("ALL");
    const [selectedOrder, setSelectedOrder] = useState(null);

    // Load orders once on mount
    useEffect(() => {
        fetchOrders();
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, []);

    const handleUpdateStatus = async (orderId, newStatus) => {
        const actionText = newStatus === "CONFIRMED" ? "duyệt" : "hủy";
        if (!window.confirm(`Bạn có chắc muốn ${actionText} đơn hàng #${orderId}?`)) return;
        
        await updateOrderStatus(orderId, newStatus);
        if (selectedOrder?.orderId === orderId) setSelectedOrder(null);
    };

    const getStatusLabel = (status) => {
        const labels = {
            ALL: "Tất cả",
            ...ORDER_STATUS_LABELS
        };
        return labels[status] || status;
    };

    const filteredOrders = filterStatus === "ALL" ? orders : orders.filter(o => o.status === filterStatus);

    if (loading) {
        return (
            <div className="min-h-screen bg-gradient-to-br from-orange-50 via-amber-50 to-yellow-50 flex items-center justify-center">
                <LoadingSpinner />
            </div>
        );
    }

    return (
        <div className="min-h-screen bg-gradient-to-br from-orange-50 via-amber-50 to-yellow-50 pb-10">
            <PageHeader
                icon="🏪"
                title="Quản lý Đơn hàng"
                subtitle="Theo dõi và xử lý các đơn hàng từ khách hàng"
            />

            <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
                <StatusFilter
                    statuses={["ALL", "PENDING", "CONFIRMED", "SHIPPING", "DELIVERED", "CANCELLED"]}
                    activeStatus={filterStatus}
                    onChange={setFilterStatus}
                    getLabel={getStatusLabel}
                />

                {filteredOrders.length === 0 ? (
                    <EmptyState
                        icon="📭"
                        title="Không có đơn hàng nào"
                        description={
                            filterStatus === "ALL"
                                ? "Chưa có đơn hàng nào trong hệ thống"
                                : `Không có đơn hàng ở trạng thái "${getStatusLabel(filterStatus)}"`
                        }
                    />
                ) : (
                    <div className="space-y-4">
                        {filteredOrders.map((order) => (
                            <SellerOrderCard
                                key={order.orderId}
                                order={order}
                                onViewDetail={setSelectedOrder}
                                onUpdateStatus={handleUpdateStatus}
                            />
                        ))}
                    </div>
                )}
            </div>

            <SellerOrderDetailModal
                order={selectedOrder}
                onClose={() => setSelectedOrder(null)}
                onUpdateStatus={handleUpdateStatus}
            />
        </div>
    );
};

export default SellerOrders;
