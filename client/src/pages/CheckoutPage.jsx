import { useState, useEffect } from "react";
import { useAuth } from "../context/AuthContext";
import { useNavigate } from "react-router-dom";
import { useCheckout } from "../hooks/useCheckout";
import LoadingSpinner from "../components/common/LoadingSpinner";
import EmptyState from "../components/common/EmptyState";
import CheckoutForm from "../components/checkout/CheckoutForm";
import OrderSummary from "../components/checkout/OrderSummary";
import CheckoutSuccess from "../components/checkout/CheckoutSuccess";

const CheckoutPage = () => {
  const { user } = useAuth();
  const navigate = useNavigate();
  const [cartItems, setCartItems] = useState([]);

  useEffect(() => {
    try {
      const cart = JSON.parse(localStorage.getItem("cart") || "[]");
      setCartItems(cart);
    } catch (e) {
      setCartItems([]);
    }
  }, []);

  const { loading, error, fieldErrors, successData, totals, submitCheckout, clearFieldError } = useCheckout(cartItems);

  const [formData, setFormData] = useState({
    recipientName: user?.fullName || "",
    recipientPhone: user?.phoneNumber || "",
    street: user?.address || "",
    ward: "",
    district: "",
    city: user?.city || "",
    paymentMethod: "COD",
    note: "",
  });

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData({ ...formData, [name]: value });
    clearFieldError(name);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    await submitCheckout(formData);
  };

  if (!user) {
    window.location.href = "/login";
    return null;
  }

  if (successData) {
    return (
      <CheckoutSuccess
        orderId={successData.orderId}
        total={successData.total}
        paymentMethod={successData.paymentMethod}
      />
    );
  }

  if (loading) {
    return (
      <div className="min-h-screen bg-gradient-to-br from-orange-50 via-amber-50 to-yellow-50 flex items-center justify-center">
        <LoadingSpinner />
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gradient-to-br from-orange-50 via-amber-50 to-yellow-50">
      <header className="bg-gradient-to-r from-[#FF6B6B] via-[#FF8E53] to-[#FFC75F] shadow-lg">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex items-center justify-between h-16">
            <button
              onClick={() => navigate(-1)}
              className="flex items-center gap-2 text-white hover:text-white/80 transition-colors"
            >
              <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 19l-7-7 7-7" />
              </svg>
              Quay lại
            </button>
            <h1 className="text-xl font-bold text-white">💳 Thanh toán</h1>
            <div className="w-20"></div>
          </div>
        </div>
      </header>

      <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {cartItems.length === 0 ? (
          <EmptyState
            icon="⚠️"
            title="Giỏ hàng trống"
            description="Bạn cần thêm sản phẩm vào giỏ hàng trước khi thanh toán"
            actionLabel="🛍️ Khám phá sản phẩm"
            onAction={() => navigate("/products")}
          />
        ) : (
          <form onSubmit={handleSubmit} className="grid grid-cols-1 lg:grid-cols-3 gap-8">
            <CheckoutForm
              formData={formData}
              onChange={handleChange}
              fieldErrors={fieldErrors}
              loading={loading}
            />
            <OrderSummary
              cartItems={cartItems}
              totals={totals}
              error={error}
              loading={loading}
              onSubmit={handleSubmit}
            />
          </form>
        )}
      </main>
    </div>
  );
};

export default CheckoutPage;
