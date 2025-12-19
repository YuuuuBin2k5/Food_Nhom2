import { Route, Routes } from "react-router";
import { ToastContainer } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import { AuthProvider, useAuth } from "./context/AuthContext";
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
import CheckoutPage from "./pages/CheckoutPage";
import { CartProvider } from "./context/CartContext";
import HomePage from "./pages/HomePage";
import BuyerHomePage from "./pages/Buyer/homePage";
import ProductDetailPage from "./pages/Buyer/ProductDetailPage";
import ShoppingCartPage from "./pages/Buyer/ShoppingCartPage";
import OrderSuccessPage from "./pages/Buyer/OrderSuccessPage";
import OrderHistoryPage from "./pages/Buyer/OrderHistoryPage";
import SellerOrders from "./pages/seller/SellerOrders";
import SellerProducts from "./pages/seller/SellerProducts";
import SellerSettings from "./pages/seller/SellerSettings";
import { NotificationProvider } from "./context/NotificationContext";
import { ThemeProvider } from "./context/ThemeContext";

function AppContent() {
  const { user } = useAuth();

  return (
    <NotificationProvider userId={user?.userId}>
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
            <Route path="/products" element={<BuyerHomePage />} />
            <Route path="/products/:productId" element={<ProductDetailPage />} />
            <Route path="/cart" element={<ShoppingCartPage />} />
            <Route path="/order-success" element={<OrderSuccessPage />} />
            <Route path="/orders" element={<OrderHistoryPage />} />

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
            
            {/* Checkout Routes */}
            <Route path="/checkout" element={<CheckoutPage />} />

            {/* Shipper Routes */}
            <Route path="/shipper/orders" element={<ShipperOrders />} />
          </Route>
        </Route>

        {/* 404 Not Found */}
        <Route path="*" element={<NotFound />} />
      </Routes>
    </NotificationProvider>
  );
}

function App() {
  return (
    <ThemeProvider>
      <AuthProvider>
        <CartProvider>
          <AppContent />
        </CartProvider>
      </AuthProvider>
      <ToastContainer position="top-right" autoClose={3000} />
    </ThemeProvider>
  );
}

export default App;
