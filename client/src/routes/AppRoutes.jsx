import { Routes, Route } from "react-router-dom";
import LoginPage from "../pages/LoginPage";
import RegisterPage from "../pages/RegisterPage";
import HomePage from "../pages/HomePage";
import NotFound from "../pages/NotFound";
import PrivateRoute from "../components/common/PrivateRoute";
import MainLayout from "../components/layouts/MainLayout";

// Admin pages
import AdminDashboard from "../pages/admin/AdminDashboard";

// Seller pages
import SellerDashboard from "../pages/seller/SellerDashboard";

// Shipper pages
import ShipperOrders from "../pages/shipper/ShipperOrders";

const AppRoutes = () => {
  return (
    <Routes>
      {/* Public Routes */}
      <Route path="/login" element={<LoginPage />} />
      <Route path="/register" element={<RegisterPage />} />

      {/* Protected Routes */}
      <Route element={<PrivateRoute />}>
        <Route element={<MainLayout />}>
          {/* Buyer Routes */}
          <Route path="/" element={<HomePage />} />

          {/* Admin Routes */}
          <Route path="/admin/dashboard" element={<AdminDashboard />} />

          {/* Seller Routes */}
          <Route path="/seller/dashboard" element={<SellerDashboard />} />

          {/* Shipper Routes */}
          <Route path="/shipper/orders" element={<ShipperOrders />} />
        </Route>
      </Route>

      {/* 404 Not Found */}
      <Route path="*" element={<NotFound />} />
    </Routes>
  );
};

export default AppRoutes;
