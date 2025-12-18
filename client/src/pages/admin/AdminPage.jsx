import { useState, useEffect } from "react";
import { useAuth } from "../../context/AuthContext";
import { useLocation } from "react-router-dom";
import api from "../../services/api";
import UserManagement from "./UserManagement";
import ProductApproval from "./ProductApproval";
import SellerApproval from "./SellerApproval";
import AdminDashboard from "./AdminDashboard";

const AdminPage = () => {
  const { user } = useAuth();
  const location = useLocation();
  const [stats, setStats] = useState({
    totalUsers: 0,
    totalSellers: 0,
    totalBuyers: 0,
    totalShippers: 0,
    pendingSellers: 0,
    pendingProducts: 0,
    bannedUsers: 0,
    totalProducts: 0,
    totalOrders: 0,
    revenue: 0,
  });
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadStats();
  }, []);

  const loadStats = async () => {
    try {
      // Load stats from API (you'll need to create these endpoints)
      const [usersRes, sellersRes, productsRes] = await Promise.all([
        api.get("/admin/users").catch(() => ({ data: [] })),
        api.get("/admin/sellers/pending/all").catch(() => ({ data: [] })),
        api.get("/admin/products/pending/all").catch(() => ({ data: [] })),
      ]);

      const users = usersRes.data || [];
      const sellers = sellersRes.data || [];
      const products = productsRes.data || [];

      setStats({
        totalUsers: users.length,
        totalSellers: users.filter(u => u.role === 'SELLER').length,
        totalBuyers: users.filter(u => u.role === 'BUYER').length,
        totalShippers: users.filter(u => u.role === 'SHIPPER').length,
        pendingSellers: sellers.length,
        pendingProducts: products.length,
        bannedUsers: users.filter(u => u.banned).length,
        totalProducts: 0,
        totalOrders: 0,
        revenue: 0,
      });
    } catch (error) {
      console.error("Error loading stats:", error);
    } finally {
      setLoading(false);
    }
  };



  // Removed renderDashboard - now using separate AdminDashboard component
  
  const oldRenderDashboard = () => (
    <div className="space-y-6 animate-in fade-in duration-300">
      {/* Welcome Banner */}
      <div className="bg-gradient-to-r from-[#FF6B6B] via-[#FF8E53] to-[#FFC75F] rounded-2xl p-8 text-white shadow-xl">
        <div className="flex items-center justify-between">
          <div>
            <h2 className="text-3xl font-bold mb-2">Chào mừng trở lại, {user?.fullName}! 👋</h2>
            <p className="text-white/90">Quản lý hệ thống Food Rescue một cách hiệu quả</p>
          </div>
          <div className="hidden md:block text-6xl">🎯</div>
        </div>
      </div>

      {/* Quick Stats Grid */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        {[
          { label: "Tổng Users", value: stats.totalUsers, icon: "👥", color: "from-blue-500 to-blue-600", change: "+12%" },
          { label: "Sản phẩm", value: stats.totalProducts, icon: "🛍️", color: "from-green-500 to-green-600", change: "+8%" },
          { label: "Đơn hàng", value: stats.totalOrders, icon: "📦", color: "from-purple-500 to-purple-600", change: "+23%" },
          { label: "Doanh thu", value: `${stats.revenue.toLocaleString()}đ`, icon: "💰", color: "from-orange-500 to-orange-600", change: "+15%" },
        ].map((stat, index) => (
          <div key={index} className="bg-white rounded-2xl p-6 shadow-sm border border-gray-100 hover:shadow-lg transition-all duration-300 hover:-translate-y-1">
            <div className="flex items-center justify-between mb-4">
              <div className={`w-14 h-14 rounded-xl bg-gradient-to-r ${stat.color} flex items-center justify-center text-2xl shadow-lg`}>
                {stat.icon}
              </div>
              <span className="text-xs font-semibold text-green-600 bg-green-50 px-2 py-1 rounded-full">
                {stat.change}
              </span>
            </div>
            <p className="text-[#334155] text-sm mb-1">{stat.label}</p>
            <p className="text-3xl font-bold text-[#0f172a]">{stat.value}</p>
          </div>
        ))}
      </div>

      {/* Pending Actions */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        <div 
          onClick={() => setActiveTab("sellers")}
          className="bg-white rounded-2xl p-6 shadow-sm border border-gray-100 hover:shadow-lg transition-all cursor-pointer hover:-translate-y-1"
        >
          <div className="flex items-center gap-4 mb-4">
            <div className="w-12 h-12 rounded-xl bg-yellow-100 flex items-center justify-center text-2xl">
              🏪
            </div>
            <div>
              <p className="text-[#334155] text-sm">Seller chờ duyệt</p>
              <p className="text-2xl font-bold text-yellow-600">{stats.pendingSellers}</p>
            </div>
          </div>
          <div className="flex items-center justify-between text-sm">
            <span className="text-[#334155]">Cần xử lý ngay</span>
            <svg className="w-5 h-5 text-yellow-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 5l7 7-7 7" />
            </svg>
          </div>
        </div>

        <div 
          onClick={() => setActiveTab("products")}
          className="bg-white rounded-2xl p-6 shadow-sm border border-gray-100 hover:shadow-lg transition-all cursor-pointer hover:-translate-y-1"
        >
          <div className="flex items-center gap-4 mb-4">
            <div className="w-12 h-12 rounded-xl bg-orange-100 flex items-center justify-center text-2xl">
              📦
            </div>
            <div>
              <p className="text-[#334155] text-sm">Sản phẩm chờ duyệt</p>
              <p className="text-2xl font-bold text-orange-600">{stats.pendingProducts}</p>
            </div>
          </div>
          <div className="flex items-center justify-between text-sm">
            <span className="text-[#334155]">Cần xử lý ngay</span>
            <svg className="w-5 h-5 text-orange-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 5l7 7-7 7" />
            </svg>
          </div>
        </div>

        <div 
          onClick={() => setActiveTab("users")}
          className="bg-white rounded-2xl p-6 shadow-sm border border-gray-100 hover:shadow-lg transition-all cursor-pointer hover:-translate-y-1"
        >
          <div className="flex items-center gap-4 mb-4">
            <div className="w-12 h-12 rounded-xl bg-red-100 flex items-center justify-center text-2xl">
              🚫
            </div>
            <div>
              <p className="text-[#334155] text-sm">Users bị ban</p>
              <p className="text-2xl font-bold text-red-600">{stats.bannedUsers}</p>
            </div>
          </div>
          <div className="flex items-center justify-between text-sm">
            <span className="text-[#334155]">Xem chi tiết</span>
            <svg className="w-5 h-5 text-red-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 5l7 7-7 7" />
            </svg>
          </div>
        </div>
      </div>

      {/* User Distribution */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <div className="bg-white rounded-2xl p-6 shadow-sm border border-gray-100">
          <h3 className="text-xl font-bold text-[#0f172a] mb-6">Phân bố người dùng</h3>
          <div className="space-y-4">
            {[
              { label: "Buyers", value: stats.totalBuyers, color: "bg-purple-500", icon: "🛍️" },
              { label: "Sellers", value: stats.totalSellers, color: "bg-blue-500", icon: "🏪" },
              { label: "Shippers", value: stats.totalShippers, color: "bg-orange-500", icon: "🚚" },
            ].map((item, index) => {
              const percentage = stats.totalUsers > 0 ? (item.value / stats.totalUsers * 100).toFixed(1) : 0;
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

  const renderContent = () => {
    const path = location.pathname;
    
    if (path === "/admin/dashboard") {
      return <AdminDashboard stats={stats} />;
    } else if (path === "/admin/users") {
      return <UserManagement />;
    } else if (path === "/admin/product-approval") {
      return <ProductApproval onUpdate={loadStats} />;
    } else if (path === "/admin/seller-approval") {
      return <SellerApproval onUpdate={loadStats} />;
    } else {
      return <AdminDashboard stats={stats} />;
    }
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-orange-50 via-amber-50 to-yellow-50 py-8">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        {loading ? (
          <div className="flex items-center justify-center h-64">
            <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-[#FF6B6B]"></div>
          </div>
        ) : (
          renderContent()
        )}
      </div>
    </div>
  );
};

export default AdminPage;
