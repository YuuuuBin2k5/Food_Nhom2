import { useEffect } from "react";
import { useLocation } from "react-router-dom";
import { useAdmin } from "../../hooks/useAdmin";
import UserManagement from "./UserManagement";
import ProductApproval from "./ProductApproval";
import SellerApproval from "./SellerApproval";
import AdminDashboard from "./AdminDashboard";

const AdminPage = () => {
  const location = useLocation();
  const { stats, loading, loadStats } = useAdmin();

  useEffect(() => {
    loadStats();
  }, []);

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
