import { useState, useRef } from "react";
import { Form } from "@heroui/react";
import { Link, useNavigate } from "react-router-dom";
import { useAuth } from "../../context/AuthContext";
import { loginAPI } from "../../services/authService";
import imageUrl from "../../images/backgroundLogin.png";

// Import Component riêng của bạn
import MysicInput from "../../components/common/MysicInput";
import MysicButton from "../../components/common/MysicButton";

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
      const payload = res?.data ?? res;

      if (payload.success) {
        login(payload.user, payload.token);
        const role = payload.user?.role?.toUpperCase();
        if (role === "ADMIN") navigate("/admin/dashboard");
        else if (role === "SELLER") navigate("/seller/dashboard");
        else if (role === "SHIPPER") navigate("/shipper/orders");
        else navigate("/");
      } else {
        setError(payload.message || "Sai tài khoản hoặc mật khẩu");
      }
    } catch (err) {
      // Prefer detailed field errors when available
      const resp = err.response?.data;
      if (resp?.errors) {
        // resp.errors is a map: field -> [messages]
        const flat = Object.values(resp.errors).flat();
        setError(flat.join("; "));
      } else {
        setError(resp?.message || err.message || "Lỗi khi đăng nhập");
      }
    } finally {
      setLoading(false);
    }
  };

  const wrapperRef = useRef(null);

  const handleToRegister = (e) => {
    e?.preventDefault();
    const wrap = wrapperRef.current;
    if (!wrap) return navigate("/register");
    wrap.classList.add("auth-swap-to-register");
    setTimeout(() => navigate("/register"), 700);
  };

  return (
    <div
      ref={wrapperRef}
      className="auth-wrap flex min-h-screen w-full py-10 px-4 items-center justify-center"
      style={{ backgroundImage: `url(${imageUrl})` }}
    >
      <div className="flex w-full max-w-6xl h-180 overflow-hidden rounded-xl shadow-2xl bg-white/10 backdrop-blur-md border border-white/30">
        {/* --- LEFT --- */}
        <div className="auth-side w-full md:w-[50%] relative h-full flex flex-col justify-between p-10">
          <div className="absolute inset- z-0 overflow-hidden rounded-l-3xl" />{" "}
          {/* Thêm rounded-l-3xl cho khớp */}
          <div className="auth-side-content relative z-10 text-entrance">
            <h2 className="text-white text-[10px] font-bold tracking-[0.2em] uppercase mb-4 opacity-90">
              Our Mission
            </h2>
            <h1 className="text-white text-4xl font-black leading-tight drop-shadow-md">
              Sẻ Chia <br />
              Hương Vị <br />
              Yêu Thương
            </h1>
          </div>
          <div className="auth-side-content relative z-10 text-entrance">
            <p className="text-white/90 text-sm font-medium leading-relaxed max-w-sm">
              Đừng để món ngon bị lãng phí. Hãy biến thức ăn dư thừa thành niềm
              vui cho cộng đồng ngay hôm nay.
            </p>
          </div>
        </div>

        {/* CỘT PHẢI: Form */}
        <div className="auth-form w-[50%] px-10 py-8 bg-white flex flex-col justify-center">
          <div className="flex flex-col mb-10 items-center text-center">
            {/* Tiêu đề chính: Gradient & Glow */}
            <h1 className="text-4xl font-black tracking-tight bg-gradient-to-b from-[#FF6B6B] via-[#FF8E53] to-[#FFC75F] bg-clip-text text-transparent drop-shadow-[0_0_18px_rgba(255,140,83,0.12)] text-entrance delay-200">
              Chào mừng trở lại!
            </h1>

            {/* Dòng phụ: Nhấn mạnh chữ "hệ thống quản lý" sáng hơn */}
            <p className="mt-3 mb-10 text-[#334155] text-base font-normal opacity-90 leading-relaxed text-entrance delay-300">
              Đăng nhập{" "}
              <span className="font-bold text-[#0f172a]">hệ thống quản lý</span>
            </p>
          </div>

          <Form
            className="flex flex-col gap-6" // Giảm gap xuống 6 cho cân đối
            validationBehavior="native"
            onSubmit={handleSubmit}
          >
            {/* Sử dụng Component Input.jsx đã tách */}
            <MysicInput
              isRequired
              label="Email"
              name="email"
              placeholder="admin@gmail.com"
              type="email"
            />

            <MysicInput
              isRequired
              label="Mật khẩu"
              name="password"
              placeholder="••••••••"
              type="password"
            />

            <div className="flex items-center justify-between w-full -mt-1">
              <div className="flex items-center gap-2">
                <input
                  id="remember"
                  name="remember"
                  type="checkbox"
                  className="w-4 h-4 rounded border-gray-300 text-[#FF6B6B] focus:ring-[#FF6B6B]"
                />
                <label htmlFor="remember" className="text-sm text-[#334155] font-medium select-none">
                  Ghi nhớ tôi
                </label>
              </div>
              <Link
                to="/forgot-password"
                className="text-sm font-bold text-[#FF6B6B] hover:text-[#FF8E53] hover:underline transition-colors"
              >
                Quên mật khẩu?
              </Link>
            </div>

            {error && (
              <div className="rounded-lg bg-[#FF6B6B]/10 p-3 text-sm text-[#FF6B6B] border border-[#FF6B6B]/30 flex items-center gap-2">
                ⚠️ {error}
              </div>
            )}

            <div className="flex items-center justify-center w-full">
              <MysicButton type="submit" isLoading={loading} className="w-full max-w-xs">
                {loading ? "Đang xác thực" : "Đăng nhập"}
              </MysicButton>
            </div>
          </Form>

          <div className="mt-6 text-center text-sm text-[#334155] opacity-90">
            Chưa có tài khoản?{" "}
            <button
              type="button"
              onClick={handleToRegister}
              className="font-bold text-[#10B981] hover:underline opacity-100 cursor-pointer"
            >
              Tạo tài khoản mới
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default LoginPage;
