import { useState } from "react";
import {
  Button,
  Card,
  CardBody,
  Input,
  Select,
  SelectItem,
} from "@heroui/react";
import { useAuth } from "../context/AuthContext";
import { useNavigate } from "react-router-dom";
import api from "../services/api";
import { useCart } from "../context/CartContext";

const CheckoutPage = () => {
  const { user } = useAuth();
  const navigate = useNavigate();

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState(false);

  const [formData, setFormData] = useState({
    shippingAddress: user?.address || "",
    paymentMethod: "COD",
  });

  const {
    items: cartItems,
    getTotals,
    clearCart,
    updateQuantity,
    removeItem,
  } = useCart();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError("");
    try {
      if (!cartItems || cartItems.length === 0) {
        setError("Giỏ hàng rỗng");
        setLoading(false);
        return;
      }

      const orderItems = cartItems.map((it) => ({
        productId: it.productId,
        quantity: it.quantity,
      }));

      const checkoutData = {
        userId: user.userId,
        shippingAddress: formData.shippingAddress,
        paymentMethod: formData.paymentMethod,
        items: orderItems,
      };

      const response = await api.post("/checkout", checkoutData);

      if (response.data && response.data.success) {
        setSuccess(true);
        clearCart();
        setTimeout(() => {
          navigate("/orders");
        }, 1200);
      } else {
        setError(response.data?.message || "Đặt hàng thất bại");
      }
    } catch (err) {
      setError(
        err.response?.data?.message ||
          err.message ||
          "Có lỗi xảy ra khi đặt hàng"
      );
    } finally {
      setLoading(false);
    }
  };

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value,
    });
  };

  if (success) {
    return (
      <div className="min-h-screen bg-gradient-to-br from-green-50 to-green-100 flex items-center justify-center p-6 ">
        <Card className="max-w-md w-full shadow-lg">
          <CardBody className="p-8 text-center">
            <div className="text-6xl mb-4">✅</div>
            <h2 className="text-2xl font-bold text-gray-800 mb-2">
              Đặt hàng thành công!
            </h2>
            <p className="text-gray-600">
              Đơn hàng của bạn đang được xử lý. Đang chuyển đến trang đơn
              hàng...
            </p>
          </CardBody>
        </Card>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gradient-to-br from-blue-50 to-indigo-100 p-6 text-black">
      <div className="max-w-4xl mx-auto">
        {/* Header */}
        <div className="mb-8">
          <Button
            variant="light"
            onPress={() => navigate("/")}
            className="mb-4"
          >
            ← Quay lại
          </Button>
          <h1 className="text-4xl font-bold text-gray-800">Thanh toán</h1>
          <p className="text-gray-600 mt-2">Hoàn tất đơn hàng của bạn</p>
        </div>

        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
          {/* Form thanh toán */}
          <div className="lg:col-span-2">
            <Card className="shadow-lg">
              <CardBody className="p-6">
                <h2 className="text-2xl font-bold text-gray-800 mb-6">
                  Thông tin giao hàng
                </h2>

                <form onSubmit={handleSubmit} className="space-y-4">
                  {/* Thông tin người dùng */}
                  <div>
                    <Input
                      label="Họ tên"
                      value={user?.fullName || ""}
                      isReadOnly
                      variant="bordered"
                    />
                  </div>

                  <div>
                    <Input
                      label="Email"
                      value={user?.email || ""}
                      isReadOnly
                      variant="bordered"
                    />
                  </div>

                  <div>
                    <Input
                      label="Số điện thoại"
                      value={user?.phoneNumber || ""}
                      isReadOnly
                      variant="bordered"
                    />
                  </div>

                  {/* Địa chỉ giao hàng */}
                  <div>
                    <Input
                      isRequired
                      label="Địa chỉ giao hàng"
                      name="shippingAddress"
                      placeholder="Nhập địa chỉ đầy đủ"
                      value={formData.shippingAddress}
                      onChange={handleChange}
                      variant="bordered"
                    />
                  </div>

                  {/* Phương thức thanh toán */}
                  <div>
                    <Select
                      label="Phương thức thanh toán"
                      name="paymentMethod"
                      selectedKeys={[formData.paymentMethod]}
                      onChange={(e) =>
                        setFormData({
                          ...formData,
                          paymentMethod: e.target.value,
                        })
                      }
                      variant="bordered"
                    >
                      <SelectItem key="COD" value="COD">
                        Thanh toán khi nhận hàng (COD)
                      </SelectItem>
                      <SelectItem key="BANKING" value="BANKING">
                        Chuyển khoản ngân hàng
                      </SelectItem>
                    </Select>
                  </div>

                  {error && (
                    <div className="rounded-lg bg-red-50 p-3 text-sm text-red-700 border border-red-100">
                      ⚠️ {error}
                    </div>
                  )}

                  <Button
                    type="submit"
                    color="primary"
                    size="lg"
                    className="w-full"
                    isLoading={loading}
                  >
                    {loading ? "Đang xử lý..." : "Đặt hàng"}
                  </Button>
                </form>
              </CardBody>
            </Card>
          </div>

          {/* Tóm tắt đơn hàng */}
          <div>
            <Card className="shadow-lg">
              <CardBody className="p-6">
                <h2 className="text-2xl font-bold text-gray-800 mb-4">
                  Đơn hàng
                </h2>

                <div className="space-y-3 mb-4">
                  {cartItems.length === 0 ? (
                    <div className="p-6 text-gray-500">Giỏ hàng trống</div>
                  ) : (
                    cartItems.map((it) => (
                      <div
                        key={it.productId}
                        className="flex items-center justify-between py-2 border-b"
                      >
                        <div>
                          <div className="font-medium">{it.name}</div>
                          <div className="text-sm text-gray-500">
                            x{it.quantity}
                          </div>
                        </div>
                        <div className="font-semibold">
                          {new Intl.NumberFormat("vi-VN", {
                            style: "currency",
                            currency: "VND",
                          }).format(
                            (it.salePrice || it.price || 0) * it.quantity
                          )}
                        </div>
                      </div>
                    ))
                  )}
                </div>

                <div className="space-y-2 pt-4 border-t">
                  {(() => {
                    const t = getTotals();
                    return (
                      <>
                        <div className="flex justify-between">
                          <span className="text-gray-600">Tạm tính:</span>
                          <span className="font-semibold">
                            {new Intl.NumberFormat("vi-VN", {
                              style: "currency",
                              currency: "VND",
                            }).format(t.subtotal)}
                          </span>
                        </div>
                        <div className="flex justify-between">
                          <span className="text-gray-600">Phí vận chuyển:</span>
                          <span className="font-semibold">
                            {new Intl.NumberFormat("vi-VN", {
                              style: "currency",
                              currency: "VND",
                            }).format(t.shipping)}
                          </span>
                        </div>
                        <div className="flex justify-between text-lg font-bold pt-2 border-t">
                          <span>Tổng cộng:</span>
                          <span className="text-blue-600">
                            {new Intl.NumberFormat("vi-VN", {
                              style: "currency",
                              currency: "VND",
                            }).format(t.total)}
                          </span>
                        </div>
                      </>
                    );
                  })()}
                </div>
              </CardBody>
            </Card>
          </div>
        </div>
      </div>
    </div>
  );
};

export default CheckoutPage;
