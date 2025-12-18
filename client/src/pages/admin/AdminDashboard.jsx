import { useAuth } from "../../context/AuthContext";
import { Link } from "react-router-dom";
import { useState, useEffect } from "react";

const AdminDashboard = ({ stats }) => {
  const { user } = useAuth();
  const [currentTime, setCurrentTime] = useState(new Date());

  useEffect(() => {
    const timer = setInterval(() => setCurrentTime(new Date()), 1000);
    return () => clearInterval(timer);
  }, []);

  const formatTime = (date) => {
    return date.toLocaleTimeString('vi-VN', { hour: '2-digit', minute: '2-digit', second: '2-digit' });
  };

  const formatDate = (date) => {
    return date.toLocaleDateString('vi-VN', { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' });
  };

  // Tính toán alerts
  const totalPending = (stats?.pendingSellers || 0) + (stats?.pendingProducts || 0);
  const hasUrgentActions = totalPending > 0;

  return (
    <div className="space-y-6 animate-in fade-in duration-300">
      {/* Header với Time & Alerts */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Welcome Card */}
        <div className="lg:col-span-2 bg-gradient-to-r from-[#FF6B6B] via-[#FF8E53] to-[#FFC75F] rounded-2xl p-8 text-white shadow-xl relative overflow-hidden">
          <div className="absolute top-0 right-0 w-64 h-64 bg-white/10 rounded-full -mr-32 -mt-32"></div>
          <div className="absolute bottom-0 left-0 w-48 h-48 bg-white/10 rounded-full -ml-24 -mb-24"></div>
          <div className="relative z-10">
            <div className="flex items-start justify-between mb-4">
              <div>
                <p className="text-white/80 text-sm font-medium mb-1">👋 Xin chào,</p>
                <h2 className="text-3xl font-bold mb-2">{user?.fullName}</h2>
                <p className="text-white/90 text-sm">Quản trị viên hệ thống Food Rescue</p>
              </div>
              <div className="text-right">
                <div className="text-2xl font-bold">{formatTime(currentTime)}</div>
                <div className="text-sm text-white/80">{formatDate(currentTime)}</div>
              </div>
            </div>
            
            {/* Quick Status */}
            <div className="mt-6 flex items-center gap-4">
              <div className="flex items-center gap-2 bg-white/20 backdrop-blur-sm px-4 py-2 rounded-full">
                <div className="w-2 h-2 bg-green-400 rounded-full animate-pulse"></div>
                <span className="text-sm font-medium">Hệ thống hoạt động tốt</span>
              </div>
              {hasUrgentActions && (
                <div className="flex items-center gap-2 bg-red-500/90 backdrop-blur-sm px-4 py-2 rounded-full animate-pulse">
                  <span className="text-sm font-bold">⚠️ {totalPending} yêu cầu cần xử lý</span>
                </div>
              )}
            </div>
          </div>
        </div>

        {/* Quick Actions Card */}
        <div className="bg-white rounded-2xl p-6 shadow-sm border border-gray-100">
          <h3 className="text-lg font-bold text-[#0f172a] mb-4 flex items-center gap-2">
            ⚡ Thao tác nhanh
          </h3>
          <div className="space-y-3">
            <Link 
              to="/admin/users"
              className="flex items-center justify-between p-3 bg-blue-50 hover:bg-blue-100 rounded-xl transition-all group"
            >
              <div className="flex items-center gap-3">
                <div className="w-10 h-10 bg-blue-500 rounded-lg flex items-center justify-center text-white">
                  👥
                </div>
                <span className="font-semibold text-gray-800">Quản lý Users</span>
              </div>
              <svg className="w-5 h-5 text-blue-500 group-hover:translate-x-1 transition-transform" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 5l7 7-7 7" />
              </svg>
            </Link>
            
            <Link 
              to="/admin/seller-approval"
              className="flex items-center justify-between p-3 bg-yellow-50 hover:bg-yellow-100 rounded-xl transition-all group"
            >
              <div className="flex items-center gap-3">
                <div className="w-10 h-10 bg-yellow-500 rounded-lg flex items-center justify-center text-white">
                  🏪
                </div>
                <span className="font-semibold text-gray-800">Duyệt Sellers</span>
              </div>
              {stats?.pendingSellers > 0 && (
                <span className="bg-yellow-500 text-white text-xs font-bold px-2 py-1 rounded-full">
                  {stats.pendingSellers}
                </span>
              )}
            </Link>
            
            <Link 
              to="/admin/product-approval"
              className="flex items-center justify-between p-3 bg-orange-50 hover:bg-orange-100 rounded-xl transition-all group"
            >
              <div className="flex items-center gap-3">
                <div className="w-10 h-10 bg-orange-500 rounded-lg flex items-center justify-center text-white">
                  📦
                </div>
                <span className="font-semibold text-gray-800">Duyệt Products</span>
              </div>
              {stats?.pendingProducts > 0 && (
                <span className="bg-orange-500 text-white text-xs font-bold px-2 py-1 rounded-full">
                  {stats.pendingProducts}
                </span>
              )}
            </Link>
          </div>
        </div>
      </div>

      {/* Stats Overview - Redesigned */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        <div className="bg-white rounded-2xl p-6 shadow-sm border-2 border-blue-100 hover:shadow-lg transition-all duration-300 hover:-translate-y-1 group">
          <div className="flex items-center justify-between mb-4">
            <div className="w-14 h-14 rounded-xl bg-gradient-to-br from-blue-500 to-blue-600 flex items-center justify-center text-2xl shadow-lg group-hover:scale-110 transition-transform">
              👥
            </div>
            <div className="text-right">
              <p className="text-xs text-gray-500 uppercase font-semibold">Tổng Users</p>
              <p className="text-3xl font-bold text-[#0f172a]">{stats?.totalUsers || 0}</p>
            </div>
          </div>
          <div className="flex items-center justify-between text-xs">
            <span className="text-gray-500">Buyers: {stats?.totalBuyers || 0}</span>
            <span className="text-gray-500">Sellers: {stats?.totalSellers || 0}</span>
            <span className="text-gray-500">Shippers: {stats?.totalShippers || 0}</span>
          </div>
        </div>

        <div className="bg-white rounded-2xl p-6 shadow-sm border-2 border-green-100 hover:shadow-lg transition-all duration-300 hover:-translate-y-1 group">
          <div className="flex items-center justify-between mb-4">
            <div className="w-14 h-14 rounded-xl bg-gradient-to-br from-green-500 to-green-600 flex items-center justify-center text-2xl shadow-lg group-hover:scale-110 transition-transform">
              🛍️
            </div>
            <div className="text-right">
              <p className="text-xs text-gray-500 uppercase font-semibold">Sản phẩm</p>
              <p className="text-3xl font-bold text-[#0f172a]">{stats?.totalProducts || 0}</p>
            </div>
          </div>
          <div className="flex items-center gap-2">
            <span className="text-xs font-semibold text-green-600 bg-green-50 px-2 py-1 rounded-full">
              ✓ Active
            </span>
            <span className="text-xs text-gray-500">Đang bán</span>
          </div>
        </div>

        <div className="bg-white rounded-2xl p-6 shadow-sm border-2 border-purple-100 hover:shadow-lg transition-all duration-300 hover:-translate-y-1 group">
          <div className="flex items-center justify-between mb-4">
            <div className="w-14 h-14 rounded-xl bg-gradient-to-br from-purple-500 to-purple-600 flex items-center justify-center text-2xl shadow-lg group-hover:scale-110 transition-transform">
              📦
            </div>
            <div className="text-right">
              <p className="text-xs text-gray-500 uppercase font-semibold">Đơn hàng</p>
              <p className="text-3xl font-bold text-[#0f172a]">{stats?.totalOrders || 0}</p>
            </div>
          </div>
          <div className="flex items-center gap-2">
            <span className="text-xs font-semibold text-purple-600 bg-purple-50 px-2 py-1 rounded-full">
              +23% ↑
            </span>
            <span className="text-xs text-gray-500">So với tháng trước</span>
          </div>
        </div>

        <div className="bg-white rounded-2xl p-6 shadow-sm border-2 border-orange-100 hover:shadow-lg transition-all duration-300 hover:-translate-y-1 group">
          <div className="flex items-center justify-between mb-4">
            <div className="w-14 h-14 rounded-xl bg-gradient-to-br from-orange-500 to-orange-600 flex items-center justify-center text-2xl shadow-lg group-hover:scale-110 transition-transform">
              💰
            </div>
            <div className="text-right">
              <p className="text-xs text-gray-500 uppercase font-semibold">Doanh thu</p>
              <p className="text-2xl font-bold text-[#0f172a]">{(stats?.revenue || 0).toLocaleString()}đ</p>
            </div>
          </div>
          <div className="flex items-center gap-2">
            <span className="text-xs font-semibold text-orange-600 bg-orange-50 px-2 py-1 rounded-full">
              +15% ↑
            </span>
            <span className="text-xs text-gray-500">Tăng trưởng tốt</span>
          </div>
        </div>
      </div>

      {/* Alert Section - Pending Actions */}
      {hasUrgentActions && (
        <div className="bg-gradient-to-r from-red-50 to-orange-50 border-2 border-red-200 rounded-2xl p-6">
          <div className="flex items-start gap-4">
            <div className="w-12 h-12 bg-red-500 rounded-xl flex items-center justify-center text-2xl animate-bounce">
              ⚠️
            </div>
            <div className="flex-1">
              <h3 className="text-xl font-bold text-red-900 mb-2">Cảnh báo: Có yêu cầu cần xử lý!</h3>
              <p className="text-red-700 mb-4">Bạn có <span className="font-bold">{totalPending} yêu cầu</span> đang chờ phê duyệt. Vui lòng xử lý sớm để đảm bảo trải nghiệm người dùng.</p>
              <div className="flex flex-wrap gap-3">
                {stats?.pendingSellers > 0 && (
                  <Link 
                    to="/admin/seller-approval"
                    className="inline-flex items-center gap-2 bg-yellow-500 hover:bg-yellow-600 text-white px-4 py-2 rounded-lg font-semibold transition-all shadow-md hover:shadow-lg"
                  >
                    <span>🏪</span>
                    <span>Duyệt {stats.pendingSellers} Seller</span>
                  </Link>
                )}
                {stats?.pendingProducts > 0 && (
                  <Link 
                    to="/admin/product-approval"
                    className="inline-flex items-center gap-2 bg-orange-500 hover:bg-orange-600 text-white px-4 py-2 rounded-lg font-semibold transition-all shadow-md hover:shadow-lg"
                  >
                    <span>📦</span>
                    <span>Duyệt {stats.pendingProducts} Product</span>
                  </Link>
                )}
              </div>
            </div>
          </div>
        </div>
      )}

      {/* System Health Cards */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        <div className="bg-white rounded-2xl p-6 shadow-sm border border-gray-100 hover:shadow-lg transition-all">
          <div className="flex items-center justify-between mb-4">
            <div className="flex items-center gap-3">
              <div className="w-10 h-10 rounded-lg bg-green-100 flex items-center justify-center">
                <div className="w-3 h-3 bg-green-500 rounded-full animate-pulse"></div>
              </div>
              <div>
                <p className="text-sm text-gray-500">Trạng thái hệ thống</p>
                <p className="font-bold text-green-600">Hoạt động tốt</p>
              </div>
            </div>
          </div>
          <div className="space-y-2 text-sm">
            <div className="flex justify-between">
              <span className="text-gray-600">Server</span>
              <span className="text-green-600 font-semibold">✓ Online</span>
            </div>
            <div className="flex justify-between">
              <span className="text-gray-600">Database</span>
              <span className="text-green-600 font-semibold">✓ Connected</span>
            </div>
          </div>
        </div>

        <div className="bg-white rounded-2xl p-6 shadow-sm border border-gray-100 hover:shadow-lg transition-all">
          <div className="flex items-center gap-3 mb-4">
            <div className="w-10 h-10 rounded-lg bg-blue-100 flex items-center justify-center text-xl">
              📊
            </div>
            <div>
              <p className="text-sm text-gray-500">Hoạt động hôm nay</p>
              <p className="font-bold text-blue-600">Bình thường</p>
            </div>
          </div>
          <div className="space-y-2 text-sm">
            <div className="flex justify-between">
              <span className="text-gray-600">Đăng ký mới</span>
              <span className="font-semibold text-gray-800">+12</span>
            </div>
            <div className="flex justify-between">
              <span className="text-gray-600">Đơn hàng mới</span>
              <span className="font-semibold text-gray-800">+45</span>
            </div>
          </div>
        </div>

        <div className="bg-white rounded-2xl p-6 shadow-sm border border-gray-100 hover:shadow-lg transition-all">
          <div className="flex items-center gap-3 mb-4">
            <div className="w-10 h-10 rounded-lg bg-purple-100 flex items-center justify-center text-xl">
              🎯
            </div>
            <div>
              <p className="text-sm text-gray-500">Mục tiêu tháng này</p>
              <p className="font-bold text-purple-600">Đang đạt</p>
            </div>
          </div>
          <div className="space-y-2">
            <div className="flex justify-between text-sm mb-1">
              <span className="text-gray-600">Tiến độ</span>
              <span className="font-semibold text-gray-800">75%</span>
            </div>
            <div className="w-full bg-gray-100 rounded-full h-2">
              <div className="bg-gradient-to-r from-purple-500 to-purple-600 h-2 rounded-full" style={{ width: '75%' }}></div>
            </div>
          </div>
        </div>
      </div>

      {/* User Distribution */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <div className="bg-white rounded-2xl p-6 shadow-sm border border-gray-100">
          <h3 className="text-xl font-bold text-[#0f172a] mb-6">Phân bố người dùng</h3>
          <div className="space-y-4">
            {[
              { label: "Buyers", value: stats?.totalBuyers || 0, color: "bg-purple-500", icon: "🛍️" },
              { label: "Sellers", value: stats?.totalSellers || 0, color: "bg-blue-500", icon: "🏪" },
              { label: "Shippers", value: stats?.totalShippers || 0, color: "bg-orange-500", icon: "🚚" },
            ].map((item, index) => {
              const totalUsers = stats?.totalUsers || 0;
              const percentage = totalUsers > 0 ? (item.value / totalUsers * 100).toFixed(1) : 0;
              return (
                <div key={index}>
                  <div className="flex items-center justify-between mb-2">
                    <div className="flex items-center gap-2">
                      <span className="text-lg">{item.icon}</span>
                      <span className="text-sm font-medium text-[#334155]">{item.label}</span>
                    </div>
                    <span className="text-sm font-bold text-[#0f172a]">{item.value} ({percentage}%)</span>
                  </div>
                  <div className="w-full bg-gray-100 rounded-full h-2.5">
                    <div className={`${item.color} h-2.5 rounded-full transition-all duration-500`} style={{ width: `${percentage}%` }}></div>
                  </div>
                </div>
              );
            })}
          </div>
        </div>

        <div className="bg-white rounded-2xl p-6 shadow-sm border border-gray-100">
          <h3 className="text-xl font-bold text-[#0f172a] mb-6">Hoạt động gần đây</h3>
          <div className="space-y-4">
            {[
              { action: "Duyệt sản phẩm", user: "Admin", time: "5 phút trước", icon: "✅", color: "text-green-600" },
              { action: "Ban user", user: "Admin", time: "1 giờ trước", icon: "🚫", color: "text-red-600" },
              { action: "Duyệt seller", user: "Admin", time: "2 giờ trước", icon: "✅", color: "text-green-600" },
            ].map((activity, index) => (
              <div key={index} className="flex items-start gap-3 p-3 rounded-lg hover:bg-gray-50 transition-colors">
                <span className="text-2xl">{activity.icon}</span>
                <div className="flex-1">
                  <p className="text-sm font-medium text-[#0f172a]">{activity.action}</p>
                  <p className="text-xs text-[#334155]">bởi {activity.user} • {activity.time}</p>
                </div>
              </div>
            ))}
          </div>
        </div>
      </div>
    </div>
  );
};

export default AdminDashboard;
