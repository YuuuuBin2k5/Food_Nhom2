import React from "react";
import { Routes, Route } from "react-router-dom";
import LoginPage from "../pages/LoginPage";
import RegisterPage from "../pages/RegisterPage";
import HomePage from "../pages/HomePage";
import NotFound from "../pages/NotFound";
import PrivateRoute from "../components/common/PrivateRoute";
import MainLayout from "../components/layouts/MainLayout";

const AppRoutes = () => {
  return (
    <Routes>
      {/* Định nghĩa các Route */}
      <Route path="/login" element={<LoginPage />} />
      <Route path="/register" element={<RegisterPage />} />

      <Route element={<PrivateRoute />}>
        <Route element={<MainLayout />}>
          <Route path="/" element={<HomePage />} />
        </Route>
      </Route>

      {/* Route cho các đường dẫn không tồn tại (404) */}
      <Route path="*" element={<NotFound />} />
    </Routes>
  );
};

export default AppRoutes;
