import { Route, Routes } from "react-router";
import { ToastContainer } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import { AuthProvider } from "./context/AuthContext";
import LoginPage from "./pages/Auth/LoginPage";
import RegisterPage from "./pages/Auth/RegisterPage";
import ForgotPassword from "./pages/Auth/ForgotPassword";
import ResetPassword from "./pages/Auth/ResetPassword";
import AdminPage from "./pages/admin/AdminPage";
import PrivateRoute from "./components/common/PrivateRoute";
import MainLayout from "./components/layouts/MainLayout";
import SellerDashboard from "./pages/seller/SellerDashboard";
import ShipperOrders from "./pages/shipper/ShipperOrders";
import NotFound from "./pages/NotFound";
import SellerOrders from "./pages/seller/SellerOrders";
import SellerProducts from "./pages/seller/SellerProducts";
import SellerSettings from "./pages/seller/SellerSettings";

function App() {
  return (
    <>
      {/* Component AppRoutes quản lý tất cả các Routes */}
      <AuthProvider>
        <Routes>
          {/* Public Routes */}
          <Route path="/login" element={<LoginPage />} />
          <Route path="/register" element={<RegisterPage />} />
          <Route path="/forgot-password" element={<ForgotPassword />} />
          <Route path="/reset-password" element={<ResetPassword />} />

          {/* Protected Routes */}
          <Route element={<PrivateRoute />}>
            <Route element={<MainLayout />}>

              {/* Admin Routes */}
              <Route path="/admin/dashboard" element={<AdminPage />} />
              <Route path="/admin/users" element={<AdminPage />} />
              <Route path="/admin/seller-approval" element={<AdminPage />} />
              <Route path="/admin/product-approval" element={<AdminPage />} />

              {/* Seller Routes */}
              <Route path="/seller/dashboard" element={<SellerDashboard />} />
              <Route path="/seller/products" element={<SellerProducts />} />
              <Route path="/seller/orders" element={<SellerOrders />} />
              <Route path="/seller/settings" element={<SellerSettings />} />

              {/* Shipper Routes */}
              <Route path="/shipper/orders" element={<ShipperOrders />} />
            </Route>
          </Route>

          {/* 404 Not Found */}
          <Route path="*" element={<NotFound />} />
        </Routes>
      </AuthProvider>
      <ToastContainer position="top-right" autoClose={3000} />
    </>
  );
}

export default App;
