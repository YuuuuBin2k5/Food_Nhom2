import { useState, useMemo } from "react";
import {
  Button,
  Card,
  CardBody,
  Input,
  Textarea,
  Divider,
  Chip,
} from "@heroui/react";
import { useAuth } from "../context/AuthContext";
import { useNavigate } from "react-router-dom";
import api from "../services/api";
import { useCart } from "../context/CartContext";

// --- ICONS (SVG thuần để tối ưu hiệu năng) ---
const Icons = {
  Back: () => (
    <path
      strokeLinecap="round"
      strokeLinejoin="round"
      strokeWidth={2}
      d="M10 19l-7-7m0 0l7-7m-7 7h18"
    />
  ),
  User: () => (
    <path
      strokeLinecap="round"
      strokeLinejoin="round"
      strokeWidth={2}
      d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"
    />
  ),
  Phone: () => (
    <path
      strokeLinecap="round"
      strokeLinejoin="round"
      strokeWidth={2}
      d="M3 5a2 2 0 012-2h3.28a1 1 0 01.948.684l1.498 4.493a1 1 0 01-.502 1.21l-2.257 1.13a11.042 11.042 0 005.516 5.516l1.13-2.257a1 1 0 011.21-.502l4.493 1.498a1 1 0 01.684.949V19a2 2 0 01-2 2h-1C9.716 21 3 14.284 3 6V5z"
    />
  ),
  Map: () => (
    <>
      <path
        strokeLinecap="round"
        strokeLinejoin="round"
        strokeWidth={2}
        d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z"
      />
      <path
        strokeLinecap="round"
        strokeLinejoin="round"
        strokeWidth={2}
        d="M15 11a3 3 0 11-6 0 3 3 0 016 0z"
      />
    </>
  ),
  Cash: () => (
    <path
      strokeLinecap="round"
      strokeLinejoin="round"
      strokeWidth={2}
      d="M17 9V7a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2m2 4h10a2 2 0 002-2v-6a2 2 0 00-2-2H9a2 2 0 00-2 2v6a2 2 0 002 2zm7-5a2 2 0 11-4 0 2 2 0 014 0z"
    />
  ),
  Bank: () => (
    <path
      strokeLinecap="round"
      strokeLinejoin="round"
      strokeWidth={2}
      d="M3 10h18M7 15h1m4 0h1m-7 4h12a3 3 0 003-3V8a3 3 0 00-3-3H6a3 3 0 00-3 3v8a3 3 0 003 3z"
    />
  ),
  Check: () => (
    <path
      strokeLinecap="round"
      strokeLinejoin="round"
      strokeWidth={2}
      d="M5 13l4 4L19 7"
    />
  ),
};

