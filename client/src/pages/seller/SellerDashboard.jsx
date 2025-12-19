import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../../context/AuthContext";
import api from "../../services/api";
import LoadingSpinner from "../../components/common/LoadingSpinner";
import { formatPrice } from "../../utils/format";
import { HeaderSeller } from "./header_seller";

const SellerDashboard = () => {
  const { user } = useAuth();
  const navigate = useNavigate();
  const [loading, setLoading] = useState(true);
  const [stats, setStats] = useState({
    totalProducts: 0,
    activeProducts: 0,
    pendingOrders: 0,
    expiringSoon: 0,
    totalRevenue: 0,
    todayOrders: 0,
  });
  const [recentProducts, setRecentProducts] = useState([]);
  const [recentOrders, setRecentOrders] = useState([]);

  useEffect(() => {
    loadDashboardData();
  }, []);

  const loadDashboardData = async () => {
    setLoading(true);
    try {
      const [productsRes, ordersRes] = await Promise.all([
        api.get("/seller/products"),
        api.get("/seller/orders").catch(() => ({ data: [] })),
      ]);

      const products = productsRes.data || [];
      const orders = ordersRes.data || [];

      // Calculate stats
      const activeProducts = products.filter(
        (p) => p.status === "ACTIVE"
      ).length;
      const expiringSoon = products.filter((p) => {
        if (p.status !== "ACTIVE") return false;
        const daysUntilExpiry = Math.ceil(
          (new Date(p.expirationDate) - new Date()) / (1000 * 60 * 60 * 24)
        );
        return daysUntilExpiry <= 3 && daysUntilExpiry >= 0;
      }).length;

      const pendingOrders = orders.filter((o) => o.status === "PENDING").length;
      const todayOrders = orders.filter((o) => {
        const orderDate = new Date(o.orderDate);
        const today = new Date();
        return orderDate.toDateString() === today.toDateString();
      }).length;

      const totalRevenue = orders
        .filter((o) => o.status === "DELIVERED")
        .reduce((sum, o) => sum + (o.totalAmount || 0), 0);

      setStats({
        totalProducts: products.length,
        activeProducts,
        pendingOrders,
        expiringSoon,
        totalRevenue,
        todayOrders,
      });

      setRecentProducts(products.slice(0, 5));
      setRecentOrders(orders.slice(0, 5));
    } catch (error) {
      console.error("Lỗi tải dữ liệu:", error);
    } finally {
      setLoading(false);
    }
  };

  const getStatusBadge = (status) => {
    const config = {
      ACTIVE: {
        label: "Đang bán",
        className: "bg-green-100 text-green-700",
        icon: "✅",
      },
      PENDING_APPROVAL: {
        label: "Chờ duyệt",
        className: "bg-yellow-100 text-yellow-700",
        icon: "⏳",
      },
      PENDING: {
        label: "Chờ xác nhận",
        className: "bg-yellow-100 text-yellow-700",
        icon: "⏳",
      },
      CONFIRMED: {
        label: "Đã xác nhận",
        className: "bg-blue-100 text-blue-700",
        icon: "👨‍🍳",
      },
    };
    const item = config[status] || {
      label: status,
      className: "bg-gray-100 text-gray-700",
      icon: "●",
    };
    return (
      <span
        className={`px-2 py-1 rounded-full text-xs font-bold ${item.className}`}
      >
        {item.icon} {item.label}
      </span>
    );
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
      <HeaderSeller user={user} />
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        {/* Stats Grid */}
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6 mb-8">
          <div
            className="bg-white rounded-2xl shadow-sm border border-gray-100 p-6 hover:shadow-lg transition-all hover:-translate-y-1 cursor-pointer"
            onClick={() => navigate("/seller/products")}
          >
            <div className="flex items-center justify-between mb-4">
              <div className="w-14 h-14 rounded-full bg-gradient-to-br from-blue-100 to-blue-200 flex items-center justify-center text-3xl">
                📦
              </div>
              <span className="text-xs font-bold text-blue-600 bg-blue-50 px-3 py-1 rounded-full">
                Tổng số
              </span>
            </div>
            <p className="text-[#334155] text-sm font-medium mb-1">Sản phẩm</p>
            <p className="text-4xl font-bold text-[#0f172a]">
              {stats.totalProducts}
            </p>
            <p className="text-xs text-green-600 mt-2 flex items-center gap-1">
              <span>✅</span> {stats.activeProducts} đang bán
            </p>
          </div>

          <div
            className="bg-white rounded-2xl shadow-sm border border-gray-100 p-6 hover:shadow-lg transition-all hover:-translate-y-1 cursor-pointer"
            onClick={() => navigate("/seller/orders")}
          >
            <div className="flex items-center justify-between mb-4">
              <div className="w-14 h-14 rounded-full bg-gradient-to-br from-yellow-100 to-amber-200 flex items-center justify-center text-3xl">
                ⏳
              </div>
              {stats.pendingOrders > 0 && (
                <span className="text-xs font-bold text-white bg-red-500 px-3 py-1 rounded-full animate-pulse">
                  Cần xử lý!
                </span>
              )}
            </div>
            <p className="text-[#334155] text-sm font-medium mb-1">
              Đơn chờ duyệt
            </p>
            <p className="text-4xl font-bold text-[#0f172a]">
              {stats.pendingOrders}
            </p>
            <p className="text-xs text-[#334155] mt-2 flex items-center gap-1">
              <span>📅</span> {stats.todayOrders} đơn hôm nay
            </p>
          </div>

          <div className="bg-white rounded-2xl shadow-sm border border-gray-100 p-6 hover:shadow-lg transition-all hover:-translate-y-1">
            <div className="flex items-center justify-between mb-4">
              <div className="w-14 h-14 rounded-full bg-gradient-to-br from-orange-100 to-red-200 flex items-center justify-center text-3xl">
                ⚠️
              </div>
              {stats.expiringSoon > 0 && (
                <span className="text-xs font-bold text-orange-600 bg-orange-50 px-3 py-1 rounded-full">
                  Chú ý!
                </span>
              )}
            </div>
            <p className="text-[#334155] text-sm font-medium mb-1">
              Sắp hết hạn
            </p>
            <p className="text-4xl font-bold text-[#0f172a]">
              {stats.expiringSoon}
            </p>
            <p className="text-xs text-orange-600 mt-2 flex items-center gap-1">
              <span>⏰</span> Còn ≤ 3 ngày
            </p>
          </div>
        </div>

        {/* Revenue Card */}
        <div className="bg-gradient-to-r from-[#FF6B6B] via-[#FF8E53] to-[#FFC75F] rounded-2xl shadow-lg p-8 mb-8 text-white">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-white/80 text-sm font-medium mb-2 flex items-center gap-2">
                <span className="text-2xl">💰</span>
                Tổng doanh thu
              </p>
              <p className="text-5xl font-bold">
                {formatPrice(stats.totalRevenue)}
              </p>
              <p className="text-white/80 text-sm mt-3">
                Từ các đơn hàng đã giao thành công
              </p>
            </div>
            <div className="hidden md:block w-32 h-32 bg-white/10 backdrop-blur-sm rounded-full flex items-center justify-center">
              <span className="text-6xl">📈</span>
            </div>
          </div>
        </div>

        {/* Two Column Layout */}
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
          {/* Recent Products */}
          <div className="bg-white rounded-2xl shadow-sm border border-gray-100 overflow-hidden">
            <div className="px-6 py-4 bg-gradient-to-r from-orange-50 to-amber-50 border-b border-gray-100 flex justify-between items-center">
              <h3 className="text-lg font-bold text-[#0f172a] flex items-center gap-2">
                <span className="text-xl">📦</span>
                Sản phẩm gần đây
              </h3>
              <button
                onClick={() => navigate("/seller/products")}
                className="text-sm text-[#FF6B6B] font-semibold hover:underline"
              >
                Xem tất cả →
              </button>
            </div>
            <div className="p-6">
              {recentProducts.length === 0 ? (
                <div className="text-center py-8 text-[#334155]">
                  <span className="text-4xl block mb-2">📭</span>
                  Chưa có sản phẩm nào
                </div>
              ) : (
                <div className="space-y-3">
                  {recentProducts.map((product) => (
                    <div
                      key={product.productId}
                      className="flex items-center justify-between p-3 rounded-xl bg-gray-50 hover:bg-orange-50 transition cursor-pointer"
                      onClick={() => navigate("/seller/products")}
                    >
                      <div className="flex-1">
                        <p className="font-semibold text-[#0f172a] text-sm">
                          {product.name}
                        </p>
                        <div className="flex items-center gap-2 mt-1">
                          <span className="text-xs text-[#334155]">
                            SL: {product.quantity}
                          </span>
                          <span className="text-xs text-[#334155]">•</span>
                          <span className="text-xs text-[#FF6B6B] font-bold">
                            {formatPrice(product.salePrice)}
                          </span>
                        </div>
                      </div>
                      {getStatusBadge(product.status)}
                    </div>
                  ))}
                </div>
              )}
            </div>
          </div>

          {/* Recent Orders */}
          <div className="bg-white rounded-2xl shadow-sm border border-gray-100 overflow-hidden">
            <div className="px-6 py-4 bg-gradient-to-r from-orange-50 to-amber-50 border-b border-gray-100 flex justify-between items-center">
              <h3 className="text-lg font-bold text-[#0f172a] flex items-center gap-2">
                <span className="text-xl">📄</span>
                Đơn hàng gần đây
              </h3>
              <button
                onClick={() => navigate("/seller/orders")}
                className="text-sm text-[#FF6B6B] font-semibold hover:underline"
              >
                Xem tất cả →
              </button>
            </div>
            <div className="p-6">
              {recentOrders.length === 0 ? (
                <div className="text-center py-8 text-[#334155]">
                  <span className="text-4xl block mb-2">📭</span>
                  Chưa có đơn hàng nào
                </div>
              ) : (
                <div className="space-y-3">
                  {recentOrders.map((order) => (
                    <div
                      key={order.orderId}
                      className="flex items-center justify-between p-3 rounded-xl bg-gray-50 hover:bg-orange-50 transition cursor-pointer"
                      onClick={() => navigate("/seller/orders")}
                    >
                      <div className="flex-1">
                        <p className="font-semibold text-[#0f172a] text-sm">
                          Đơn #{order.orderId}
                        </p>
                        <div className="flex items-center gap-2 mt-1">
                          <span className="text-xs text-[#334155]">
                            {order.buyerName}
                          </span>
                          <span className="text-xs text-[#334155]">•</span>
                          <span className="text-xs text-[#FF6B6B] font-bold">
                            {formatPrice(order.totalAmount)}
                          </span>
                        </div>
                      </div>
                      {getStatusBadge(order.status)}
                    </div>
                  ))}
                </div>
              )}
            </div>
          </div>
        </div>

        {/* Quick Actions */}
        <div className="mt-8 bg-white rounded-2xl shadow-sm border border-gray-100 p-6">
          <h3 className="text-lg font-bold text-[#0f172a] mb-4 flex items-center gap-2">
            <span className="text-xl">⚡</span>
            Thao tác nhanh
          </h3>
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
            <button
              onClick={() => navigate("/seller/products")}
              className="p-4 rounded-xl bg-gradient-to-br from-blue-50 to-blue-100 border-2 border-blue-200 hover:border-blue-300 transition-all text-left group"
            >
              <span className="text-3xl block mb-2 group-hover:scale-110 transition-transform">
                ➕
              </span>
              <p className="font-bold text-[#0f172a] text-sm">Thêm sản phẩm</p>
              <p className="text-xs text-[#334155] mt-1">
                Đăng bán thực phẩm mới
              </p>
            </button>
            <button
              onClick={() => navigate("/seller/orders")}
              className="p-4 rounded-xl bg-gradient-to-br from-yellow-50 to-amber-100 border-2 border-yellow-200 hover:border-yellow-300 transition-all text-left group"
            >
              <span className="text-3xl block mb-2 group-hover:scale-110 transition-transform">
                📋
              </span>
              <p className="font-bold text-[#0f172a] text-sm">Xem đơn hàng</p>
              <p className="text-xs text-[#334155] mt-1">
                Quản lý đơn đặt hàng
              </p>
            </button>
            <button
              onClick={() => navigate("/seller/settings")}
              className="p-4 rounded-xl bg-gradient-to-br from-purple-50 to-purple-100 border-2 border-purple-200 hover:border-purple-300 transition-all text-left group"
            >
              <span className="text-3xl block mb-2 group-hover:scale-110 transition-transform">
                ⚙️
              </span>
              <p className="font-bold text-[#0f172a] text-sm">Cài đặt shop</p>
              <p className="text-xs text-[#334155] mt-1">Cập nhật thông tin</p>
            </button>
            <button
              onClick={() => window.location.reload()}
              className="p-4 rounded-xl bg-gradient-to-br from-green-50 to-emerald-100 border-2 border-green-200 hover:border-green-300 transition-all text-left group"
            >
              <span className="text-3xl block mb-2 group-hover:scale-110 transition-transform">
                🔄
              </span>
              <p className="font-bold text-[#0f172a] text-sm">Làm mới</p>
              <p className="text-xs text-[#334155] mt-1">
                Cập nhật dữ liệu mới
              </p>
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default SellerDashboard;
