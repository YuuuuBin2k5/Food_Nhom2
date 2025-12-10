import React from "react";
import { Outlet } from "react-router-dom";

const MainLayout = () => {
  return (
    <div className="app-container min-h-screen relative">
      {/* Main */}
      <main className="pt-12 min-h-screen transition-colors duration-700 w-full">
        {/* Component AppRoutes quản lý tất cả các Routes */}
        <Outlet />
      </main>
    </div>
  );
};

export default MainLayout;
