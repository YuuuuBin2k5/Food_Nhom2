import { useState, useEffect, useMemo } from "react";
import { useOrders } from "../../hooks/useOrders";
import LoadingSpinner from "../../components/common/LoadingSpinner";
import EmptyState from "../../components/common/EmptyState";
import PageHeader from "../../components/common/PageHeader";
import StatusFilter from "../../components/common/StatusFilter";
import ShipperStatsCards from "../../components/shipper/ShipperStatsCards";
import ShipperOrderCard from "../../components/shipper/ShipperOrderCard";
import ShipperOrderDetailModal from "../../components/shipper/ShipperOrderDetailModal";

const ShipperOrders = () => {
    const { orders, loading, fetchOrders, updateOrderStatus } = useOrders('shipper');
    const [filterStatus, setFilterStatus] = useState("AVAILABLE");
    const [selectedOrder, setSelectedOrder] = useState(null);

    // Load orders once on mount
    useEffect(() => {
        fetchOrders();
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, []);

    // Calculate stats
    const stats = useMemo(() => {
        const available = orders.filter(o => o.status === 'CONFIRMED').length;
        const myShipping = orders.filter(o => o.status === 'SHIPPING').length;
        const myDelivered = orders.filter(o => o.status === 'DELIVERED').length;
        const totalEarnings = orders
            .filter(o => o.status === 'DELIVERED')
            .reduce((sum, o) => sum + (o.shippingFee || 15000), 0);
        
        return { available, myShipping, myDelivered, totalEarnings };
    }, [orders]);

    const handleUpdateStatus = async (orderId, newStatus) => {
        const actionText = newStatus === 'SHIPPING' ? 'nhận đơn này và bắt đầu giao hàng' : 'xác nhận đã giao hàng thành công';
        if (!window.confirm(`Bạn có chắc muốn ${actionText} cho đơn #${orderId}?`)) return;
        
        await updateOrderStatus(orderId, newStatus);
        if (selectedOrder?.orderId === orderId) setSelectedOrder(null);
    };

    const getStatusLabel = (status) => {
        const labels = {
            AVAILABLE: 'Đơn có sẵn',
            CONFIRMED: 'Đơn có sẵn',
            SHIPPING: 'Đang giao',
            DELIVERED: 'Đã giao'
        };
        return labels[status] || status;
    };

    const filteredOrders = filterStatus === 'AVAILABLE' 
        ? orders.filter(o => o.status === 'CONFIRMED')
        : orders.filter(o => o.status === filterStatus);

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
                icon="🚚"
                title="Shipper Dashboard"
                subtitle="Quản lý và giao các đơn hàng"
            />

            <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
                <ShipperStatsCards stats={stats} onFilterChange={setFilterStatus} />

                <StatusFilter
                    statuses={['AVAILABLE', 'SHIPPING', 'DELIVERED']}
                    activeStatus={filterStatus}
                    onChange={setFilterStatus}
                    getLabel={getStatusLabel}
                />

                {filteredOrders.length === 0 ? (
                    <EmptyState
                        icon="📭"
                        title="Không có đơn hàng nào"
                        description={
                            filterStatus === 'AVAILABLE'
                                ? 'Chưa có đơn hàng nào có sẵn để nhận'
                                : `Không có đơn hàng ở trạng thái "${getStatusLabel(filterStatus)}"`
                        }
                    />
                ) : (
                    <div className="space-y-4">
                        {filteredOrders.map((order) => (
                            <ShipperOrderCard
                                key={order.orderId}
                                order={order}
                                onViewDetail={setSelectedOrder}
                                onUpdateStatus={handleUpdateStatus}
                            />
                        ))}
                    </div>
                )}
            </div>

            <ShipperOrderDetailModal
                order={selectedOrder}
                onClose={() => setSelectedOrder(null)}
                onUpdateStatus={handleUpdateStatus}
            />
        </div>
    );
};

export default ShipperOrders;
