import React, { useState } from "react";
import { Button, Form, Input, Tab, Tabs } from "@heroui/react";
import { Link, useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

const RegisterPage = () => {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  // Mặc định là người mua ("buyer")
  const [selectedRole, setSelectedRole] = useState("buyer");

  const { register } = useAuth();
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");

    // Lấy dữ liệu từ Form
    const formData = new FormData(e.currentTarget);
    const data = Object.fromEntries(formData);

    // 1. Validate mật khẩu
    if (data.password !== data.confirmPassword) {
      setError("Mật khẩu nhập lại không khớp!");
      return;
    }

    // 2. Validate riêng cho Seller
    if (selectedRole === "seller" && !data.shopName) {
      setError("Vui lòng nhập tên cửa hàng!");
      return;
    }

    setLoading(true);

    const payload = {
      ...data,
      role: selectedRole,
    };

    delete payload.confirmPassword;

    try {
      const result = await register(payload);

      if (result.success) {
        alert("Đăng ký thành công! Vui lòng đăng nhập.");
        navigate("/login");
      } else {
        setError(result.message || "Đăng ký thất bại, vui lòng thử lại.");
      }
    } catch (err) {
      setError("Lỗi kết nối server.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="flex min-h-screen w-full items-center justify-center bg-gray-50 p-8">
      <div className="w-full max-w-xl overflow-hidden rounded-2xl bg-white shadow-lg transition-all duration-200">
        {/* Header Section */}
        <div className="rounded-t-2xl bg-gradient-to-r from-blue-600 to-blue-500 px-10 py-8 text-center">
          <h1 className="text-4xl leading-tight font-extrabold text-white">
            Tạo tài khoản
          </h1>
          <p className="mt-2 text-blue-100 text-base">
            Tham gia cộng đồng Food Rescue ngay hôm nay
          </p>
        </div>

        <div className="px-10 py-8 text-gray-700">
          {/* ROLE SELECTION */}
          <div className="mb-6 flex w-full justify-center">
            <Tabs
              aria-label="Role Selection"
              color="primary"
              radius="full"
              selectedKey={selectedRole}
              onSelectionChange={(key) => setSelectedRole(key.toString())}
              classNames={{
                tabList: "bg-gray-100 p-2 border border-gray-200 rounded-full",
                cursor: "shadow-sm",
                tabContent:
                  "font-semibold group-data-[selected=true]:text-blue-600",
              }}
            >
              <Tab key="buyer" title="Khách hàng" />
              <Tab key="seller" title="Người bán" />
              <Tab key="shipper" title="Shipper" />
            </Tabs>
          </div>

          <Form
            className="flex flex-col gap-5"
            validationBehavior="native"
            onSubmit={handleSubmit}
          >
            {/* Logic hiển thị trường riêng cho Seller (shopName) */}
            {selectedRole === "seller" && (
              <Input
                isRequired
                label="Tên Cửa Hàng"
                labelPlacement="outside"
                name="shopName"
                placeholder="Ví dụ: Cơm Tấm Sài Gòn"
                variant="bordered"
                radius="md"
                classNames={{
                  inputWrapper:
                    "border-blue-200 bg-blue-50 focus-within:border-blue-500",
                  label: "font-medium text-blue-600",
                }}
              />
            )}

            {/* Các trường chung */}
            <Input
              isRequired
              label="Họ và tên"
              labelPlacement="outside"
              name="fullName"
              placeholder="Nguyễn Văn A"
              variant="bordered"
              radius="md"
            />

            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
              <Input
                isRequired
                label="Số điện thoại"
                labelPlacement="outside"
                name="phoneNumber"
                placeholder="0901..."
                type="tel"
                variant="bordered"
                radius="md"
              />
              <Input
                isRequired
                label="Email"
                labelPlacement="outside"
                name="email"
                placeholder="email@domain.com"
                type="email"
                variant="bordered"
                radius="md"
              />
            </div>

            <Input
              isRequired
              label="Mật khẩu"
              labelPlacement="outside"
              name="password"
              placeholder="••••••••"
              type="password"
              variant="bordered"
              radius="md"
            />

            <Input
              isRequired
              label="Nhập lại mật khẩu"
              labelPlacement="outside"
              name="confirmPassword"
              placeholder="••••••••"
              type="password"
              variant="bordered"
              radius="md"
            />

            {/* Hiển thị lỗi nếu có */}
            {error && (
              <div className="rounded-lg bg-red-50 p-3 text-sm text-red-700 border border-red-100 flex items-center gap-2">
                ⚠️ {error}
              </div>
            )}

            <Button
              color="primary"
              type="submit"
              size="lg"
              radius="md"
              isLoading={loading}
              className="mt-3 w-full bg-blue-600 font-bold shadow-md"
            >
              {loading ? "Đang xử lý..." : "Đăng Ký Tài Khoản"}
            </Button>

            <div className="mt-4 text-center text-sm text-gray-600">
              Đã có tài khoản?{" "}
              <Link
                to="/login"
                className="font-semibold text-blue-600 hover:text-blue-800 hover:underline"
              >
                Đăng nhập ngay
              </Link>
            </div>
          </Form>
        </div>
      </div>
    </div>
  );
};

export default RegisterPage;
