import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import api from "../../services/api";
import { showToast } from "../../utils/toast";
import LoadingSpinner from "../../components/common/LoadingSpinner";
import { formatPrice } from "../../utils/format";

const OrderHistoryPage = () => {
    const navigate = useNavigate();
    const [orders, setOrders] = useState([]);
    const [loading, setLoading] = useState(true);
    const [selectedOrder, setSelectedOrder] = useState(null);

    useEffect(() => {
        fetchOrders();
    }, []);

    const fetchOrders = async () => {
        setLoading(true);
        try {
            const res = await api.get("/buyer/orders");
            const sortedOrders = (res.data || []).sort(
                (a, b) => new Date(b.orderDate) - new Date(a.orderDate)
            );
            setOrders(sortedOrders);
        } catch (error) {
            console.error("Lỗi tải lịch sử đơn hàng:", error);
            showToast.error("Không thể tải lịch sử mua hàng");
        } finally {
            setLoading(false);
        }
    };

    const handleCancelOrder = async (orderId) => {
        if (!window.confirm("Bạn có chắc chắn muốn hủy đơn hàng này?")) return;

        try {
            await api.put(`/buyer/orders/${orderId}/cancel`);
            showToast.success("Đã hủy đơn hàng thành công");
            fetchOrders();
            if (selectedOrder?.orderId === orderId) setSelectedOrder(null);
        } catch (error) {
            const msg = error.response?.data?.message || "Lỗi khi hủy đơn";
            showToast.error(msg);
        }
    };

    const getStatusBadge = (status) => {
        const config = {
            PENDING: {
                text: "Chờ xác nhận",
                className: "bg-yellow-100 text-yellow-700 border-yellow-200",
                icon: "⏳",
            },
            CONFIRMED: {
                text: "Đã xác nhận",
                className: "bg-blue-100 text-blue-700 border-blue-200",
                icon: "👨‍🍳",
            },
            SHIPPING: {
                text: "Đang giao",
                className: "bg-purple-100 text-purple-700 border-purple-200",
                icon: "🚚",
            },
            DELIVERED: {
                text: "Giao thành công",
                className: "bg-green-100 text-green-700 border-green-200",
                icon: "✅",
            },
            CANCELLED: {
                text: "Đã hủy",
                className: "bg-red-100 text-red-700 border-red-200",
                icon: "❌",
            },
        };

        const item = config[status] || {
            text: status,
            className: "bg-gray-100 text-gray-700",
            icon: "●",
        };

        return (
            <span
                className={`px-3 py-1 rounded-full text-xs font-bold border flex items-center gap-1 w-fit ${item.className}`}
            >
                <span>{item.icon}</span> {item.text}
            </span>
        );
    };

    const formatDate = (dateStr) => {
        return new Date(dateStr).toLocaleString("vi-VN", {
            day: "2-digit",
            month: "2-digit",
            year: "numeric",
            hour: "2-digit",
            minute: "2-digit",
        });
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
                    <div className="text-center py-20 bg-white rounded-2xl shadow-sm border-2 border-dashed border-gray-200">
                        <div className="w-32 h-32 bg-gradient-to-br from-orange-100 to-amber-100 rounded-full flex items-center justify-center mx-auto mb-6">
                            <span className="text-6xl">🛒</span>
                        </div>
                        <h3 className="text-2xl font-bold text-[#0f172a] mb-3">
                            Bạn chưa có đơn hàng nào
                        </h3>
                        <p className="text-[#334155] mb-8 max-w-md mx-auto">
                            Hãy dạo một vòng chợ và giải cứu những món ngon nhé!
                        </p>
                        <button
                            onClick={() => navigate("/products")}
                            className="px-8 py-4 bg-gradient-to-r from-[#FF6B6B] via-[#FF8E53] to-[#FFC75F] text-white rounded-xl font-semibold hover:opacity-90 transition shadow-lg hover:shadow-xl"
                        >
                            🛍️ Khám phá ngay
                        </button>
                    </div>
                ) : (
                    <div className="space-y-6">
                        {orders.map((order) => (
                            <div
                                key={order.orderId}
                                className="bg-white rounded-2xl shadow-sm border border-gray-100 overflow-hidden hover:shadow-lg transition-all duration-300 hover:-translate-y-1 cursor-pointer"
                                onClick={() => setSelectedOrder(order)}
                            >
                                {/* Order Header */}
                                <div className="p-6 flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4 bg-gradient-to-r from-orange-50 to-amber-50 border-b border-gray-100">
                                    <div className="flex flex-col gap-2">
                                        <div className="flex items-center gap-3">
                                            <span className="font-bold text-[#0f172a] text-xl">
                                                #{order.orderId}
                                            </span>
                                            {getStatusBadge(order.status)}
                                        </div>
                                        <span className="text-sm text-[#334155]">
                                            📅 {formatDate(order.orderDate)}
                                        </span>
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
                                                    src={item.imageUrl || "https://placehold.co/64x64/FF6B6B/FFFFFF?text=Food"}
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
                                            ...và {order.items.length - 2} sản phẩm khác
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
                            {/* Status */}
                            <div className="flex justify-between items-center mb-6 p-5 bg-gradient-to-r from-orange-50 to-amber-50 rounded-xl border border-orange-100">
                                <span className="text-sm font-bold text-[#0f172a] uppercase tracking-wide">
                                    Trạng thái đơn hàng
                                </span>
                                {getStatusBadge(selectedOrder.status)}
                            </div>

                            {/* Info Grid */}
                            <div className="grid grid-cols-1 sm:grid-cols-2 gap-6 mb-8">
                                <div>
                                    <h4 className="text-sm font-bold text-[#0f172a] mb-3 flex items-center gap-2">
                                        <span className="text-lg">📍</span>
                                        Địa chỉ nhận hàng
                                    </h4>
                                    <p className="text-[#334155] text-sm leading-relaxed bg-gray-50 p-4 rounded-xl border border-gray-100">
                                        {selectedOrder.shippingAddress}
                                    </p>
                                </div>
                                <div>
                                    <h4 className="text-sm font-bold text-[#0f172a] mb-3 flex items-center gap-2">
                                        <span className="text-lg">💳</span>
                                        Thanh toán
                                    </h4>
                                    <p className="text-[#334155] text-sm leading-relaxed bg-gray-50 p-4 rounded-xl border border-gray-100">
                                        {selectedOrder.paymentMethod === "COD"
                                            ? "💵 Thanh toán khi nhận hàng (COD)"
                                            : "🏦 Chuyển khoản ngân hàng"}
                                    </p>
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
                                        <img
                                            src={item.imageUrl || "https://placehold.co/64x64/FF6B6B/FFFFFF?text=Food"}
                                            alt={item.name}
                                            className="w-20 h-20 rounded-xl object-cover bg-gray-100 border-2 border-white shadow-sm"
                                        />
                                        <div className="flex-1">
                                            <div className="flex justify-between items-start mb-2">
                                                <div>
                                                    <h5 className="font-bold text-[#0f172a] text-base">
                                                        {item.name}
                                                    </h5>
                                                    <p className="text-xs text-[#334155] mt-1">
                                                        🏪 {item.shopName}
                                                    </p>
                                                </div>
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
                                <span className="text-xl font-bold text-[#0f172a]">
                                    Tổng cộng
                                </span>
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
                                    onClick={() => handleCancelOrder(selectedOrder.orderId)}
                                    className="px-6 py-3 bg-red-50 text-red-600 border-2 border-red-200 rounded-xl font-bold hover:bg-red-100 hover:border-red-300 transition"
                                >
                                    ❌ Hủy đơn hàng
                                </button>
                            )}
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
};

export default OrderHistoryPage;
