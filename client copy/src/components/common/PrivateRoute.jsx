import React from "react";
import { Navigate, Outlet } from "react-router-dom";
import { useAuth } from "../../context/AuthContext";

const PrivateRoute = () => {
  const { user, loading } = useAuth();

  // 1. Nếu đang kiểm tra xem user có đăng nhập hay không (lúc F5 trang)
  if (loading) {
    return (
      <div style={{ textAlign: "center", marginTop: "50px" }}>Đang tải...</div>
    );
  }

  // 2. Nếu có user -> Cho hiển thị nội dung bên trong (Outlet)
  // 3. Nếu không -> Đá về trang Login
  return user ? <Outlet /> : <Navigate to="/login" replace />;
};

export default PrivateRoute;
