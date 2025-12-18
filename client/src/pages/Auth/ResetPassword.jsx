import React, { useState, useEffect, useRef } from "react";
import { useSearchParams, useNavigate } from "react-router-dom";
import MysicInput from "../../components/common/MysicInput";
import MysicButton from "../../components/common/MysicButton";
import { resetPasswordAPI } from "../../services/authService";
import imageUrl from "../../images/backgroundLogin.png";

const ResetPassword = () => {
  const [searchParams] = useSearchParams();
  const token = searchParams.get("token");
  const navigate = useNavigate();
  const wrapperRef = useRef(null);

  const [password, setPassword] = useState("");
  const [confirm, setConfirm] = useState("");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [message, setMessage] = useState("");

  useEffect(() => {
    if (!token) setError("Token không hợp lệ.");
  }, [token]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");
    setMessage("");
    if (!password || !confirm)
      return setError("Vui lòng nhập mật khẩu và xác nhận.");
    if (password !== confirm) return setError("Mật khẩu xác nhận không khớp.");
    if (!token) return setError("Token không được cung cấp.");

    setLoading(true);
    try {
      const res = await resetPasswordAPI(token, password);
      const payload = res?.data ?? res;
      if (payload?.success) {
        setMessage(
          payload.message || "Đổi mật khẩu thành công. Hãy đăng nhập."
        );
        setTimeout(() => navigate("/login"), 1400);
      } else {
        setError(payload.message || "Không thể đặt lại mật khẩu.");
      }
    } catch (err) {
      setError(err.response?.data?.message || "Lỗi máy chủ. Thử lại sau.");
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
      <div className="flex w-full max-w-6xl h-180 overflow-hidden rounded-xl shadow-2xl bg-white/10 backdrop-blur-md border border-white/30">
        <div className="auth-side w-full md:w-[50%] relative h-full flex flex-col justify-between p-10">
          <div className="absolute inset- z-0 overflow-hidden rounded-l-3xl" />
          <div className="auth-side-content relative z-10 text-entrance delay-100">
            <h2 className="text-white text-xs font-bold tracking-[0.2em] uppercase mb-4 opacity-90">
              Our Mission
            </h2>
            <h1 className="text-white text-5xl font-black leading-tight drop-shadow-md text-glow">
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
          <div className="flex flex-col mb-6 items-center text-center">
            <span className="text-[#FF8E53] text-xs font-bold tracking-[0.2em] uppercase mb-2 opacity-90 text-entrance delay-100">
              Reset Password
            </span>
            <h1 className="text-4xl font-black tracking-tight bg-gradient-to-b from-[#FF6B6B] via-[#FF8E53] to-[#FFC75F] bg-clip-text text-transparent drop-shadow-[0_0_18px_rgba(255,140,83,0.12)] text-entrance delay-200">
              Đặt lại mật khẩu
            </h1>
            <p className="mt-3 mb-6 text-[#334155] text-base font-normal opacity-90 leading-relaxed text-entrance delay-300">
              Nhập mật khẩu mới của bạn.
            </p>
          </div>

          <form className="flex flex-col gap-6" onSubmit={handleSubmit}>
            <MysicInput
              label="Mật khẩu mới"
              name="password"
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              isRequired
            />

            <MysicInput
              label="Xác nhận mật khẩu"
              name="confirm"
              type="password"
              value={confirm}
              onChange={(e) => setConfirm(e.target.value)}
              isRequired
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
            <div className="flex items-center justify-center">
              <MysicButton type="submit" isLoading={loading}>
                {loading ? "Đang xử lý..." : "Đặt lại mật khẩu"}
              </MysicButton>
            </div>

            <div className="mt-4 text-center text-sm text-[#334155] opacity-90">
              Quay lại đăng nhập?{" "}
              <button
                type="button"
                onClick={handleBackToLogin}
                className="font-bold text-[#10B981] hover:underline opacity-100"
              >
                Đăng nhập
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
};

export default ResetPassword;
