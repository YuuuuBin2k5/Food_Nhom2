// src/components/layouts/AuthLayout.jsx
import React from "react";

const AuthLayout = ({ children, imageSrc, title, subtitle }) => {
  return (
    <div className="flex min-h-screen w-full py-10 px-4 items-center justify-center bg-[#FFF9F0]">
      <div className="flex w-full max-w-6xl overflow-hidden rounded-xl shadow-lg bg-white transition-all duration-200 flex-col md:flex-row min-h-[600px] border border-[#E7DACE]/30">
        {/* CỘT TRÁI: Ảnh */}
        <div className="w-full md:w-[45%] relative min-h-[200px] md:min-h-full">
          <div className="absolute inset-0 bg-gradient-to-b from-[#FF6B6B]/10 via-transparent to-[#FF8E53]/8 mix-blend-overlay z-10" />
          <img
            src={imageSrc}
            alt="auth-background"
            className="w-full h-full object-cover opacity-80"
          />
        </div>

        {/* CỘT PHẢI: Nội dung Form */}
        <div className="w-full md:w-[55%] px-8 py-10 bg-white flex flex-col justify-center">
          <div className="flex flex-col mb-8 items-center text-center">
            <h1 className="text-3xl font-extrabold bg-gradient-to-b from-[#FF6B6B] via-[#FF8E53] to-[#FFC75F] bg-clip-text text-transparent">
              {title}
            </h1>
            {subtitle && (
              <p className="mt-2 text-[#334155] text-sm font-medium opacity-90">
                {subtitle}
              </p>
            )}
          </div>

          {/* Render nội dung form ở đây */}
          {children}
        </div>
      </div>
    </div>
  );
};

export default AuthLayout;
