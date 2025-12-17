import { Route, Routes } from "react-router";
import { ToastContainer } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import { AuthProvider } from "./context/AuthContext";
import LoginPage from "./pages/LoginPage";
import RegisterPage from "./pages/RegisterPage";
import AdminDashboard from "./pages/admin/AdminDashboard";
import PrivateRoute from "./components/common/PrivateRoute";
import MainLayout from "./components/layouts/MainLayout";
import HomePage from "./pages/HomePage";
import BuyerHomePage from "./pages/Buyer/homePage";
import ProductDetailPage from "./pages/Buyer/ProductDetailPage";
import ShoppingCartPage from "./pages/Buyer/ShoppingCartPage";
import OrderSuccessPage from "./pages/Buyer/OrderSuccessPage";
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
              <Route path="/products" element={<BuyerHomePage />} />
              <Route path="/products/:id" element={<ProductDetailPage />} />
              <Route path="/cart" element={<ShoppingCartPage />} />
              <Route path="/order-success" element={<OrderSuccessPage />} />

              {/* Admin Routes */}
              <Route path="/admin/dashboard" element={<AdminDashboard />} />

              {/* Seller Routes */}
              <Route path="/seller/dashboard" element={<SellerDashboard />} />

              {/* Checkout Routes */}
              <Route path="/checkout" element={<CheckoutPage />} />

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
