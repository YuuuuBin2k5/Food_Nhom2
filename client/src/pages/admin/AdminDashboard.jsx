import React, { useState, useEffect } from "react";
import { useAuth } from "../../context/AuthContext";
import api from "../../services/api";
import UserManagement from "./UserManagement";
import SellerApproval from "./SellerApproval";
import ProductApproval from "./ProductApproval";
import UserLog from "./UserLog";

const AdminDashboard = () => {
  const { user, logout } = useAuth();
  const [activeTab, setActiveTab] = useState("sellerApproval");
  const [stats, setStats] = useState({
    totalUsers: 0,
    totalSellers: 0,
    totalBuyers: 0,
    totalShippers: 0,
    bannedUsers: 0,
    pendingSellers: 0,
    pendingProducts: 0,
  });

  useEffect(() => {
    loadStats();
  }, []);

  const loadStats = async () => {
    try {
      const res = await api.get("/admin/stats");
      setStats(res.data);
    } catch (err) {
      console.error("Lỗi tải thống kê:", err);
    }
  };

  const SidebarItem = ({ id, label, icon, badge }) => (
    <button
      onClick={() => setActiveTab(id)}
      className={`w-full flex items-center gap-3 px-4 py-3 rounded-lg transition-all text-left relative ${
        activeTab === id
          ? "bg-blue-600 text-white shadow-md border-l-4 border-blue-400"
          : "text-gray-300 hover:bg-gray-700"
      }`}
    >
      <span className="text-xl">{icon}</span>
      <span className="font-medium flex-1">{label}</span>
      {badge > 0 && (
        <span className="px-2 py-0.5 bg-red-500 text-white text-xs font-bold rounded-full">
          {badge}
        </span>
      )}
    </button>
  );

  return (
    <div className="flex h-screen bg-gray-100 font-sans">
      {/* SIDEBAR */}
      <div className="w-64 bg-gray-800 shadow-xl flex flex-col justify-between p-4 fixed h-full z-10">
        <div>
          <div className="flex items-center gap-2 mb-8 px-2">
            <div className="w-10 h-10 bg-blue-600 rounded-lg flex items-center justify-center text-white font-bold text-lg">
              A
            </div>
            <div>
              <h1 className="text-xl font-bold text-white">Admin Panel</h1>
              <p className="text-xs text-gray-400">Food Rescue</p>
            </div>
          </div>
          
          <div className="space-y-2">
            <SidebarItem id="userLog" label="User Log" icon="📊" />
            <SidebarItem 
              id="sellerApproval" 
              label="Duyệt Seller" 
              icon="🏪"
              badge={stats.pendingSellers}
            />
            <SidebarItem 
              id="productApproval" 
              label="Duyệt Product" 
              icon="📦"
              badge={stats.pendingProducts}
            />
            <SidebarItem id="userManagement" label="Ban User" icon="👥" />
          </div>
        </div>

        <div className="border-t border-gray-700 pt-4">
          <div className="mb-4 px-2">
            <p className="text-xs text-gray-400">Đang đăng nhập:</p>
            <p className="font-bold text-white truncate">{user?.fullName || "Admin"}</p>
          </div>
          <button
            onClick={logout}
            className="w-full flex items-center gap-2 text-red-400 bg-red-900/20 px-4 py-2 rounded-lg hover:bg-red-900/40 transition font-medium"
          >
            <span>🚪</span> Đăng xuất
          </button>
        </div>
      </div>

      {/* MAIN CONTENT */}
      <div className="flex-1 ml-64 p-8 overflow-y-auto bg-gray-50">
        {activeTab === "userLog" && <UserLog stats={stats} />}
        {activeTab === "sellerApproval" && <SellerApproval onUpdate={loadStats} />}
        {activeTab === "productApproval" && <ProductApproval onUpdate={loadStats} />}
        {activeTab === "userManagement" && <UserManagement />}
      </div>
    </div>
  );
};

export default AdminDashboard;
