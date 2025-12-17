import React, { useState } from "react";
import { Button, Form, Input, Checkbox } from "@heroui/react";
import { Link, useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import { loginAPI } from "../services/authService";

const LoginPage = () => {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const { login } = useAuth();
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError("");

    const form = new FormData(e.target);
    const email = form.get("email");
    const password = form.get("password");

    try {
      const res = await loginAPI(email, password);
      // loginAPI currently returns axios response; get payload
      const payload = res?.data ?? res;

      if (payload.success) {
        // store in context and localStorage via context.login
        login(payload.user, payload.token);

        // điều hướng theo role nếu backend trả role
        const role = payload.user?.role?.toUpperCase();
        if (role === "ADMIN") navigate("/admin/dashboard");
        else if (role === "SELLER") navigate("/seller/dashboard");
        else if (role === "SHIPPER") navigate("/shipper/orders");
        else navigate("/");
      } else {
        setError(payload.message || "Sai tài khoản hoặc mật khẩu");
      }
    } catch (err) {
      setError(
        err.response?.data?.message || err.message || "Lỗi khi đăng nhập"
      );
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="flex min-h-screen w-full items-center justify-center bg-gray-50 p-6">
      {/* Card Container */}
      <div className="w-full max-w-md overflow-hidden rounded-2xl bg-white shadow-lg transition-all duration-200">
        {/* Header Section */}
        <div className="rounded-t-2xl bg-gradient-to-r from-blue-600 to-blue-500 px-8 py-6 text-center">
          <h1 className="text-3xl font-extrabold text-white">
            Chào mừng trở lại!
          </h1>
          <p className="mt-1 text-blue-100 text-sm">
            Đăng nhập để tiếp tục quản lý
          </p>
        </div>

        {/* Form Section */}
        <div className="px-8 py-6">
          <Form
            className="flex flex-col"
            validationBehavior="native"
            onSubmit={handleSubmit}
          >
            <div className="mb-4">
              <Input
                isRequired
                label="Email"
                labelPlacement="outside"
                name="email"
                placeholder="admin@gmail.com"
                type="email"
                variant="bordered"
                radius="md"
                classNames={{
                  label: "font-medium text-gray-700 text-sm",
                  inputWrapper:
                    "border-gray-200 bg-gray-50 focus-within:border-blue-500",
                  input: "border w-full bg-transparent text-black",
                  errorMessage: "text-red-600",
                }}
              />
            </div>

            <div className="mb-3">
              <Input
                isRequired
                label="Mật khẩu"
                labelPlacement="outside"
                name="password"
                placeholder="••••••••"
                type="password"
                variant="bordered"
                radius="md"
                classNames={{
                  label: "font-medium text-gray-700 text-sm",
                  inputWrapper:
                    "border-gray-200 bg-gray-50 focus-within:border-blue-500",
                  input: "border w-full bg-transparent text-black",
                  errorMessage: "text-red-600",
                }}
              />
            </div>

            <div className="flex items-center justify-between mb-4">
              <label className="flex items-center gap-2 text-sm text-gray-600">
                <Checkbox size="sm" classNames={{ input: "p-1" }} />
                <span>Ghi nhớ tôi</span>
              </label>

              <Link
                to="#"
                className="text-sm font-medium text-blue-600 hover:text-blue-800 hover:underline"
              >
                Quên mật khẩu?
              </Link>
            </div>

            {error && (
              <div className="rounded-lg bg-red-50 p-3 text-sm text-red-700 border border-red-100 flex items-center gap-2 mb-3">
                ⚠️ {error}
              </div>
            )}

            <Button
              color="primary"
              type="submit"
              size="lg"
              radius="md"
              isLoading={loading}
              className="mt-1 w-full bg-blue-600 text-white font-semibold hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-200"
            >
              {loading ? "Đang xác thực..." : "Đăng nhập"}
            </Button>

            <div className="mt-5 text-center text-sm text-gray-600">
              Chưa có tài khoản?{" "}
              <Link
                to="/register"
                className="font-semibold text-blue-600 hover:text-blue-800 hover:underline"
              >
                Tạo tài khoản mới
              </Link>
            </div>
          </Form>
        </div>
      </div>
    </div>
  );
};

export default LoginPage;
