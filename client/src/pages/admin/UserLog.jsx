const UserLog = ({ stats }) => {
  return (
    <div className="animate-in fade-in duration-300">
      <h2 className="text-2xl font-bold text-gray-900 mb-6">Tổng quan hệ thống</h2>

      {/* STATS CARDS */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
        <div className="bg-white rounded-xl shadow-sm border border-gray-200 p-6">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-gray-600 text-sm mb-1">Tổng Users</p>
              <p className="text-3xl font-bold text-gray-900">{stats?.totalUsers || 0}</p>
            </div>
            <div className="w-16 h-16 rounded-full bg-gradient-to-r from-blue-500 to-blue-600 flex items-center justify-center text-3xl">
              👥
            </div>
          </div>
        </div>

        <div className="bg-white rounded-xl shadow-sm border border-gray-200 p-6">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-gray-600 text-sm mb-1">Sellers</p>
              <p className="text-3xl font-bold text-gray-900">{stats?.totalSellers || 0}</p>
            </div>
            <div className="w-16 h-16 rounded-full bg-gradient-to-r from-green-500 to-green-600 flex items-center justify-center text-3xl">
              🏪
            </div>
          </div>
        </div>

        <div className="bg-white rounded-xl shadow-sm border border-gray-200 p-6">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-gray-600 text-sm mb-1">Buyers</p>
              <p className="text-3xl font-bold text-gray-900">{stats?.totalBuyers || 0}</p>
            </div>
            <div className="w-16 h-16 rounded-full bg-gradient-to-r from-purple-500 to-purple-600 flex items-center justify-center text-3xl">
              🛍️
            </div>
          </div>
        </div>

        <div className="bg-white rounded-xl shadow-sm border border-gray-200 p-6">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-gray-600 text-sm mb-1">Shippers</p>
              <p className="text-3xl font-bold text-gray-900">{stats?.totalShippers || 0}</p>
            </div>
            <div className="w-16 h-16 rounded-full bg-gradient-to-r from-orange-500 to-orange-600 flex items-center justify-center text-3xl">
              🚚
            </div>
          </div>
        </div>
      </div>

      {/* PENDING APPROVALS */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
        <div className="bg-white rounded-xl shadow-sm border border-gray-200 p-6">
          <div className="flex items-center gap-4">
            <div className="w-12 h-12 rounded-full bg-yellow-100 flex items-center justify-center text-2xl">
              ⏳
            </div>
            <div>
              <p className="text-gray-600 text-sm">Seller chờ duyệt</p>
              <p className="text-2xl font-bold text-yellow-600">{stats?.pendingSellers || 0}</p>
            </div>
          </div>
        </div>

        <div className="bg-white rounded-xl shadow-sm border border-gray-200 p-6">
          <div className="flex items-center gap-4">
            <div className="w-12 h-12 rounded-full bg-yellow-100 flex items-center justify-center text-2xl">
              📦
            </div>
            <div>
              <p className="text-gray-600 text-sm">Sản phẩm chờ duyệt</p>
              <p className="text-2xl font-bold text-yellow-600">{stats?.pendingProducts || 0}</p>
            </div>
          </div>
        </div>

        <div className="bg-white rounded-xl shadow-sm border border-gray-200 p-6">
          <div className="flex items-center gap-4">
            <div className="w-12 h-12 rounded-full bg-red-100 flex items-center justify-center text-2xl">
              🚫
            </div>
            <div>
              <p className="text-gray-600 text-sm">Users bị ban</p>
              <p className="text-2xl font-bold text-red-600">{stats?.bannedUsers || 0}</p>
            </div>
          </div>
        </div>
      </div>

      {/* SYSTEM INFO */}
      <div className="bg-white rounded-xl shadow-sm border border-gray-200 p-6">
        <h3 className="text-xl font-bold text-gray-900 mb-4">Thông tin hệ thống</h3>
        <div className="space-y-3">
          <div className="flex justify-between py-2 border-b">
            <span className="text-gray-600">Tổng số người dùng:</span>
            <span className="font-semibold text-gray-900">{stats?.totalUsers || 0}</span>
          </div>
          <div className="flex justify-between py-2 border-b">
            <span className="text-gray-600">Sellers hoạt động:</span>
            <span className="font-semibold text-gray-900">{stats?.totalSellers || 0}</span>
          </div>
          <div className="flex justify-between py-2 border-b">
            <span className="text-gray-600">Buyers:</span>
            <span className="font-semibold text-gray-900">{stats?.totalBuyers || 0}</span>
          </div>
          <div className="flex justify-between py-2">
            <span className="text-gray-600">Shippers:</span>
            <span className="font-semibold text-gray-900">{stats?.totalShippers || 0}</span>
          </div>
        </div>
      </div>
    </div>
  );
};

export default UserLog;
