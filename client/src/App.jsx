import { Route, Routes } from "react-router";
import { AuthProvider } from "./context/AuthContext";
import LoginPage from "./pages/Auth/LoginPage";
import RegisterPage from "./pages/Auth/RegisterPage";
import ForgotPassword from "./pages/Auth/ForgotPassword";
import ResetPassword from "./pages/Auth/ResetPassword";
import AdminDashboard from "./pages/admin/AdminDashboard";
import PrivateRoute from "./components/common/PrivateRoute";
import MainLayout from "./components/layouts/MainLayout";
import HomePage from "./pages/HomePage";
import SellerDashboard from "./pages/seller/SellerDashboard";
import ShipperOrders from "./pages/shipper/ShipperOrders";
import NotFound from "./pages/NotFound";
import CheckoutPage from "./pages/CheckoutPage";
import { CartProvider } from "./context/CartContext";
import ProductPage from "./pages/ProductPage";
import ProductList from "./pages/ProductList";

function App() {
  return (
    <>
      {/* Component AppRoutes quản lý tất cả các Routes */}
      <AuthProvider>
        <CartProvider>
          <Routes>
            {/* Public Routes */}
            <Route path="/login" element={<LoginPage />} />
            <Route path="/register" element={<RegisterPage />} />
            <Route path="/forgot-password" element={<ForgotPassword />} />
            <Route path="/reset-password" element={<ResetPassword />} />

            {/* Protected Routes */}
            <Route element={<PrivateRoute />}>
              <Route element={<MainLayout />}>
                {/* Buyer Routes */}
                <Route path="/" element={<HomePage />} />

                {/* Admin Routes */}
                <Route path="/admin/dashboard" element={<AdminDashboard />} />

                {/* Seller Routes */}
                <Route path="/seller/dashboard" element={<SellerDashboard />} />

                {/* Checkout Routes */}
                <Route path="/checkout" element={<CheckoutPage />} />
                <Route path="/products" element={<ProductList />} />
                <Route path="/product/:id" element={<ProductPage />} />

                {/* Shipper Routes */}
                <Route path="/shipper/orders" element={<ShipperOrders />} />
              </Route>
            </Route>

            {/* 404 Not Found */}
            <Route path="*" element={<NotFound />} />
          </Routes>
        </CartProvider>
      </AuthProvider>
    </>
  );
}

export default App;