const CheckoutPage = () => {
  const { user } = useAuth();
  const navigate = useNavigate();
  const { items: cartItems, getTotals, clearCart } = useCart();

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [successData, setSuccessData] = useState(null); // Lưu data đơn hàng thành công để hiển thị

  // Form State (structured address fields for better UX)
  const [formData, setFormData] = useState({
    recipientName: user?.fullName || "",
    recipientPhone: user?.phoneNumber || "",
    street: user?.address || "",
    ward: "",
    district: "",
    city: user?.city || "",
    useAccountAddress: !!user?.address,
    paymentMethod: "COD",
    note: "", // Thêm ghi chú
  });

  // Validation State
  const [fieldErrors, setFieldErrors] = useState({});

  const totals = useMemo(() => getTotals(), [cartItems, getTotals]);

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
    if (!formData.recipientName || !formData.recipientName.trim()) {
      errors.recipientName = "Vui lòng nhập tên người nhận";
    } else if (formData.recipientName.length < 2) {
      errors.recipientName = "Tên quá ngắn";
    }

    const phone = (formData.recipientPhone || "").replace(/[^0-9+]/g, "");
    const phoneRe = /^\+?[0-9]{9,14}$/;
    if (!phone) {
      errors.recipientPhone = "Vui lòng nhập số điện thoại";
    } else if (!phoneRe.test(phone)) {
      errors.recipientPhone = "Số điện thoại không hợp lệ";
    }

    if (!formData.street || formData.street.trim().length < 6) {
      errors.street = "Vui lòng nhập địa chỉ (số nhà, tên đường)";
    }

    setFieldErrors(errors);
    return Object.keys(errors).length === 0;
  };

  // Submit Handler
  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!validate()) return;

    setLoading(true);
    setError("");
    setFieldErrors({});

    try {
      if (!cartItems || cartItems.length === 0)
        throw new Error("Giỏ hàng đang trống");

      const checkoutPayload = {
        userId: user.userId,
        buyerName: user.fullName,
        buyerPhone: user.phoneNumber,
        shippingAddress: {
          recipientName: formData.recipientName,
          recipientPhone: formData.recipientPhone,
          street: formData.street,
          ward: formData.ward,
          district: formData.district,
          city: formData.city,
        },
        paymentMethod: formData.paymentMethod,
        note: formData.note,
        items: cartItems.map((it) => ({
          productId: it.productId,
          quantity: it.quantity,
          price: Number(it.salePrice || it.price || 0), // ensure number
        })),
        totalAmount: Number(totals.total || 0), // Gửi tổng tiền để backend đối chiếu (optional)
      };

      const response = await api.post("/checkout", checkoutPayload);

      if (response.data && response.data.success) {
        setSuccessData({
          orderId: response.data.orderId || "MỚI", // Giả sử backend trả về orderId
          total: totals.total,
        });
        clearCart();
        // Không navigate ngay lập tức, hiển thị trang thành công trước
      } else {
        throw new Error(response.data?.message || "Đặt hàng thất bại");
      }
    } catch (err) {
      // If backend returned validation errors in { errors: { field: [msg] } }
      const serverData = err.response?.data;
      if (serverData && serverData.errors) {
        const fe = {};
        Object.keys(serverData.errors).forEach((k) => {
          // join multiple messages
          fe[k] = Array.isArray(serverData.errors[k])
            ? serverData.errors[k].join(" ")
            : String(serverData.errors[k]);
        });
        setFieldErrors(fe);
        setError(serverData.message || "Vui lòng kiểm tra thông tin");
      } else {
        setError(
          serverData?.message ||
            err.message ||
            "Có lỗi xảy ra, vui lòng thử lại."
        );
      }
    } finally {
      setLoading(false);
    }
  };

  // --- RENDER SUCCESS STATE ---
  if (successData) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center p-4 animate-in fade-in zoom-in duration-300">
        <Card className="max-w-md w-full shadow-2xl border-t-4 border-green-500">
          <CardBody className="p-8 text-center">
            <div className="w-20 h-20 bg-green-100 rounded-full flex items-center justify-center mx-auto mb-6">
              <svg
                className="w-10 h-10 text-green-600"
                fill="none"
                viewBox="0 0 24 24"
                stroke="currentColor"
              >
                <Icons.Check />
              </svg>
            </div>
            <h2 className="text-2xl font-bold text-gray-900 mb-2">
              Đặt hàng thành công!
            </h2>
            <p className="text-gray-500 mb-6">
              Cảm ơn bạn đã mua sắm tại FoodNhom2
            </p>

            <div className="bg-gray-50 rounded-lg p-4 mb-6 border border-gray-100">
              <div className="flex justify-between mb-2 text-sm">
                <span className="text-gray-600">Mã đơn hàng:</span>
                <span className="font-bold text-gray-900">
                  #{successData.orderId}
                </span>
              </div>
              <div className="flex justify-between mb-2 text-sm">
                <span className="text-gray-600">Phương thức:</span>
                <span className="font-medium text-gray-900">
                  {formData.paymentMethod === "COD"
                    ? "Thanh toán khi nhận"
                    : "Chuyển khoản"}
                </span>
              </div>
              <Divider className="my-2" />
              <div className="flex justify-between text-lg font-bold text-blue-600">
                <span>Tổng tiền:</span>
                <span>
                  {new Intl.NumberFormat("vi-VN", {
                    style: "currency",
                    currency: "VND",
                  }).format(successData.total)}
                </span>
              </div>
            </div>

            <div className="space-y-3">
              <Button
                color="primary"
                className="w-full font-bold shadow-lg shadow-blue-200"
                onPress={() => navigate("/orders")}
              >
                Xem đơn hàng của tôi
              </Button>
              <Button
                variant="light"
                className="w-full"
                onPress={() => navigate("/")}
              >
                Tiếp tục mua sắm
              </Button>
            </div>
          </CardBody>
        </Card>
      </div>
    );
  }

  // --- RENDER MAIN FORM ---
  return (
    <div className="min-h-screen bg-gray-50 pb-12">
      {/* Header Background */}
      <div className="bg-white border-b border-gray-200 sticky top-16 md:top-20 z-30">
        <div className="max-w-7xl mx-auto px-4 py-4 flex items-center gap-4">
          <Button
            isIconOnly
            variant="light"
            onPress={() => navigate(-1)}
            className="text-gray-500"
          >
            <svg
              className="w-6 h-6"
              fill="none"
              viewBox="0 0 24 24"
              stroke="currentColor"
            >
              <Icons.Back />
            </svg>
          </Button>
          <h1 className="text-xl font-bold text-gray-800">
            Thanh toán an toàn
          </h1>
        </div>
      </div>

      <div className="max-w-7xl mx-auto px-4 mt-8">
        {cartItems.length === 0 && (
          <div className="mb-6 rounded-lg bg-yellow-50 border border-yellow-100 p-4 text-yellow-800">
            Giỏ hàng của bạn đang trống.{" "}
            <button
              className="underline text-blue-600"
              onClick={() => navigate("/products")}
            >
              Quay lại mua sắm
            </button>
          </div>
        )}
        <form
          onSubmit={handleSubmit}
          className="grid grid-cols-1 lg:grid-cols-12 gap-8"
        >
          {/* LEFT COLUMN: INPUTS (Chiếm 8 phần) */}
          <div className="lg:col-span-8 space-y-6">
            {/* 1. Thông tin giao hàng */}
            <Card className="shadow-sm border border-gray-100">
              <CardBody className="p-6 text-black">
                <h3 className="text-lg font-bold text-gray-800 mb-4 flex items-center gap-2">
                  <span className="w-8 h-8 rounded-full bg-blue-100 text-blue-600 flex items-center justify-center text-sm">
                    1
                  </span>
                  Thông tin nhận hàng
                </h3>

                <div className="grid grid-cols-1 md:grid-cols-2 gap-4 mb-4">
                  <Input
                    label="Người nhận"
                    name="recipientName"
                    value={formData.recipientName}
                    onChange={handleChange}
                    variant="bordered"
                    startContent={
                      <div className="text-gray-400">
                        <Icons.User />
                      </div>
                    }
                    isInvalid={!!fieldErrors.recipientName}
                    errorMessage={fieldErrors.recipientName}
                  />

                  <Input
                    label="Số điện thoại"
                    name="recipientPhone"
                    value={formData.recipientPhone}
                    onChange={handleChange}
                    variant="bordered"
                    startContent={
                      <div className="text-gray-400">
                        <Icons.Phone />
                      </div>
                    }
                    isInvalid={!!fieldErrors.recipientPhone}
                    errorMessage={fieldErrors.recipientPhone}
                  />
                </div>

                <div className="space-y-4 mb-2">
                  <div className="flex items-center gap-3">
                    <input
                      id="useAccountAddress"
                      name="useAccountAddress"
                      type="checkbox"
                      checked={!!formData.useAccountAddress}
                      onChange={(e) =>
                        setFormData({
                          ...formData,
                          useAccountAddress: e.target.checked,
                          street: e.target.checked
                            ? user?.address || ""
                            : formData.street,
                        })
                      }
                    />
                    <label
                      htmlFor="useAccountAddress"
                      className="text-sm text-gray-600"
                    >
                      Sử dụng địa chỉ trong tài khoản
                    </label>
                  </div>

                  <Input
                    label="Số nhà, tên đường"
                    name="street"
                    value={formData.street}
                    onChange={handleChange}
                    variant="bordered"
                    startContent={
                      <div className="text-gray-400">
                        <Icons.Map />
                      </div>
                    }
                    isInvalid={!!fieldErrors.street}
                    errorMessage={fieldErrors.street}
                  />

                  <div className="grid grid-cols-1 sm:grid-cols-3 gap-3">
                    <Input
                      label="Phường / Xã"
                      name="ward"
                      value={formData.ward}
                      onChange={handleChange}
                      variant="bordered"
                    />
                    <Input
                      label="Quận / Huyện"
                      name="district"
                      value={formData.district}
                      onChange={handleChange}
                      variant="bordered"
                    />
                    <Input
                      label="Tỉnh / Thành phố"
                      name="city"
                      value={formData.city}
                      onChange={handleChange}
                      variant="bordered"
                    />
                  </div>

                  <Input
                    label="Ghi chú cho đơn hàng (Tùy chọn)"
                    placeholder="VD: Giao giờ hành chính, gọi trước khi giao..."
                    name="note"
                    value={formData.note}
                    onChange={handleChange}
                    variant="bordered"
                  />
                </div>
              </CardBody>
            </Card>

            {/* 2. Phương thức thanh toán */}
            <Card className="shadow-sm border border-gray-100">
              <CardBody className="p-6">
                <h3 className="text-lg font-bold text-gray-800 mb-4 flex items-center gap-2">
                  <span className="w-8 h-8 rounded-full bg-blue-100 text-blue-600 flex items-center justify-center text-sm">
                    2
                  </span>
                  Phương thức thanh toán
                </h3>

                <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                  {/* Option 1: COD */}
                  <div
                    onClick={() =>
                      setFormData({ ...formData, paymentMethod: "COD" })
                    }
                    className={`cursor-pointer p-4 rounded-xl border-2 transition-all flex items-center gap-4 ${
                      formData.paymentMethod === "COD"
                        ? "border-blue-600 bg-blue-50"
                        : "border-gray-200 hover:border-gray-300"
                    }`}
                  >
                    <div className="w-10 h-10 rounded-full bg-green-100 text-green-600 flex items-center justify-center">
                      <svg
                        className="w-6 h-6"
                        fill="none"
                        viewBox="0 0 24 24"
                        stroke="currentColor"
                      >
                        <Icons.Cash />
                      </svg>
                    </div>
                    <div>
                      <div className="font-bold text-gray-800">
                        Tiền mặt (COD)
                      </div>
                      <div className="text-xs text-gray-500">
                        Thanh toán khi nhận hàng
                      </div>
                    </div>
                    {formData.paymentMethod === "COD" && (
                      <div className="ml-auto text-blue-600">
                        <svg
                          className="w-6 h-6"
                          fill="none"
                          viewBox="0 0 24 24"
                          stroke="currentColor"
                        >
                          <Icons.Check />
                        </svg>
                      </div>
                    )}
                  </div>

                  {/* Option 2: Banking */}
                  <div
                    onClick={() =>
                      setFormData({ ...formData, paymentMethod: "BANKING" })
                    }
                    className={`cursor-pointer p-4 rounded-xl border-2 transition-all flex items-center gap-4 ${
                      formData.paymentMethod === "BANKING"
                        ? "border-blue-600 bg-blue-50"
                        : "border-gray-200 hover:border-gray-300"
                    }`}
                  >
                    <div className="w-10 h-10 rounded-full bg-purple-100 text-purple-600 flex items-center justify-center">
                      <svg
                        className="w-6 h-6"
                        fill="none"
                        viewBox="0 0 24 24"
                        stroke="currentColor"
                      >
                        <Icons.Bank />
                      </svg>
                    </div>
                    <div>
                      <div className="font-bold text-gray-800">
                        Chuyển khoản
                      </div>
                      <div className="text-xs text-gray-500">
                        Qua QR Code / E-Banking
                      </div>
                    </div>
                    {formData.paymentMethod === "BANKING" && (
                      <div className="ml-auto text-blue-600">
                        <svg
                          className="w-6 h-6"
                          fill="none"
                          viewBox="0 0 24 24"
                          stroke="currentColor"
                        >
                          <Icons.Check />
                        </svg>
                      </div>
                    )}
                  </div>
                </div>
              </CardBody>
            </Card>
          </div>

          {/* RIGHT COLUMN: SUMMARY (Chiếm 4 phần, Sticky) */}
          <div className="lg:col-span-4">
            <div className="sticky top-20 space-y-4">
              <Card className="shadow-lg border border-gray-100 overflow-hidden">
                <div className="bg-gray-50 p-4 border-b border-gray-100">
                  <h3 className="font-bold text-gray-800">
                    Đơn hàng của bạn ({cartItems.length} món)
                  </h3>
                </div>

                <CardBody className="p-0">
                  {/* Scrollable Product List */}
                  <div className="max-h-[300px] overflow-y-auto p-4 space-y-4 custom-scrollbar">
                    {cartItems.map((item) => (
                      <div key={item.productId} className="flex gap-3">
                        <div className="w-16 h-16 bg-gray-100 rounded-lg flex-shrink-0 overflow-hidden border border-gray-200">
                          {item.imageUrl ? (
                            <img
                              src={item.imageUrl}
                              alt={item.name}
                              className="w-full h-full object-cover"
                            />
                          ) : (
                            <div className="w-full h-full flex items-center justify-center text-xs">
                              No Img
                            </div>
                          )}
                        </div>
                        <div className="flex-1 min-w-0">
                          <p className="text-sm font-medium text-gray-800 truncate line-clamp-2">
                            {item.name}
                          </p>
                          <div className="flex justify-between items-end mt-1">
                            <p className="text-xs text-gray-500">
                              x{item.quantity}
                            </p>
                            <p className="text-sm font-semibold text-gray-900">
                              {new Intl.NumberFormat("vi-VN", {
                                style: "currency",
                                currency: "VND",
                              }).format(
                                (item.salePrice || item.price) * item.quantity
                              )}
                            </p>
                          </div>
                        </div>
                      </div>
                    ))}
                  </div>

                  <Divider />

                  {/* Calculations */}
                  <div className="p-4 space-y-2 bg-gray-50/50">
                    <div className="flex justify-between text-sm text-gray-600">
                      <span>Tạm tính</span>
                      <span>
                        {new Intl.NumberFormat("vi-VN", {
                          style: "currency",
                          currency: "VND",
                        }).format(totals.subtotal)}
                      </span>
                    </div>
                    <div className="flex justify-between text-sm text-gray-600">
                      <span>Phí vận chuyển</span>
                      <span>
                        {new Intl.NumberFormat("vi-VN", {
                          style: "currency",
                          currency: "VND",
                        }).format(totals.shipping)}
                      </span>
                    </div>
                    {/* Voucher nếu có sẽ thêm vào đây */}

                    <Divider className="my-2" />

                    <div className="flex justify-between items-center">
                      <span className="font-bold text-gray-800 text-lg">
                        Tổng cộng
                      </span>
                      <span className="font-bold text-blue-600 text-2xl">
                        {new Intl.NumberFormat("vi-VN", {
                          style: "currency",
                          currency: "VND",
                        }).format(totals.total)}
                      </span>
                    </div>
                  </div>

                  {/* Error Message */}
                  {error && (
                    <div className="px-4 py-2 bg-red-50 text-red-600 text-sm border-t border-red-100">
                      ⚠️ {error}
                    </div>
                  )}

                  {/* Action Button */}
                  <div className="p-4 bg-white border-t border-gray-100">
                    <Button
                      type="submit"
                      color="primary"
                      size="lg"
                      className="w-full font-bold shadow-lg shadow-blue-200 text-lg"
                      isLoading={loading}
                      onClick={handleSubmit}
                      disabled={cartItems.length === 0}
                    >
                      {loading ? "Đang xử lý..." : "ĐẶT HÀNG"}
                    </Button>
                    <p className="text-xs text-center text-gray-400 mt-2">
                      Nhấn "Đặt hàng" đồng nghĩa với việc bạn đồng ý với điều
                      khoản của chúng tôi.
                    </p>
                  </div>
                </CardBody>
              </Card>
            </div>
          </div>
        </form>
      </div>
    </div>
  );
};

export default CheckoutPage;
