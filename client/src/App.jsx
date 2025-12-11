import { Route, Routes } from "react-router";
import { AuthProvider } from "./context/AuthContext";
import LoginPage from "./pages/LoginPage";
import RegisterPage from "./pages/RegisterPage";
import AdminDashboard from "./pages/admin/AdminDashboard";
import ABC from "./pages/admin/ABC";
import PrivateRoute from "./components/common/PrivateRoute";
import MainLayout from "./components/layouts/MainLayout";
import HomePage from "./pages/HomePage";
import SellerDashboard from "./pages/seller/SellerDashboard";
import ShipperOrders from "./pages/shipper/ShipperOrders";
import NotFound from "./pages/NotFound";
import CheckoutPage from "./pages/CheckoutPage";

function App() {
  return (
    <>
      {/* Component AppRoutes quản lý tất cả các Routes */}
      <AuthProvider>
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

              <Route path="/checkout" element={<CheckoutPage />} />

              {/* Shipper Routes */}
              <Route path="/shipper/orders" element={<ShipperOrders />} />
            </Route>
          </Route>

          {/* 404 Not Found */}
          <Route path="*" element={<NotFound />} />
        </Routes>
      </AuthProvider>
    </>
  );
}

export default App;
