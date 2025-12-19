import { useAuth } from "../../context/AuthContext";
import { useNavigate } from "react-router-dom";
import StatsCard from "../../components/admin/StatsCard";
import PendingActionCard from "../../components/admin/PendingActionCard";
import UserDistributionChart from "../../components/admin/UserDistributionChart";

const AdminDashboard = ({ stats }) => {
  const { user } = useAuth();
  const navigate = useNavigate();

  const quickStats = [
    { label: "Tổng Users", value: stats?.totalUsers || 0, icon: "👥", color: "from-blue-500 to-blue-600", change: "+12%" },
    { label: "Sản phẩm", value: stats?.totalProducts || 0, icon: "🛍️", color: "from-green-500 to-green-600", change: "+8%" },
    { label: "Đơn hàng", value: stats?.totalOrders || 0, icon: "📦", color: "from-purple-500 to-purple-600", change: "+23%" },
    { label: "Doanh thu", value: `${(stats?.revenue || 0).toLocaleString()}đ`, icon: "💰", color: "from-orange-500 to-orange-600", change: "+15%" },
  ];

  const pendingActions = [
    {
      title: "Seller chờ duyệt",
      count: stats?.pendingSellers || 0,
      icon: "🏪",
      color: "yellow",
      onClick: () => navigate("/admin/seller-approval")
    },
    {
      title: "Sản phẩm chờ duyệt",
      count: stats?.pendingProducts || 0,
      icon: "📦",
      color: "orange",
      onClick: () => navigate("/admin/product-approval")
    },
    {
      title: "Users bị ban",
      count: stats?.bannedUsers || 0,
      icon: "🚫",
      color: "red",
      onClick: () => navigate("/admin/users")
    },
  ];

  const userDistribution = [
    { label: "Buyers", value: stats?.totalBuyers || 0, color: "bg-purple-500", icon: "🛍️" },
    { label: "Sellers", value: stats?.totalSellers || 0, color: "bg-blue-500", icon: "🏪" },
    { label: "Shippers", value: stats?.totalShippers || 0, color: "bg-orange-500", icon: "🚚" },
  ];

  const recentActivities = [
    { action: "Duyệt sản phẩm", user: "Admin", time: "5 phút trước", icon: "✅", color: "text-green-600" },
    { action: "Ban user", user: "Admin", time: "1 giờ trước", icon: "🚫", color: "text-red-600" },
    { action: "Duyệt seller", user: "Admin", time: "2 giờ trước", icon: "✅", color: "text-green-600" },
  ];

  return (
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
        {quickStats.map((stat, index) => (
          <StatsCard key={index} {...stat} />
        ))}
      </div>

      {/* Pending Actions */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        {pendingActions.map((action, index) => (
          <PendingActionCard key={index} {...action} />
        ))}
      </div>

      {/* User Distribution & Recent Activities */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <UserDistributionChart 
          data={userDistribution} 
          totalUsers={stats?.totalUsers || 0} 
        />

        <div className="bg-white rounded-2xl p-6 shadow-sm border border-gray-100">
          <h3 className="text-xl font-bold text-[#0f172a] mb-6">Hoạt động gần đây</h3>
          <div className="space-y-4">
            {recentActivities.map((activity, index) => (
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
