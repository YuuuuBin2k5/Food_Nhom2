import React from "react";
import { useNavigate } from "react-router-dom";

export const HeaderSeller = ({ user }) => {
  const navigate = useNavigate();
  return (
    <div className="bg-gradient-to-r from-[#FF6B6B] via-[#FF8E53] to-[#FFC75F] shadow-lg mb-8">
      <div className="max-w-7xl mx-auto px-4 py-8">
        <div className="flex flex-col md:flex-row justify-between items-start md:items-center gap-4">
          <div>
            <h1 className="text-3xl font-bold text-white flex items-center gap-3">
              <span className="text-4xl">📊</span>
              Tổng quan kinh doanh
            </h1>
            <p className="text-white/90 text-base mt-2">
              Xin chào,{" "}
              <span className="font-bold">
                {user?.extraInfo?.shopName || user?.fullName}
              </span>
              ! 👋
            </p>
          </div>
          <div className="flex gap-3">
            <button
              onClick={() => navigate("/seller/products")}
              className="px-5 py-2.5 bg-white text-[#FF6B6B] rounded-xl font-semibold hover:bg-orange-50 shadow-lg transition-all flex items-center gap-2"
            >
              <span>📦</span>
              Quản lý kho
            </button>
            <button
              onClick={() => navigate("/seller/orders")}
              className="px-5 py-2.5 bg-white/10 backdrop-blur-sm text-white border-2 border-white/30 rounded-xl font-semibold hover:bg-white/20 transition-all flex items-center gap-2"
            >
              <span>📄</span>
              Xem đơn hàng
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};
