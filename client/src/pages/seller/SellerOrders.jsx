import { useState, useEffect } from "react";
import api from "../../services/api";
import { showToast } from "../../utils/toast";
import LoadingSpinner from "../../components/common/LoadingSpinner";
import { formatPrice } from "../../utils/format";

const SellerOrders = () => {
    const [orders, setOrders] = useState([]);
    const [loading, setLoading] = useState(true);
    const [filterStatus, setFilterStatus] = useState("ALL");
    const [selectedOrder, setSelectedOrder] = useState(null);

    useEffect(() => {
        fetchOrders();
    }, []);

    const fetchOrders = async () => {
        setLoading(true);
        try {
            const res = await api.get("/seller/orders");
            setOrders(res.data || []);
        } catch (error) {
            console.error("Lỗi tải đơn hàng:", error);
            showToast.error("Không thể tải danh sách đơn hàng");
        } finally {
            setLoading(false);
        }
    };

    const handleUpdateStatus = async (orderId, newStatus) => {
        const actionText = newStatus === "CONFIRMED" ? "duyệt" : newStatus === "SHIPPING" ? "chuyển sang giao hàng" : "hủy";
        if (!window.confirm(`Bạn có chắc muốn ${actionText} đơn hàng #${orderId}?`)) return;

        try {
            await api.put(`/seller/orders/${orderId}/status`, { status: newStatus });
            showToast.success(`Đã ${actionText} đơn hàng thành công!`);
            fetchOrders();
            if (selectedOrder?.orderId === orderId) setSelectedOrder(null);
        } catch (error) {
            showToast.error(`Không thể ${actionText} đơn hàng. Lỗi server.`);
        }
    };

    const formatDate = (dateString) => {
        if (!dateString) return "";
        return new Date(dateString).toLocaleString("vi-VN", {
            day: "2-digit",
            month: "2-digit",
            year: "numeric",
            hour: "2-digit",
            minute: "2-digit",
        });
    };

    const getStatusBadge = (status) => {
        const config = {
            PENDING: { label: "Chờ xác nhận", className: "bg-yellow-100 text-yellow-700 border-yellow-200", icon: "⏳" },
            CONFIRMED: { label: "Đã xác nhận", className: "bg-blue-100 text-blue-700 border-blue-200", icon: "👨‍🍳" },
            SHIPPING: { label: "Đang giao", className: "bg-purple-100 text-purple-700 border-purple-200", icon: "🚚" },
            DELIVERED: { label: "Đã giao", className: "bg-green-100 text-green-700 border-green-200", icon: "✅" },
            CANCELLED: { label: "Đã hủy", className: "bg-red-100 text-red-700 border-red-200", icon: "❌" },
        };
        const item = config[status] || { label: status, className: "bg-gray-100 text-gray-700 border-gray-200", icon: "●" };

        return (
            <span className={`px-3 py-1 rounded-full text-xs font-bold border flex items-center gap-1 w-fit ${item.className}`}>
                <span>{item.icon}</span> {item.label}
            </span>
        );
    };

    const getStatusLabel = (status) => {
        const labels = {
            ALL: "Tất cả",
            PENDING: "Chờ xác nhận",
            CONFIRMED: "Đã xác nhận",
            SHIPPING: "Đang giao",
            DELIVERED: "Đã giao",
            CANCELLED: "Đã hủy"
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
            {/* Header Banner */}
            <div className="bg-gradient-to-r from-[#FF6B6B] via-[#FF8E53] to-[#FFC75F] shadow-lg mb-8">
                <div className="max-w-7xl mx-auto px-4 py-8">
                    <h1 className="text-3xl font-bold text-white flex items-center gap-3">
                        <span className="text-4xl">🏪</span>
                        Quản lý Đơn hàng
                    </h1>
                    <p className="text-white/90 text-base mt-2">
                        Theo dõi và xử lý các đơn hàng từ khách hàng
                    </p>
                </div>
            </div>

            <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
                {/* Filter Tabs */}
                <div className="mb-6 bg-white rounded-2xl shadow-sm border border-gray-100 p-4">
                    <div className="flex flex-wrap gap-2">
                        {["ALL", "PENDING", "CONFIRMED", "SHIPPING", "DELIVERED", "CANCELLED"].map((status) => (
                            <button
                                key={status}
                                onClick={() => setFilterStatus(status)}
                                className={`px-5 py-2.5 rounded-xl text-sm font-semibold transition-all ${
                                    filterStatus === status
                                        ? "bg-gradient-to-r from-[#FF6B6B] via-[#FF8E53] to-[#FFC75F] text-white shadow-lg scale-105"
                                        : "bg-gray-50 text-[#334155] hover:bg-gray-100 border border-gray-200"
                                }`}
                            >
                                {getStatusLabel(status)}
                            </button>
                        ))}
                    </div>
                </div>

                {/* Orders List */}
                {filteredOrders.length === 0 ? (
                    <div className="text-center py-20 bg-white rounded-2xl shadow-sm border-2 border-dashed border-gray-200">
                        <div className="w-32 h-32 bg-gradient-to-br from-orange-100 to-amber-100 rounded-full flex items-center justify-center mx-auto mb-6">
                            <span className="text-6xl">📭</span>
                        </div>
                        <h3 className="text-2xl font-bold text-[#0f172a] mb-3">
                            Không có đơn hàng nào
                        </h3>
                        <p className="text-[#334155]">
                            {filterStatus === "ALL" ? "Chưa có đơn hàng nào trong hệ thống" : `Không có đơn hàng ở trạng thái "${getStatusLabel(filterStatus)}"`}
                        </p>
                    </div>
                ) : (
                    <div className="space-y-4">
                        {filteredOrders.map((order) => (
                            <div
                                key={order.orderId}
                                className="bg-white rounded-2xl shadow-sm border border-gray-100 overflow-hidden hover:shadow-lg transition-all duration-300 hover:-translate-y-1"
                            >
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
                                        onClick={() => setSelectedOrder(order)}
                                        className="text-[#FF6B6B] font-semibold text-sm hover:underline flex items-center gap-1"
                                    >
                                        Xem chi tiết
                                        <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 5l7 7-7 7" />
                                        </svg>
                                    </button>
                                    <div className="flex gap-2">
                                        {order.status === "PENDING" && (
                                            <>
                                                <button
                                                    onClick={() => handleUpdateStatus(order.orderId, "CONFIRMED")}
                                                    className="px-4 py-2 bg-green-50 text-green-600 border-2 border-green-200 rounded-xl font-bold hover:bg-green-100 hover:border-green-300 transition text-sm flex items-center gap-1"
                                                >
                                                    ✅ Duyệt đơn
                                                </button>
                                                <button
                                                    onClick={() => handleUpdateStatus(order.orderId, "CANCELLED")}
                                                    className="px-4 py-2 bg-red-50 text-red-600 border-2 border-red-200 rounded-xl font-bold hover:bg-red-100 hover:border-red-300 transition text-sm flex items-center gap-1"
                                                >
                                                    ❌ Từ chối
                                                </button>
                                            </>
                                        )}
                                        {order.status === "CONFIRMED" && (
                                            <button
                                                onClick={() => handleUpdateStatus(order.orderId, "SHIPPING")}
                                                className="px-4 py-2 bg-purple-50 text-purple-600 border-2 border-purple-200 rounded-xl font-bold hover:bg-purple-100 hover:border-purple-300 transition text-sm flex items-center gap-1"
                                            >
                                                📦 Giao hàng
                                            </button>
                                        )}
                                    </div>
                                </div>
                            </div>
                        ))}
                    </div>
                )}
            </div>

            {/* MODAL CHI TIẾT */}
            {selectedOrder && (
                <div className="fixed inset-0 z-50 flex items-center justify-center p-4">
                    <div
                        className="absolute inset-0 bg-black/60 backdrop-blur-sm"
                        onClick={() => setSelectedOrder(null)}
                    ></div>
                    <div className="relative bg-white rounded-2xl shadow-2xl w-full max-w-3xl max-h-[90vh] flex flex-col animate-in zoom-in-95 duration-200">
                        {/* Modal Header */}
                        <div className="p-6 border-b border-gray-100 flex justify-between items-center bg-gradient-to-r from-orange-50 to-amber-50 rounded-t-2xl">
                            <div>
                                <h3 className="text-2xl font-bold text-[#0f172a]">
                                    Chi tiết đơn hàng #{selectedOrder.orderId}
                                </h3>
                                <p className="text-sm text-[#334155] mt-1">
                                    📅 {formatDate(selectedOrder.orderDate)}
                                </p>
                            </div>
                            <button
                                onClick={() => setSelectedOrder(null)}
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
                                    <p className="font-bold text-[#0f172a] text-lg">👤 {selectedOrder.buyerName}</p>
                                </div>
                                <div className="p-5 bg-gradient-to-r from-orange-50 to-amber-50 rounded-xl border border-orange-100">
                                    <p className="text-xs text-[#334155] uppercase font-semibold mb-2">Trạng thái</p>
                                    {getStatusBadge(selectedOrder.status)}
                                </div>
                            </div>

                            {/* Item List */}
                            <h4 className="text-sm font-bold text-[#0f172a] mb-4 flex items-center gap-2">
                                <span className="text-lg">🛍️</span>
                                Danh sách sản phẩm
                            </h4>
                            <div className="space-y-3 mb-6">
                                {selectedOrder.items?.map((item, idx) => (
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
                                                Số lượng: <span className="font-semibold">{item.quantity}</span> x{" "}
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
                                    {formatPrice(selectedOrder.totalAmount)}
                                </span>
                            </div>
                        </div>

                        {/* Modal Footer */}
                        <div className="p-6 border-t border-gray-100 bg-gray-50 rounded-b-2xl flex justify-end gap-3">
                            <button
                                onClick={() => setSelectedOrder(null)}
                                className="px-6 py-3 bg-white border-2 border-gray-200 rounded-xl font-semibold text-[#334155] hover:bg-gray-50 transition"
                            >
                                Đóng
                            </button>
                            {selectedOrder.status === "PENDING" && (
                                <button
                                    onClick={() => {
                                        handleUpdateStatus(selectedOrder.orderId, "CONFIRMED");
                                        setSelectedOrder(null);
                                    }}
                                    className="px-6 py-3 bg-gradient-to-r from-[#FF6B6B] via-[#FF8E53] to-[#FFC75F] text-white rounded-xl font-bold hover:opacity-90 transition shadow-lg"
                                >
                                    ✅ Duyệt đơn ngay
                                </button>
                            )}
                            {selectedOrder.status === "CONFIRMED" && (
                                <button
                                    onClick={() => {
                                        handleUpdateStatus(selectedOrder.orderId, "SHIPPING");
                                        setSelectedOrder(null);
                                    }}
                                    className="px-6 py-3 bg-gradient-to-r from-purple-500 to-purple-600 text-white rounded-xl font-bold hover:opacity-90 transition shadow-lg"
                                >
                                    📦 Giao hàng
                                </button>
                            )}
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
};

export default SellerOrders;
