import React, { useState, useRef } from "react";
import { Form } from "@heroui/react";
import { useNavigate } from "react-router-dom";
import { forgotPasswordAPI } from "../../services/authService";

import MysicInput from "../../components/common/MysicInput";
import MysicButton from "../../components/common/MysicButton";
import imageUrl from "../../images/backgroundLogin.png";

const ForgotPassword = () => {
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState("");
  const [error, setError] = useState("");
  const navigate = useNavigate();
  const wrapperRef = useRef(null);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");
    setMessage("");
    const form = new FormData(e.target);
    const email = form.get("email");
    if (!email) return setError("Vui lòng nhập email");

    setLoading(true);
    try {
      const res = await forgotPasswordAPI(email);
      const payload = res?.data ?? res;

      if (payload?.success) {
        setMessage(
          payload.message || "Nếu email tồn tại, hướng dẫn sẽ được gửi."
        );
      } else {
        setError(payload.message || "Yêu cầu thất bại, vui lòng thử lại.");
      }
    } catch (err) {
      setError(
        err.response?.data?.message || "Lỗi khi gửi yêu cầu. Vui lòng thử lại."
      );
    } finally {
      setLoading(false);
    }
  };

  const handleBackToLogin = (e) => {
    e?.preventDefault();
    const wrap = wrapperRef.current;
    if (!wrap) return navigate("/login");
    wrap.classList.add("auth-swap-to-login");
    setTimeout(() => navigate("/login"), 700);
  };

  return (
    <div
      ref={wrapperRef}
      className="auth-wrap flex min-h-screen w-full py-10 px-4 items-center justify-center bg-cover bg-center"
      style={{ backgroundImage: `url(${imageUrl})` }}
    >
      <div className="flex w-full max-w-6xl h-[600px] overflow-hidden rounded-xl shadow-2xl bg-white/10 backdrop-blur-md border border-white/30">
        <div className="auth-side w-full md:w-[50%] relative h-full flex flex-col justify-between p-10">
          <div className="absolute inset- z-0 overflow-hidden rounded-l-3xl" />
          <div className="auth-side-content relative z-10 text-entrance delay-100">
            <h2 className="text-white text-[10px] font-bold tracking-[0.2em] uppercase mb-4 opacity-90">
              Our Mission
            </h2>
            <h1 className="text-white text-4xl font-black leading-tight drop-shadow-md">
              Sẻ Chia <br /> Hương Vị <br /> Yêu Thương
            </h1>
          </div>
          <div className="auth-side-content relative z-10">
            <p className="text-white/90 text-sm font-medium leading-relaxed max-w-sm text-entrance delay-300">
              Đừng để món ngon bị lãng phí. Hãy biến thức ăn dư thừa thành niềm
              vui cho cộng đồng ngay hôm nay.
            </p>
          </div>
        </div>

        <div className="auth-form w-[50%] px-10 py-8 bg-white flex flex-col justify-center">
          <div className="flex flex-col mb-10 items-center text-center">
            <span className="text-[#FF8E53] text-xs font-bold tracking-[0.2em] uppercase mb-2 opacity-90 text-entrance delay-100">
              Forgot Password
            </span>
            <h1 className="text-4xl font-black tracking-tight bg-gradient-to-b from-[#FF6B6B] via-[#FF8E53] to-[#FFC75F] bg-clip-text text-transparent drop-shadow-[0_0_18px_rgba(255,140,83,0.12)] text-entrance delay-200">
              Quên mật khẩu?
            </h1>
            <p className="mt-3 mb-6 text-[#334155] text-base font-normal opacity-90 leading-relaxed text-entrance delay-300">
              Nhập email của bạn để nhận hướng dẫn đặt lại mật khẩu.
            </p>
          </div>

          <Form
            className="flex flex-col gap-6"
            onSubmit={handleSubmit}
            validationBehavior="native"
          >
            <MysicInput
              isRequired
              label="Email"
              name="email"
              placeholder="email@domain.com"
              type="email"
            />

            {error && (
              <div className="rounded-lg bg-[#FF6B6B]/10 p-3 text-sm text-[#FF6B6B] border border-[#FF6B6B]/30 flex items-center gap-2">
                ⚠️ {error}
              </div>
            )}

            {message && (
              <div className="rounded-lg bg-[#10B981]/10 p-3 text-sm text-[#065f46] border border-[#10B981]/30 flex items-center gap-2">
                ✅ {message}
              </div>
            )}

            <div className="flex items-center justify-center w-full">
              <MysicButton type="submit" isLoading={loading} className="w-full max-w-xs">
                {loading ? "Đang gửi..." : "Gửi email đặt lại"}
              </MysicButton>
            </div>
          </Form>

          <div className="mt-6 text-center text-sm text-[#334155] opacity-90">
            Quay lại đăng nhập?{" "}
            <button
              type="button"
              onClick={handleBackToLogin}
              className="font-bold text-[#10B981] hover:underline opacity-100"
            >
              Đăng nhập
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default ForgotPassword;
