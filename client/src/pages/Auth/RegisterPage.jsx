import { useState, useRef } from "react";
import { Form, Tab, Tabs } from "@heroui/react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../../context/AuthContext";
import imageUrl from "../../images/backgroundLogin.png";

// Import Component tái sử dụng (đã tạo ở bước trước)
import MysicInput from "../../components/common/MysicInput";
import MysicButton from "../../components/common/MysicButton";

const RegisterPage = () => {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [selectedRole, setSelectedRole] = useState("buyer");
  const { register } = useAuth();
  const navigate = useNavigate();
  const wrapperRef = useRef(null);

  const handleToLogin = (e) => {
    e?.preventDefault();
    const wrap = wrapperRef.current;
    if (!wrap) return navigate("/login");
    wrap.classList.add("auth-swap-to-login");
    setTimeout(() => navigate("/login"), 700);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");

    const formData = new FormData(e.currentTarget);
    const data = Object.fromEntries(formData);

    if (data.password !== data.confirmPassword) {
      setError("Mật khẩu nhập lại không khớp!");
      return;
    }

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
        handleToLogin();
      } else {
        // If server returned field errors, format them for display
        if (result.errors && typeof result.errors === "object") {
          const flat = Object.values(result.errors).flat();
          setError(flat.join("; "));
        } else {
          setError(result.message || "Đăng ký thất bại, vui lòng thử lại.");
        }
      }
    } catch (err) {
      // try to surface server response message when fetch fails
      const body = err?.response?.data || err;
      if (body?.errors) {
        const flat = Object.values(body.errors).flat();
        setError(flat.join("; "));
      } else if (body?.message) {
        setError(body.message);
      } else {
        setError("Lỗi kết nối server.");
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <div
      ref={wrapperRef}
      className="auth-wrap flex min-h-screen w-full py-10 px-4 items-center justify-center bg-cover bg-center"
      style={{ backgroundImage: `url(${imageUrl})` }}
    >
      <div className="flex w-full max-w-6xl h-[650px] overflow-hidden rounded-xl shadow-2xl bg-white/10 backdrop-blur-md border border-white/30">
        {/* LEFT: FORM (will animate in from left when page mounts) */}
        <div className="auth-form w-full md:w-[50%] px-8 py-10 bg-white flex flex-col justify-start">
          <div className="flex flex-col mb-4 items-center text-center">
            {/* Hiệu ứng Gradient Text + Bóng nhẹ */}
            <h1 className="text-4xl font-black tracking-tight bg-gradient-to-b from-[#FF6B6B] via-[#FF8E53] to-[#FFC75F] bg-clip-text text-transparent drop-shadow-[0_0_18px_rgba(255,140,83,0.12)] text-entrance delay-200">
              Tạo tài khoản
            </h1>

            <p className="mt-2 text-[#334155] text-base font-normal opacity-90 max-w-5xs leading-relaxed text-entrance delay-300">
              Tham gia cộng đồng{" "}
              <span className="font-bold text-[#0f172a]">Food Rescue</span> ngay
              hôm nay
            </p>
          </div>

          {/* Role Selection Tabs */}
          <div className="mb-12 flex w-full justify-center">
            <Tabs
              aria-label="Role Selection"
              radius="full"
              selectedKey={selectedRole}
              onSelectionChange={(key) => setSelectedRole(key.toString())}
              classNames={{
                tabList:
                  "bg-[#FFF9F0] p-2 w-100 h-10 border border-[#E7DACE]/40",
                cursor: "bg-[#FF6B6B] shadow-md",
                tabContent:
                  "text-[#334155] font-bold group-data-[selected=true]:text-white",
              }}
            >
              <Tab key="buyer" title="Khách hàng" />
              <Tab key="seller" title="Người bán" />
              <Tab key="shipper" title="Shipper" />
            </Tabs>
          </div>

          <Form
            className="flex flex-col gap-4"
            validationBehavior="native"
            onSubmit={handleSubmit}
          >
            {/* Logic hiển thị trường riêng cho Seller */}
            {selectedRole === "seller" && (
              <MysicInput
                isRequired
                label="Tên Cửa Hàng"
                name="shopName"
                placeholder="Ví dụ: Cơm Tấm Sài Gòn"
              />
            )}

            <MysicInput
              isRequired
              label="Họ và tên"
              name="fullName"
              placeholder="Nguyễn Văn A"
            />

            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
              <MysicInput
                isRequired
                label="Số điện thoại"
                name="phoneNumber"
                placeholder="0901..."
                type="tel"
              />
              <MysicInput
                isRequired
                label="Email"
                name="email"
                placeholder="email@domain.com"
                type="email"
              />
            </div>

            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
              <MysicInput
                isRequired
                label="Mật khẩu"
                name="password"
                placeholder="••••••••"
                type="password"
              />

              <MysicInput
                isRequired
                label="Xác nhận mật khẩu"
                name="confirmPassword"
                placeholder="••••••••"
                type="password"
              />
            </div>

            {error && (
              <div className="rounded-lg bg-[#FF6B6B]/10 p-3 text-sm text-[#FF6B6B] border border-[#FF6B6B]/30 flex items-center gap-2">
                ⚠️ {error}
              </div>
            )}

            <div className="flex items-center justify-center w-full">
              <MysicButton type="submit" isLoading={loading} className="w-full max-w-xs">
                {loading ? "Đang xử lý..." : "Đăng Ký"}
              </MysicButton>
            </div>
          </Form>

          <div className="mt-6 text-center text-sm text-[#334155] opacity-90">
            Đã có tài khoản?{" "}
            <button
              type="button"
              onClick={handleToLogin}
              className="font-bold text-[#10B981] hover:underline opacity-100 cursor-pointer"
            >
              Đăng nhập ngay
            </button>
          </div>
        </div>

        {/* RIGHT: IMAGE + PROMO TEXT (translucent image with different text) */}
        <div className="auth-side w-full md:w-[50%] relative h-full flex flex-col justify-between p-10 ">
          <div className="absolute inset-0 bg-white/10 backdrop-blur-md z-0 overflow-hidden rounded-r-3xl" />
          <div className="auth-side-content relative z-10 text-right text-entrance delay-100">
            <h2 className="text-white text-xs font-bold tracking-[0.2em] uppercase mb-4 opacity-90">
              Join the Movement
            </h2>
            <h1 className="text-white text-4xl font-black leading-tight drop-shadow-md">
              Bạn đang tham gia
              <br /> Hành Trình
              <br /> Giải Cứu Thức Ăn
            </h1>
          </div>
          <div className="auth-side-content relative z-10 text-right">
            <p className="text-white/90 text-sm font-medium leading-relaxed max-w-sm ml-auto text-entrance delay-100">
              Cùng nhau chuyển hóa phần dư thành cơ hội cho người cần. Bắt đầu
              bằng việc tạo một tài khoản và chia sẻ yêu thương.
            </p>
          </div>
        </div>
      </div>
    </div>
  );
};

export default RegisterPage;
