import { useState, useMemo, useEffect } from "react";
import { useAuth } from "../context/AuthContext";
import { useNavigate } from "react-router-dom";
import api from "../services/api";
import { formatPrice } from "../utils/format";
import { showToast } from "../utils/toast";
import LoadingSpinner from "../components/common/LoadingSpinner";

const CheckoutPage = () => {
  const { user } = useAuth();
  const navigate = useNavigate();

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [successData, setSuccessData] = useState(null);

  // Get cart from localStorage
  const [cartItems, setCartItems] = useState([]);

  useEffect(() => {
    try {
      const cart = JSON.parse(localStorage.getItem("cart") || "[]");
      setCartItems(cart);
    } catch (e) {
      setCartItems([]);
    }
  }, []);

  // Form State
  const [formData, setFormData] = useState({
    recipientName: user?.fullName || "",
    recipientPhone: user?.phoneNumber || "",
    street: user?.address || "",
    ward: "",
    district: "",
    city: user?.city || "",
    useAccountAddress: !!user?.address,
    paymentMethod: "COD",
    note: "",
  });

  // Validation State
  const [fieldErrors, setFieldErrors] = useState({});

  // Calculate totals
  const totals = useMemo(() => {
    const subtotal = cartItems.reduce(
      (sum, item) =>
        sum + (item.product?.salePrice || item.product?.originalPrice || 0) * item.quantity,
      0
    );
    const shipping = subtotal > 0 ? 30000 : 0;
    const total = subtotal + shipping;
    return { subtotal, shipping, total };
  }, [cartItems]);

  // Clear cart function
  const clearCart = () => {
    localStorage.removeItem("cart");
    setCartItems([]);
    
    // Dispatch event to update cart icon
    window.dispatchEvent(new CustomEvent('cartUpdated', {
      detail: { cart: [], count: 0 }
    }));
  };

  // Handle Input Change
  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
    // Clear error khi user gõ
    if (fieldErrors[e.target.name]) {
      setFieldErrors({ ...fieldErrors, [e.target.name]: "" });
    }
  };

  // Redirect to login if not authenticated (checkout requires user)
  if (!user) {
    // avoid rendering sensitive UI for anonymous
    window.location.href = "/login";
    return null;
  }

  // Validate Form
  const validate = () => {
    const errors = {};
    
    // Validate recipient name
    if (!formData.recipientName || !formData.recipientName.trim()) {
      errors.recipientName = "Vui lòng nhập tên người nhận";
    } else if (formData.recipientName.trim().length < 2) {
      errors.recipientName = "Tên phải có ít nhất 2 ký tự";
    } else if (formData.recipientName.trim().length > 100) {
      errors.recipientName = "Tên quá dài (tối đa 100 ký tự)";
    }

    // Validate phone number
    const phone = (formData.recipientPhone || "").replace(/[^0-9+]/g, "");
    const phoneRe = /^\+?[0-9]{9,14}$/;
    if (!phone) {
      errors.recipientPhone = "Vui lòng nhập số điện thoại";
    } else if (!phoneRe.test(phone)) {
      errors.recipientPhone = "Số điện thoại không hợp lệ (9-14 số)";
    }

    // Validate street address
    if (!formData.street || formData.street.trim().length < 6) {
      errors.street = "Vui lòng nhập địa chỉ chi tiết (tối thiểu 6 ký tự)";
    } else if (formData.street.trim().length > 200) {
      errors.street = "Địa chỉ quá dài (tối đa 200 ký tự)";
    }

    // Validate payment method
    if (!formData.paymentMethod || !["COD", "BANKING"].includes(formData.paymentMethod)) {
      errors.paymentMethod = "Vui lòng chọn phương thức thanh toán";
    }

    setFieldErrors(errors);
    return Object.keys(errors).length === 0;
  };

  // Submit Handler
  const handleSubmit = async (e) => {
    e.preventDefault();
    
    // Validate form
    if (!validate()) {
      showToast.error("Vui lòng kiểm tra lại thông tin");
      return;
    }

    // Check cart not empty
    if (!cartItems || cartItems.length === 0) {
      showToast.error("Giỏ hàng đang trống");
      return;
    }

    // Validate all items have valid quantity
    const invalidItems = cartItems.filter(item => !item.quantity || item.quantity <= 0);
    if (invalidItems.length > 0) {
      showToast.error("Số lượng sản phẩm không hợp lệ");
      return;
    }

    setLoading(true);
    setError("");
    setFieldErrors({});

    try {
      // Build full address string as backend expects
      const addressParts = [
        formData.street,
        formData.ward,
        formData.district,
        formData.city,
      ].filter(Boolean);
      const fullAddress = addressParts.join(", ");

      if (!fullAddress || fullAddress.trim().length < 10) {
        throw new Error("Địa chỉ giao hàng không hợp lệ");
      }

      // Backend expects simple structure: { userId, shippingAddress, items, paymentMethod }
      const checkoutPayload = {
        shippingAddress: fullAddress,
        paymentMethod: formData.paymentMethod,
        items: cartItems.map((item) => ({
          productId: String(item.product.productId), // Convert to String
          quantity: item.quantity,
        })),
      };

      console.log("=== [Checkout] Sending payload:", checkoutPayload);

      const response = await api.post("/checkout", checkoutPayload);

      console.log("=== [Checkout] Response:", response.data);

      if (response.data && response.data.success) {
        showToast.success("Đặt hàng thành công!");
        setSuccessData({
          orderId: response.data.orderId || "MỚI",
          total: totals.total,
        });
        clearCart();
      } else {
        throw new Error(response.data?.message || "Đặt hàng thất bại");
      }
    } catch (err) {
      console.error("=== [Checkout] Error:", err);
      
      const serverData = err.response?.data;
      
      // Handle validation errors from backend
      if (serverData && serverData.errors) {
        const fe = {};
        Object.keys(serverData.errors).forEach((k) => {
          fe[k] = Array.isArray(serverData.errors[k])
            ? serverData.errors[k].join(" ")
            : String(serverData.errors[k]);
        });
        setFieldErrors(fe);
        const errorMsg = serverData.message || "Vui lòng kiểm tra thông tin";
        setError(errorMsg);
        showToast.error(errorMsg);
      } else {
        // Handle general errors
        const errorMsg = serverData?.message || err.message || "Có lỗi xảy ra, vui lòng thử lại.";
        setError(errorMsg);
        showToast.error(errorMsg);
      }
    } finally {
      setLoading(false);
    }
  };

  // --- RENDER SUCCESS STATE ---
  if (successData) {
    return (
      <div className="min-h-screen bg-gradient-to-br from-emerald-50 via-white to-amber-50 flex items-center justify-center p-4">
        <div className="bg-white rounded-3xl shadow-xl p-12 text-center max-w-lg">
          <div className="w-24 h-24 mx-auto mb-6 bg-gradient-to-br from-emerald-100 to-teal-100 rounded-full flex items-center justify-center">
            <svg className="w-12 h-12 text-emerald-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M5 13l4 4L19 7" />
            </svg>
          </div>
          
          <h2 className="text-3xl font-bold text-gray-800 mb-2">
            Đặt hàng thành công! 🎉
          </h2>
          <p className="text-gray-500 mb-8">
            Cảm ơn bạn đã mua sắm tại FoodNhom2
          </p>

          <div className="bg-gradient-to-br from-emerald-50 to-teal-50 rounded-2xl p-6 mb-8 border border-emerald-100">
            <div className="flex justify-between mb-3 text-sm">
              <span className="text-gray-600">Mã đơn hàng:</span>
              <span className="font-bold text-gray-900">#{successData.orderId}</span>
            </div>
            <div className="flex justify-between mb-3 text-sm">
              <span className="text-gray-600">Phương thức:</span>
              <span className="font-medium text-gray-900">
                {formData.paymentMethod === "COD" ? "💵 Thanh toán khi nhận" : "🏦 Chuyển khoản"}
              </span>
            </div>
            <div className="border-t border-emerald-200 my-3"></div>
            <div className="flex justify-between text-xl font-bold text-emerald-600">
              <span>Tổng tiền:</span>
              <span>{formatPrice(successData.total)}</span>
            </div>
          </div>

          <div className="space-y-3">
            <button
              onClick={() => navigate("/orders")}
              className="w-full py-4 bg-gradient-to-r from-emerald-500 to-teal-500 text-white font-semibold rounded-xl hover:from-emerald-600 hover:to-teal-600 transition-all shadow-lg hover:shadow-xl"
            >
              Xem đơn hàng của tôi
            </button>
            <button
              onClick={() => navigate("/products")}
              className="w-full py-3 bg-white text-emerald-600 font-medium rounded-xl border-2 border-emerald-200 hover:bg-emerald-50 transition-colors"
            >
              Tiếp tục mua sắm
            </button>
          </div>
        </div>
      </div>
    );
  }

  if (loading) {
    return (
      <div className="min-h-screen bg-gradient-to-br from-emerald-50 via-white to-amber-50 flex items-center justify-center">
        <LoadingSpinner />
      </div>
    );
  }

  // --- RENDER MAIN FORM ---
  return (
    <div className="min-h-screen bg-gradient-to-br from-emerald-50 via-white to-amber-50">
      {/* Header */}
      <header className="bg-gradient-to-r from-emerald-600 via-green-500 to-teal-500 shadow-lg">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex items-center justify-between h-16">
            <button
              onClick={() => navigate(-1)}
              className="flex items-center gap-2 text-white hover:text-emerald-100 transition-colors"
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
          <div className="bg-white rounded-3xl shadow-xl p-12 text-center max-w-lg mx-auto">
            <div className="w-32 h-32 mx-auto mb-6 bg-gradient-to-br from-amber-100 to-orange-100 rounded-full flex items-center justify-center">
              <span className="text-6xl">⚠️</span>
            </div>
            <h2 className="text-2xl font-bold text-gray-800 mb-2">Giỏ hàng trống</h2>
            <p className="text-gray-500 mb-8">
              Bạn cần thêm sản phẩm vào giỏ hàng trước khi thanh toán
            </p>
            <button
              onClick={() => navigate("/products")}
              className="px-8 py-4 bg-gradient-to-r from-emerald-500 to-teal-500 text-white font-semibold rounded-xl hover:from-emerald-600 hover:to-teal-600 transition-all shadow-lg hover:shadow-xl"
            >
              🛍️ Khám phá sản phẩm
            </button>
          </div>
        ) : (
          <form onSubmit={handleSubmit} className="grid grid-cols-1 lg:grid-cols-3 gap-8">
            {/* LEFT COLUMN: FORM INPUTS */}
            <div className="lg:col-span-2 space-y-6">
              {/* 1. Thông tin giao hàng */}
              <div className="bg-white rounded-2xl shadow-sm p-6">
                <h3 className="text-lg font-bold text-gray-800 mb-4 flex items-center gap-2">
                  <span className="w-8 h-8 rounded-full bg-gradient-to-br from-emerald-100 to-teal-100 text-emerald-600 flex items-center justify-center text-sm font-bold">
                    1
                  </span>
                  📍 Thông tin nhận hàng
                </h3>

                <div className="space-y-4">
                  <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                    <div>
                      <label className="block text-sm font-medium text-gray-700 mb-2">
                        Người nhận <span className="text-red-500">*</span>
                      </label>
                      <input
                        type="text"
                        name="recipientName"
                        value={formData.recipientName}
                        onChange={handleChange}
                        placeholder="Nhập tên người nhận"
                        disabled={loading}
                        className={`w-full px-4 py-3 rounded-xl border text-gray-900 placeholder-gray-400 ${
                          fieldErrors.recipientName
                            ? "border-red-300 bg-red-50"
                            : "border-gray-200"
                        } focus:outline-none focus:ring-2 focus:ring-emerald-500 transition-all disabled:opacity-50 disabled:cursor-not-allowed`}
                      />
                      {fieldErrors.recipientName && (
                        <p className="text-red-500 text-xs mt-1">{fieldErrors.recipientName}</p>
                      )}
                    </div>

                    <div>
                      <label className="block text-sm font-medium text-gray-700 mb-2">
                        Số điện thoại <span className="text-red-500">*</span>
                      </label>
                      <input
                        type="tel"
                        name="recipientPhone"
                        value={formData.recipientPhone}
                        onChange={handleChange}
                        placeholder="Nhập số điện thoại"
                        disabled={loading}
                        className={`w-full px-4 py-3 rounded-xl border text-gray-900 placeholder-gray-400 ${
                          fieldErrors.recipientPhone
                            ? "border-red-300 bg-red-50"
                            : "border-gray-200"
                        } focus:outline-none focus:ring-2 focus:ring-emerald-500 transition-all disabled:opacity-50 disabled:cursor-not-allowed`}
                      />
                      {fieldErrors.recipientPhone && (
                        <p className="text-red-500 text-xs mt-1">{fieldErrors.recipientPhone}</p>
                      )}
                    </div>
                  </div>

                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">
                      Địa chỉ chi tiết <span className="text-red-500">*</span>
                    </label>
                    <input
                      type="text"
                      name="street"
                      value={formData.street}
                      onChange={handleChange}
                      placeholder="Số nhà, tên đường"
                      disabled={loading}
                      className={`w-full px-4 py-3 rounded-xl border text-gray-900 placeholder-gray-400 ${
                        fieldErrors.street
                          ? "border-red-300 bg-red-50"
                          : "border-gray-200"
                      } focus:outline-none focus:ring-2 focus:ring-emerald-500 transition-all disabled:opacity-50 disabled:cursor-not-allowed`}
                    />
                    {fieldErrors.street && (
                      <p className="text-red-500 text-xs mt-1">{fieldErrors.street}</p>
                    )}
                  </div>

                  <div className="grid grid-cols-1 sm:grid-cols-3 gap-3">
                    <input
                      type="text"
                      name="ward"
                      value={formData.ward}
                      onChange={handleChange}
                      placeholder="Phường / Xã"
                      disabled={loading}
                      className="px-4 py-3 rounded-xl border border-gray-200 text-gray-900 placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-emerald-500 transition-all disabled:opacity-50 disabled:cursor-not-allowed"
                    />
                    <input
                      type="text"
                      name="district"
                      value={formData.district}
                      onChange={handleChange}
                      placeholder="Quận / Huyện"
                      disabled={loading}
                      className="px-4 py-3 rounded-xl border border-gray-200 text-gray-900 placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-emerald-500 transition-all disabled:opacity-50 disabled:cursor-not-allowed"
                    />
                    <input
                      type="text"
                      name="city"
                      value={formData.city}
                      onChange={handleChange}
                      placeholder="Tỉnh / TP"
                      disabled={loading}
                      className="px-4 py-3 rounded-xl border border-gray-200 text-gray-900 placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-emerald-500 transition-all disabled:opacity-50 disabled:cursor-not-allowed"
                    />
                  </div>

                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">
                      Ghi chú (Tùy chọn)
                    </label>
                    <textarea
                      name="note"
                      value={formData.note}
                      onChange={handleChange}
                      placeholder="VD: Giao giờ hành chính, gọi trước khi giao..."
                      rows="3"
                      disabled={loading}
                      className="w-full px-4 py-3 rounded-xl border border-gray-200 text-gray-900 placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-emerald-500 transition-all resize-none disabled:opacity-50 disabled:cursor-not-allowed"
                    />
                  </div>
                </div>
              </div>

              {/* 2. Phương thức thanh toán */}
              <div className="bg-white rounded-2xl shadow-sm p-6">
                <h3 className="text-lg font-bold text-gray-800 mb-4 flex items-center gap-2">
                  <span className="w-8 h-8 rounded-full bg-gradient-to-br from-emerald-100 to-teal-100 text-emerald-600 flex items-center justify-center text-sm font-bold">
                    2
                  </span>
                  💳 Phương thức thanh toán
                </h3>

                <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                  {/* Option 1: COD */}
                  <div
                    onClick={() => !loading && setFormData({ ...formData, paymentMethod: "COD" })}
                    className={`p-5 rounded-xl border-2 transition-all ${
                      loading ? "opacity-50 cursor-not-allowed" : "cursor-pointer"
                    } ${
                      formData.paymentMethod === "COD"
                        ? "border-emerald-500 bg-gradient-to-br from-emerald-50 to-teal-50 shadow-md"
                        : "border-gray-200 hover:border-emerald-200 hover:bg-gray-50"
                    }`}
                  >
                    <div className="flex items-center gap-3">
                      <div className="w-12 h-12 rounded-full bg-gradient-to-br from-green-100 to-emerald-100 flex items-center justify-center text-2xl flex-shrink-0">
                        💵
                      </div>
                      <div className="flex-1">
                        <div className="font-bold text-gray-800">Tiền mặt (COD)</div>
                        <div className="text-xs text-gray-500">Thanh toán khi nhận hàng</div>
                      </div>
                      {formData.paymentMethod === "COD" && (
                        <svg className="w-6 h-6 text-emerald-600 flex-shrink-0" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M5 13l4 4L19 7" />
                        </svg>
                      )}
                    </div>
                  </div>

                  {/* Option 2: Banking */}
                  <div
                    onClick={() => !loading && setFormData({ ...formData, paymentMethod: "BANKING" })}
                    className={`p-5 rounded-xl border-2 transition-all ${
                      loading ? "opacity-50 cursor-not-allowed" : "cursor-pointer"
                    } ${
                      formData.paymentMethod === "BANKING"
                        ? "border-emerald-500 bg-gradient-to-br from-emerald-50 to-teal-50 shadow-md"
                        : "border-gray-200 hover:border-emerald-200 hover:bg-gray-50"
                    }`}
                  >
                    <div className="flex items-center gap-3">
                      <div className="w-12 h-12 rounded-full bg-gradient-to-br from-purple-100 to-pink-100 flex items-center justify-center text-2xl flex-shrink-0">
                        🏦
                      </div>
                      <div className="flex-1">
                        <div className="font-bold text-gray-800">Chuyển khoản</div>
                        <div className="text-xs text-gray-500">Qua QR Code / E-Banking</div>
                      </div>
                      {formData.paymentMethod === "BANKING" && (
                        <svg className="w-6 h-6 text-emerald-600 flex-shrink-0" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M5 13l4 4L19 7" />
                        </svg>
                      )}
                    </div>
                  </div>
                </div>
              </div>
            </div>

            {/* RIGHT COLUMN: ORDER SUMMARY */}
            <div className="lg:col-span-1">
              <div className="bg-white rounded-2xl shadow-sm p-6 sticky top-4">
                <h2 className="text-xl font-bold text-gray-800 mb-6 pb-4 border-b">
                  Đơn hàng ({cartItems.length} món)
                </h2>

                {/* Scrollable Product List */}
                <div className="max-h-[280px] overflow-y-auto mb-6 space-y-3 pr-2">
                  {cartItems.map((item, index) => (
                    <div key={item.product?.productId || index} className="flex gap-3 pb-3 border-b border-gray-100 last:border-0">
                      <div className="w-16 h-16 bg-gray-100 rounded-lg flex-shrink-0 overflow-hidden">
                        {item.product?.imageUrl ? (
                          <img
                            src={item.product.imageUrl}
                            alt={item.product.name}
                            className="w-full h-full object-cover"
                          />
                        ) : (
                          <div className="w-full h-full flex items-center justify-center text-xs text-gray-400">
                            No Img
                          </div>
                        )}
                      </div>
                      <div className="flex-1 min-w-0">
                        <p className="text-sm font-medium text-gray-800 line-clamp-2 mb-1">
                          {item.product?.name}
                        </p>
                        <div className="flex justify-between items-center">
                          <span className="text-xs text-gray-500">x{item.quantity}</span>
                          <span className="text-sm font-semibold text-emerald-600">
                            {formatPrice((item.product?.salePrice || item.product?.originalPrice || 0) * item.quantity)}
                          </span>
                        </div>
                      </div>
                    </div>
                  ))}
                </div>

                {/* Calculations */}
                <div className="space-y-3 mb-6">
                  <div className="flex justify-between text-gray-600">
                    <span>Tạm tính</span>
                    <span className="font-medium">{formatPrice(totals.subtotal)}</span>
                  </div>
                  <div className="flex justify-between text-gray-600">
                    <span>Phí vận chuyển</span>
                    <span className="font-medium">{formatPrice(totals.shipping)}</span>
                  </div>
                </div>

                <div className="border-t pt-4 mb-6">
                  <div className="flex justify-between items-center">
                    <span className="text-lg font-semibold text-gray-800">Tổng cộng</span>
                    <span className="text-2xl font-bold text-emerald-600">
                      {formatPrice(totals.total)}
                    </span>
                  </div>
                </div>

                {/* Error Message */}
                {error && (
                  <div className="mb-4 p-3 bg-red-50 border border-red-200 rounded-xl text-red-600 text-sm">
                    ⚠️ {error}
                  </div>
                )}

                {/* Action Button */}
                <button
                  type="submit"
                  disabled={loading || cartItems.length === 0}
                  className="w-full py-4 bg-gradient-to-r from-emerald-500 to-teal-500 text-white font-semibold rounded-xl hover:from-emerald-600 hover:to-teal-600 transition-all shadow-lg hover:shadow-xl disabled:opacity-50 disabled:cursor-not-allowed"
                >
                  {loading ? "Đang xử lý..." : "🛒 ĐẶT HÀNG"}
                </button>

                <p className="text-center text-xs text-gray-400 mt-4">
                  Bạn có 30 phút để đến lấy đồ sau khi đặt
                </p>
              </div>
            </div>
          </form>
        )}
      </main>
    </div>
  );
};

export default CheckoutPage;
